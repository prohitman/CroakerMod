package com.prohitman.croakermod.core;

import com.mojang.serialization.Codec;
import com.prohitman.croakermod.CroakerMod;
import com.prohitman.croakermod.server.worldgen.AddMobSpawnsBiomeModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBiomeModifiers {

    public static DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, CroakerMod.MODID);

    public static RegistryObject<Codec<AddMobSpawnsBiomeModifier>> ADD_MOB_SPAWNS_CODEC = BIOME_MODIFIER_SERIALIZERS.register("add_mob_spawns", () -> Codec.unit(AddMobSpawnsBiomeModifier::new));
}
