package com.prohitman.croakermod.core;

import com.prohitman.croakermod.CroakerMod;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CroakerMod.MODID);

    public static final RegistryObject<Item> CROAKER_SPAWN_EGG = ITEMS.register("croaker_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.CROAKER, 0x46534D, 0x4B8152, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
}
