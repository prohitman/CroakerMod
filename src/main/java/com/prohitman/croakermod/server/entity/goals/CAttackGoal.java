package com.prohitman.croakermod.server.entity.goals;

import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class CAttackGoal extends MeleeAttackGoal {
    private final CroakerEntity mob;
    public CAttackGoal(CroakerEntity pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        this.mob = pMob;
    }

    @Override
    public boolean canUse() {
/*        if(this.mob != null && this.mob.jumpCooldown == 0){
            return false;
        }*/
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
/*        if(this.mob != null && this.mob.jumpCooldown == 0){
            return false;
        }*/
        return super.canContinueToUse();
    }

}
