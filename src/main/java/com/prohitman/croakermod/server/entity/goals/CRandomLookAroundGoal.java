package com.prohitman.croakermod.server.entity.goals;

import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;

public class CRandomLookAroundGoal extends RandomLookAroundGoal {
    private final CroakerEntity mob;
    public CRandomLookAroundGoal(CroakerEntity pMob) {
        super(pMob);
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
