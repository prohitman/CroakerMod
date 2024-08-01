package com.prohitman.croakermod.server;

import com.prohitman.croakermod.CroakerMod;
import com.prohitman.croakermod.core.ModEntities;
import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CroakerMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.CROAKER.get(), CroakerEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnRules(SpawnPlacementRegisterEvent event){
        event.register(ModEntities.CROAKER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CroakerEntity::checkCroakerSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
    }
}
