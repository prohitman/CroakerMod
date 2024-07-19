package com.prohitman.croakermod.core;

import com.prohitman.croakermod.CroakerMod;
import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CroakerMod.MODID);

    public static final RegistryObject<EntityType<CroakerEntity>> CROAKER = ENTITY_TYPES.register("croaker", () -> EntityType.Builder.of(CroakerEntity::new, MobCategory.MONSTER).sized(2.25F, 2F).clientTrackingRange(20).build("croaker"));
}
