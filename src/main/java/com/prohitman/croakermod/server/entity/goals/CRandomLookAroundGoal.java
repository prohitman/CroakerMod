package com.prohitman.croakermod.server.entity.goals;

import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;

import java.util.EnumSet;

public class CRandomLookAroundGoal extends Goal {
    private final CroakerEntity mob;
    private double relX;
    private double relZ;
    private int lookTime;

    public CRandomLookAroundGoal(CroakerEntity pMob) {
        this.mob = pMob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }
    public void start() {
        double d0 = (Math.PI * 2D) * this.mob.getRandom().nextDouble();
        this.relX = Math.cos(d0);
        this.relZ = Math.sin(d0);

        this.lookTime = 20 + this.mob.getRandom().nextInt(20);
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        --this.lookTime;

        this.mob.getLookControl().setLookAt(this.mob.getX() + this.relX, this.mob.getEyeY(), this.mob.getZ() + this.relZ);
    }
    @Override
    public boolean canUse() {
        if(this.mob.getIsBusy()){
            return false;
        }
        return this.mob.getRandom().nextFloat() < 0.02F;
    }

    @Override
    public boolean canContinueToUse() {
        if(this.mob.getIsBusy()){
            return false;
        }
        return this.lookTime >= 0;
    }
}
