package com.prohitman.croakermod.core;

import com.prohitman.croakermod.CroakerMod;
import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CroakerMod.MODID);

    public static final RegistryObject<SoundEvent> CROAKER_AMBIENT = registerSoundEvents("croaker_ambient");
    public static final RegistryObject<SoundEvent> CROAKER_HURT = registerSoundEvents("croaker_hurt");
    public static final RegistryObject<SoundEvent> CROAKER_DEATH = registerSoundEvents("croaker_death");


    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(CroakerMod.MODID, name)));
    }
}
