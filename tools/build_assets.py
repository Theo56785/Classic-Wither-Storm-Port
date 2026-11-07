"""Converte o addon Bedrock V2 (Wither_Storm_Remake) para assets Java/GeckoLib 4. Uso: python3 tools/build_assets.py <pasta_do_addon_extraido>"""
import json, re, os, sys, shutil, subprocess
import numpy as np
from PIL import Image

ADDON = sys.argv[1] if len(sys.argv) > 1 else "/home/claude/addon2"
SRC = f"{ADDON}/SW witherstorm R"
OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "common", "src", "main", "resources")
A = f"{OUT}/assets/witherstormport"
for d in ["geo/entity","animations/entity","textures/entity","textures/item","sounds","lang","models/item"]:
    os.makedirs(f"{A}/{d}", exist_ok=True)

def load(p):
    s = open(p, encoding="utf8").read()
    s = re.sub(r'/\*.*?\*/', '', s, flags=re.S)
    s = re.sub(r'(?m)^\s*//.*$', '', s)
    return json.loads(s)

GEO_BONES = {}
def convert(geo_key, src_json, out_name, split_self=()):
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
        if b["name"] in split_self and cubes:      # part_visibility do Bedrock nao cascateia aos filhos
            bones.append(nb)
            bones.append({"name": b["name"] + "_self", "parent": b["name"], "pivot": nb["pivot"], "cubes": cubes})
            continue
        if cubes: nb["cubes"] = cubes
        bones.append(nb)
    GEO_BONES[out_name] = {b["name"] for b in bones}
    desc = {"identifier": "geometry." + out_name, "texture_width": d["texturewidth"], "texture_height": d["textureheight"],
            "visible_bounds_width": d.get("visible_bounds_width", 4), "visible_bounds_height": d.get("visible_bounds_height", 4),
            "visible_bounds_offset": d.get("visible_bounds_offset", [0,1,0])}
    json.dump({"format_version": "1.12.0", "minecraft:geometry": [{"description": desc, "bones": bones}]},
              open(f"{A}/geo/entity/{out_name}.geo.json", "w"), indent=1)

convert("geometry.wither_command", "models/entity/wither_command.geo.json", "wither_storm_stage1", ("head1",))
convert("geometry.wither_command.tractor_beam", "models/entity/wither_command.geo.json", "wither_storm_stage1_beam")
convert("geometry.witherstorm", "models/entity/witherstorm.geo.json", "wither_storm")
convert("geometry.witherstorm.tractor_beam", "models/entity/witherstorm.geo.json", "wither_storm_beam")
convert("geometry.bigbrain_witherstorm", "models/entity/bigbrain_witherstorm.geo.json", "bigbrain_storm")
convert("geometry.bigbrain_witherstorm.tractor_beam", "models/entity/bigbrain_witherstorm.geo.json", "bigbrain_storm_beam")
convert("geometry.severed_witherstorm", "models/entity/severed_witherstorm.geo.json", "severed_storm")
convert("geometry.severed_witherstorm.tractor_beam", "models/entity/severed_witherstorm.geo.json", "severed_storm_beam")
convert("geometry.witherstorm_dead", "models/entity/ws_dead.geo.json", "witherstorm_dead")
convert("geometry.witherstorm_skull", "models/entity/witherstorm_skull.geo.json", "witherstorm_skull")
for i in range(1,6):
    convert(f"geometry.witherstorm.tentacles{i}", f"models/entity/tentacles/{i}.json", f"wither_storm_tentacles{i}")
    convert(f"geometry.severed_witherstorm.tentacles{i}", f"models/entity/tentacles/s{i}.json", f"severed_storm_tentacles{i}")
for i in range(1,7):
    convert(f"geometry.bigbrain_witherstorm.tentacles{i}", f"models/entity/tentacles/bb{i}.json", f"bigbrain_storm_tentacles{i}")
print("geometrias:", len(GEO_BONES))

# ---------- Animacoes: so keyframes numericos (Molang vira codigo Java) ----------
def kf(v):
    if isinstance(v, dict):
        out = {}
        for t, val in v.items():
            if isinstance(val, dict): val = val.get("post", val.get("pre"))
            if isinstance(val, list) and all(isinstance(x, (int, float)) for x in val): out[t] = val
        return out
    if isinstance(v, list) and all(isinstance(x, (int, float)) for x in v): return {"0.0": v}
    return None

def convert_anim(src_file, anim_key, out_key, geo, out_file, loop):
    a = load(f"{SRC}/animations/{src_file}")["animations"][anim_key]
    lower = {n.lower(): n for n in GEO_BONES[geo]}
    bones, miss = {}, []
    for bname, ch in a.get("bones", {}).items():
        real = bname if bname in GEO_BONES[geo] else lower.get(bname.lower())
        if not real: miss.append(bname); continue
        o = {}
        for k in ("rotation", "position"):          # 'scale' era so gating por flag (feito em codigo)
            if k in ch and (r := kf(ch[k])): o[k] = r
        if o: bones[real] = o
    return out_key, {"loop": loop, "animation_length": a.get("animation_length", 1.0), "bones": bones}, miss

def write_anims(name, items):
    anims, allmiss = {}, []
    for it in items:
        k, v, miss = convert_anim(*it); anims[k] = v; allmiss += miss
    json.dump({"format_version": "1.8.0", "animations": anims}, open(f"{A}/animations/entity/{name}.animation.json", "w"), indent=1)
    print("anim", name, list(anims), "ossos sem correspondencia:", allmiss)

write_anims("wither_storm_stage1", [
    ("wither_storm.block1.animation.json", "animation.wither_storm.block1", "animation.wither_storm.block1", "wither_storm_stage1", None, True),
    ("spawn.animation.json", "animation.wither_command.spawn", "animation.wither_command.spawn", "wither_storm_stage1", None, False)])
write_anims("wither_storm",   [("witherstorm.block2.animation.json", "animation.wither_storm.block2", "animation.wither_storm.block2", "wither_storm", None, True)])
write_anims("severed_storm",  [("witherstorm.block3.animation.json", "animation.wither_storm.block3", "animation.wither_storm.block3", "severed_storm", None, True)])
write_anims("bigbrain_storm", [("witherstorm.block4.animation.json", "animation.wither_storm.block4", "animation.wither_storm.block4", "bigbrain_storm", None, True)])
write_anims("witherstorm_dead", [("bone.animation.json", "animation.witherstorm_dead.bone", "animation.witherstorm_dead.bone", "witherstorm_dead", None, False)])
json.dump({"format_version": "1.8.0", "animations": {}}, open(f"{A}/animations/entity/empty.animation.json", "w"))

# ---------- Texturas ----------
def split_glow(src, base, glow, ea):
    im = np.array(Image.open(src).convert("RGBA")); m = (im[..., 3] == ea)
    g = np.zeros_like(im); g[m] = im[m]; g[m, 3] = 255
    b = im.copy(); b[m, 3] = 255
    Image.fromarray(b).save(base); Image.fromarray(g).save(glow)
T = f"{A}/textures/entity"
split_glow(f"{SRC}/entity/wither_storm.png", f"{T}/wither_storm.png", f"{T}/wither_storm_glowmask.png", 37)
split_glow(f"{SRC}/entity/ws_dead.png", f"{T}/ws_dead.png", f"{T}/ws_dead_glowmask.png", 37)
split_glow(f"{SRC}/entity/witherstorm_skull.png", f"{T}/witherstorm_skull.png", f"{T}/witherstorm_skull_glowmask.png", 63)
for n in ("grass_block", "soil_block"): shutil.copy(f"{SRC}/entity/{n}.png", f"{T}/{n}.png")
shutil.copy(f"{SRC}/effect/tractor_beam.png", f"{T}/tractor_beam.png")
up = "/mnt/user-data/uploads/witherstorm_netherstar.png"
shutil.copy(up if os.path.exists(up) else f"{SRC}/textures/items/witherstorm_netherstar.png", f"{A}/textures/item/witherstorm_netherstar.png")
shutil.copy(f"{SRC}/textures/items/ultimate_sword.png", f"{A}/textures/item/ultimate_sword.png")
Image.open(f"{SRC}/entity/wither_storm_icon.png").convert("RGBA").resize((64, 64), Image.LANCZOS).save(f"{A}/textures/item/wither_storm_egg.png")

# ---------- Sons (estereo -> mono quando o som e posicional) ----------
def channels(p):
    return int(subprocess.run(["ffprobe","-v","error","-show_entries","stream=channels","-of","csv=p=0",p], capture_output=True, text=True).stdout.strip() or 1)
for f in sorted(os.listdir(f"{SRC}/sounds")):
    if not f.endswith(".ogg"): continue
    s, d = f"{SRC}/sounds/{f}", f"{A}/sounds/{f}"
    positional = not f.startswith(("ws_music", "ws_spawn", "sws_head"))
    if positional and channels(s) > 1:
        subprocess.run(["ffmpeg","-y","-loglevel","error","-i",s,"-ac","1","-c:a","libvorbis","-q:a","5",d], check=True)
    else: shutil.copy(s, d)
print("sons:", len(os.listdir(f"{A}/sounds")))
