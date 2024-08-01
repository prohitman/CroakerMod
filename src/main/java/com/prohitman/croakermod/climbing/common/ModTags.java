package com.prohitman.croakermod.climbing.common;

import com.prohitman.croakermod.CroakerMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTags {
	public static final TagKey<Block> NON_CLIMBABLE = BlockTags.create(new ResourceLocation(CroakerMod.MODID, "non_climbable"));
	public static final TagKey<Biome> CROAKER_SPAWNS = createBiomeTags("croaker_spawns");

	private static TagKey<Biome> createBiomeTags(String name)
	{
		return TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(CroakerMod.MODID, name));
	}
}
