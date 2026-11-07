package com.witherstorm.witherstormport.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

/** Musica em loop (mob.ws.music / music2 / music3) enquanto a entidade existir (animation.*.bgm). */
public class StormMusicInstance extends AbstractTickableSoundInstance {
    private static StormMusicInstance current;
    private final Entity source;

    private StormMusicInstance(Entity source, SoundEvent event) {
        super(event, SoundSource.MUSIC, RandomSource.create());
        this.source = source;
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0F;
        this.attenuation = SoundInstance.Attenuation.NONE;
        this.x = source.getX();
        this.y = source.getY();
        this.z = source.getZ();
    }

    public static void startFor(Entity storm, SoundEvent event) {
        if (current != null) current.stop();                 // troca de forma = troca de musica
        current = new StormMusicInstance(storm, event);
        Minecraft.getInstance().getSoundManager().play(current);
    }

    @Override
    public void tick() {
        if (!source.isAlive()) {
            this.stop();
            return;
        }
        this.x = source.getX();
        this.y = source.getY();
        this.z = source.getZ();
    }
}
