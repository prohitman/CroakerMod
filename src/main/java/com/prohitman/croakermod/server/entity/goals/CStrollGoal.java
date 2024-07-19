package com.prohitman.croakermod.server.entity.goals;

import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;

public class CStrollGoal extends WaterAvoidingRandomStrollGoal {
    private final CroakerEntity mob;
    public CStrollGoal(CroakerEntity pMob, double pSpeedModifier, float pProbability) {
        super(pMob, pSpeedModifier, pProbability);
        this.mob = pMob;
    }

    @Override
    public boolean canUse() {
        if(this.mob.getIsBusy()){
            return false;
        }
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if(this.mob.getIsBusy()){
            return false;
        }
        return super.canContinueToUse();
    }
}
