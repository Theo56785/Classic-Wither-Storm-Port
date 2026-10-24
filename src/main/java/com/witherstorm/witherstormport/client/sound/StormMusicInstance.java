package com.witherstorm.witherstormport.client.sound;

import com.witherstorm.witherstormport.registry.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

/** Musica (mob.ws.music) em loop enquanto a forma final estiver viva e carregada no cliente. */
public class StormMusicInstance extends AbstractTickableSoundInstance {
    private static StormMusicInstance current;
    private final Entity source;

    private StormMusicInstance(Entity source) {
        super(ModSounds.WS_MUSIC.get(), SoundSource.MUSIC, RandomSource.create());
        this.source = source;
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0F;
        this.attenuation = SoundInstance.Attenuation.NONE;
        this.x = source.getX();
        this.y = source.getY();
        this.z = source.getZ();
    }

    public static void startFor(Entity storm) {
        if (current != null && !current.isStopped() && current.source.isAlive()) return;
        current = new StormMusicInstance(storm);
        Minecraft.getInstance().getSoundManager().play(current);
    }

    @Override
    public void tick() {
        if (!source.isAlive()) {   // morreu, foi descarregada ou removida do mundo
            this.stop();
            return;
        }
        this.x = source.getX();
        this.y = source.getY();
        this.z = source.getZ();
    }
}
