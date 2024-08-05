package com.prohitman.croakermod.server.entity;

import com.prohitman.croakermod.core.ModSounds;
import com.prohitman.croakermod.server.entity.goals.CroakingGoal;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.BeeSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class CroakingSoundInstance extends AbstractTickableSoundInstance {
    private final CroakerEntity croaker;
    protected CroakingSoundInstance(CroakerEntity entity) {
        super(ModSounds.CROAKER_CROAKING.get(), SoundSource.AMBIENT, entity.getRandom());
        this.croaker = entity;
        this.looping = true;
        this.delay = 5;
    }

    @Override
    public void tick() {
        if(!croaker.isRemoved() && croaker.getIsCroaking()){
            this.x = croaker.getX() + 0.5D;
            this.z = croaker.getZ() + 0.5D;
            this.y = croaker.getEyeY();
        } else {
            stop();
        }
    }
}
