package com.prohitman.croakermod.climbing.common.entity.movement;

import com.prohitman.croakermod.climbing.common.entity.mob.IClimberEntity;
import com.prohitman.croakermod.server.entity.AbstractClimberMob;
import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ClimberLookController<T extends Mob & IClimberEntity> extends LookControl {
	protected final IClimberEntity climber;

	public ClimberLookController(T entity) {
		super(entity);
		this.climber = entity;
	}

	@Override
	protected @NotNull Optional<Float> getXRotD() {
		double d0 = this.wantedX - this.mob.getX();
		double d1 = this.wantedY - this.mob.getEyeY();
		double d2 = this.wantedZ - this.mob.getZ();

		double d3 = Math.sqrt(d0 * d0 + d2 * d2);

		Vec3 dir = new Vec3(d0, d1, d2);

		return !(Math.abs(d1) > (double)1.0E-5F) && !(Math.abs(d3) > (double)1.0E-5F) ? Optional.empty() : Optional.of(this.climber.getOrientation().getLocalRotation(dir).getRight());
	}

	@Override
	protected @NotNull Optional<Float> getYRotD() {
		double d0 = this.wantedX - this.mob.getX();
		double d1 = this.wantedZ - this.mob.getZ();

		Vec3 dir = new Vec3(d0, this.wantedY - this.mob.getEyeY(), d1);

		return !(Math.abs(d1) > (double)1.0E-5F) && !(Math.abs(d0) > (double)1.0E-5F) ? Optional.empty() : Optional.of(this.climber.getOrientation().getLocalRotation(dir).getLeft());
	}
}
