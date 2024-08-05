package com.prohitman.croakermod.server.entity.goals;

import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class CStrollGoal extends WaterAvoidingRandomStrollGoal {
    private final CroakerEntity mob;
    public CStrollGoal(CroakerEntity pMob, double pSpeedModifier, float pProbability) {
        super(pMob, pSpeedModifier, pProbability);
        this.mob = pMob;
    }

    @Override
    public boolean canUse() {
        if(this.mob.getIsBusy() || this.mob.getTarget() != null || this.mob.getIsCroaking()){
            return false;
        }
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if(this.mob.getIsBusy() || this.mob.getTarget() != null || this.mob.getIsCroaking()){
            return false;
        }
        return super.canContinueToUse();
    }

    @Nullable
    protected Vec3 getPosition() {
        if (this.mob.isInWaterOrBubble()) {
            Vec3 vec3 = ClimbRandomPos.getPos(this.mob, 15, 15);
            return vec3 == null ? super.getPosition() : vec3;
        } else {
            return this.mob.getRandom().nextFloat() >= this.probability ? ClimbRandomPos.getPos(this.mob, 10, 10) : super.getPosition();
        }
    }
}
