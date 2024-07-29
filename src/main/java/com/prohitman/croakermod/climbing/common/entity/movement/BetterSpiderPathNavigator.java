package com.prohitman.croakermod.climbing.common.entity.movement;

import com.mojang.logging.LogUtils;
import com.prohitman.croakermod.climbing.common.entity.mob.IClimberEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class BetterSpiderPathNavigator<T extends Mob & IClimberEntity> extends AdvancedClimberPathNavigator<T> {
	private boolean useVanillaBehaviour;
	private BlockPos targetPosition;

	public BetterSpiderPathNavigator(T entity, Level worldIn, boolean useVanillaBehaviour) {
		super(entity, worldIn, false, true, true);
		this.useVanillaBehaviour = useVanillaBehaviour;
	}

	@Override
	public Path createPath(BlockPos pos, int checkpointRange) {
		this.targetPosition = pos;
		//System.out.println("Attempting to create path from block pos: " + targetPosition);
		return super.createPath(pos, checkpointRange);
	}

	@Override
	public Path createPath(Entity entityIn, int checkpointRange) {
		this.targetPosition = entityIn.blockPosition();
		return super.createPath(entityIn, checkpointRange);
	}

	@Override
	public boolean moveTo(Entity pEntity, double pSpeed) {
		Path path = this.createPath(pEntity, 0);
		if(path != null) {
			//System.out.println("Called moveTo Entity with position: " + path.getTarget() + pSpeed);
			return this.moveTo(path, pSpeed);
		} else {
			this.targetPosition = pEntity.blockPosition();
			this.speedModifier = pSpeed;
			return true;
		}
	}

	@Override
	public void tick() {
		if(!this.isDone()) {
			//System.out.println("Obtained Position " + targetPosition + ", ticking...");
			super.tick();
		} else {
			if(this.targetPosition != null && this.useVanillaBehaviour) {
				// FORGE: Fix MC-94054
				if(!this.targetPosition.closerToCenterThan(this.mob.position(), Math.max((double) this.mob.getBbWidth(), 1.0D)) && (!(this.mob.getY() > (double) this.targetPosition.getY()) || !(new BlockPos((double) this.targetPosition.getX(), this.mob.getY(), (double) this.targetPosition.getZ())).closerToCenterThan(this.mob.position(), Math.max((double) this.mob.getBbWidth(), 1.0D)))) {
					this.mob.getMoveControl().setWantedPosition((double) this.targetPosition.getX(), (double) this.targetPosition.getY(), (double) this.targetPosition.getZ(), this.speedModifier);
				} else {
					this.targetPosition = null;
				}
			}

		}
	}
}
