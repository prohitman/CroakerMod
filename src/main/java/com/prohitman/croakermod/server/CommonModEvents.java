package com.prohitman.croakermod.server;

import com.prohitman.croakermod.CroakerMod;
import com.prohitman.croakermod.core.ModEntities;
import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CroakerMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.CROAKER.get(), CroakerEntity.createAttributes().build());
    }
}
