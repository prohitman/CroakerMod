package com.prohitman.croakermod.server.worldgen;

import com.mojang.serialization.Codec;
import com.prohitman.croakermod.climbing.common.Config;
import com.prohitman.croakermod.climbing.common.ModTags;
import com.prohitman.croakermod.core.ModBiomeModifiers;
import com.prohitman.croakermod.core.ModEntities;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public class AddMobSpawnsBiomeModifier implements BiomeModifier {
    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase.equals(Phase.ADD)) {
            addMobSpawn(builder, biome, ModTags.CROAKER_SPAWNS, MobCategory.CREATURE, ModEntities.CROAKER.get(), Config.CROAKER_SPAWN_WEIGHT.get(), Config.CROAKER_MIN_SPAWN_SIZE.get(), Config.CROAKER_MAX_SPAWN_SIZE.get());
        }
    }

    void addMobSpawn(ModifiableBiomeInfo.BiomeInfo.Builder builder, Holder<Biome> biome, TagKey<Biome> tag, MobCategory mobCategory, EntityType<?> entityType, int weight, int minGroupSize, int maxGroupSize) {
        if (biome.is(tag)) {
            builder.getMobSpawnSettings().addSpawn(mobCategory, new MobSpawnSettings.SpawnerData(entityType, weight, minGroupSize, maxGroupSize));
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return ModBiomeModifiers.ADD_MOB_SPAWNS_CODEC.get();
    }
}
