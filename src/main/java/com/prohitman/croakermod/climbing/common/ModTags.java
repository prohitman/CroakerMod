package com.prohitman.croakermod.climbing.common;

import com.prohitman.croakermod.CroakerMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
	public static final TagKey<Block> NON_CLIMBABLE = BlockTags.create(new ResourceLocation(CroakerMod.MODID, "non_climbable"));
}
