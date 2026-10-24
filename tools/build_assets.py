import json, re, os, shutil, subprocess
import numpy as np
from PIL import Image

SRC = "/home/claude/addon/SW witherstorm R"
OUT = "/home/claude/mod/src/main/resources"
A = f"{OUT}/assets/witherstormport"
for d in ["geo/entity","animations/entity","textures/entity","textures/item","sounds","lang","models/item"]:
    os.makedirs(f"{A}/{d}", exist_ok=True)

def load(p):
    s = open(p, encoding="utf8").read()
    s = re.sub(r'/\*.*?\*/', '', s, flags=re.S)
    s = re.sub(r'(?m)^\s*//.*$', '', s)
    return json.loads(s)

def convert(geo_key, src_json, out_name, ident, split_self=()):
    d = load(f"{SRC}/{src_json}")[geo_key]
    bones = []
    for b in d["bones"]:
        nb = {"name": b["name"], "pivot": b.get("pivot", [0,0,0])}
        if b.get("parent"): nb["parent"] = b["parent"]
        if b.get("rotation"): nb["rotation"] = b["rotation"]
        if b.get("neverRender"): nb["neverRender"] = True
        cubes = []
        for c in b.get("cubes", []):
            nc = {"origin": c["origin"], "size": c["size"], "uv": c["uv"]}
            if c.get("inflate"): nc["inflate"] = c["inflate"]
            if c.get("mirror"): nc["mirror"] = True
            cubes.append(nc)
        if b["name"] in split_self and cubes:
            # Bedrock part_visibility nao cascateia aos filhos; GeckoLib sim.
            # Cubos vao para um osso-filho "<nome>_self" que pode ser escondido isoladamente.
            bones.append(nb)
            bones.append({"name": b["name"] + "_self", "parent": b["name"], "pivot": nb["pivot"], "cubes": cubes})
            continue
        if cubes: nb["cubes"] = cubes
        bones.append(nb)
    desc = {"identifier": ident,
            "texture_width": d["texturewidth"], "texture_height": d["textureheight"],
            "visible_bounds_width": d.get("visible_bounds_width", 4),
            "visible_bounds_height": d.get("visible_bounds_height", 4),
            "visible_bounds_offset": d.get("visible_bounds_offset", [0,1,0])}
    out = {"format_version": "1.12.0", "minecraft:geometry": [{"description": desc, "bones": bones}]}
    json.dump(out, open(f"{A}/geo/entity/{out_name}.geo.json", "w"), indent=1)
    print("geo", out_name, len(bones), "bones")

convert("geometry.wither_command", "models/entity/wither_command.geo.json", "wither_storm_stage1", "geometry.wither_storm_stage1", split_self=("head1",))
convert("geometry.wither_command.tractor_beam", "models/entity/wither_command.geo.json", "wither_storm_stage1_beam", "geometry.wither_storm_stage1_beam")
convert("geometry.witherstorm", "models/entity/witherstorm.geo.json", "wither_storm", "geometry.wither_storm")
convert("geometry.witherstorm.tractor_beam", "models/entity/witherstorm.geo.json", "wither_storm_beam", "geometry.wither_storm_beam")
convert("geometry.witherstorm_skull", "models/entity/witherstorm_skull.geo.json", "witherstorm_skull", "geometry.witherstorm_skull")
for i in range(1,6):
    convert(f"geometry.witherstorm.tentacles{i}", f"models/entity/tentacles/{i}.json", f"wither_storm_tentacles{i}", f"geometry.wither_storm_tentacles{i}")

# ---------- Animacoes GeckoLib (so as que sao puro keyframe) ----------
def anim(path, name, length, bones):
    json.dump({"format_version":"1.8.0","animations":{name:{"loop":True,"animation_length":length,"bones":bones}}}, open(path,"w"), indent=1)
anim(f"{A}/animations/entity/wither_storm_stage1.animation.json","animation.wither_storm.block1",1.04,
     {"bone":{"rotation":{"0.0":[0,0,0],"1.0333":[0,180,0]}},"bone2":{"rotation":{"0.0":[0,0,0],"1.0333":[0,-360,0]}}})
anim(f"{A}/animations/entity/wither_storm.animation.json","animation.wither_storm.block2",1.0,
     {"bone":{"rotation":{"0.0":[0,0,0],"1.0":[0,360,0]}},"bone2":{"rotation":{"0.0":[0,0,0],"1.0":[0,-180,0]}},
      "bone3":{"rotation":{"0.0":[0,0,0],"1.0":[0,90,0]}},"bone4":{"rotation":{"0.0":[0,0,0],"1.0":[0,-90,0]}}})
json.dump({"format_version":"1.8.0","animations":{}}, open(f"{A}/animations/entity/empty.animation.json","w"))

# ---------- Texturas ----------
def split_glow(src, dst_base, dst_glow, emissive_alpha):
    im = np.array(Image.open(src).convert("RGBA"))
    mask = (im[...,3] == emissive_alpha)
    glow = np.zeros_like(im); glow[mask] = im[mask]; glow[mask,3] = 255
    base = im.copy(); base[mask,3] = 255
    Image.fromarray(base).save(dst_base); Image.fromarray(glow).save(dst_glow)
    print("glow", os.path.basename(src), int(mask.sum()), "px")
split_glow(f"{SRC}/entity/wither_storm.png", f"{A}/textures/entity/wither_storm.png", f"{A}/textures/entity/wither_storm_glowmask.png", 37)
split_glow(f"{SRC}/entity/witherstorm_skull.png", f"{A}/textures/entity/witherstorm_skull.png", f"{A}/textures/entity/witherstorm_skull_glowmask.png", 63)
for n in ["grass_block","soil_block"]:
    shutil.copy(f"{SRC}/entity/{n}.png", f"{A}/textures/entity/{n}.png")
shutil.copy(f"{SRC}/effect/tractor_beam.png", f"{A}/textures/entity/tractor_beam.png")
# nether star modificada (a do anexo do usuario tem prioridade; e identica a do pack)
up = "/mnt/user-data/uploads/witherstorm_netherstar.png"
shutil.copy(up if os.path.exists(up) else f"{SRC}/items/witherstorm_netherstar.png", f"{A}/textures/item/witherstorm_netherstar.png")
Image.open(f"{SRC}/entity/wither_storm_icon.png").convert("RGBA").resize((64,64), Image.LANCZOS).save(f"{A}/textures/item/wither_storm_egg.png")

# ---------- Sons: hurt/idle estereo -> mono (so mono atenua por distancia no Java) ----------
for f in sorted(os.listdir(f"{SRC}/sounds")):
    if not f.endswith(".ogg"): continue
    dst = f"{A}/sounds/{f}"
    if f.startswith(("ws_hurt","ws_idle")):
        subprocess.run(["ffmpeg","-y","-loglevel","error","-i",f"{SRC}/sounds/{f}","-ac","1","-c:a","libvorbis","-q:a","5",dst], check=True)
    else:
        shutil.copy(f"{SRC}/sounds/{f}", dst)
print("sons:", len(os.listdir(f"{A}/sounds")))
