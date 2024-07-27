package com.prohitman.croakermod.climbing.common.entity.mob;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

public interface IEntityMovementHook {
	public boolean onMove(MoverType type, Vec3 pos, boolean pre);

	@Nullable
	public BlockPos getAdjustedOnPosition(BlockPos onPosition);

	public boolean getAdjustedCanTriggerWalking(boolean canTriggerWalking);
}
