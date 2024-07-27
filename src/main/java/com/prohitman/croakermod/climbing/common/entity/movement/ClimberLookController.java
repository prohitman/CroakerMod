package com.prohitman.croakermod.climbing.common.entity.movement;

import com.prohitman.croakermod.climbing.common.entity.mob.IClimberEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class ClimberLookController<T extends Mob & IClimberEntity> extends LookControl {
	protected final IClimberEntity climber;

	public ClimberLookController(T entity) {
		super(entity);
		this.climber = entity;
	}

	@Override
	protected Optional<Float> getXRotD() {
		Vec3 dir = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getEyeY(), this.wantedZ - this.mob.getZ());
		return Optional.of(this.climber.getOrientation().getLocalRotation(dir).getRight());
	}

	@Override
	protected Optional<Float> getYRotD() {
		Vec3 dir = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getEyeY(), this.wantedZ - this.mob.getZ());
		return Optional.of(this.climber.getOrientation().getLocalRotation(dir).getLeft());
	}
}
