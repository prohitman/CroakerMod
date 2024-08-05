package com.prohitman.croakermod.server.entity.goals;

import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class CroakingGoal extends Goal {
    private final CroakerEntity entity;
    private int tickCount = 0;
    private int maxTickCount;

    public CroakingGoal(CroakerEntity entity){
        this.entity = entity;
    }
    @Override
    public boolean canUse() {
        if(entity == null){
            return false;
        } else if(this.entity.getIsBusy() || this.entity.getIsPouncing() || this.entity.getIsStalking() || this.entity.getTarget() != null){
            return false;
        }
        this.maxTickCount = this.entity.getRandom().nextInt(30, 80);
        return this.entity.getRandom().nextFloat() < 0.01;
    }

    @Override
    public boolean canContinueToUse() {
        if(tickCount >= this.maxTickCount){
            return false;
        }
        if(this.entity.getIsBusy() || this.entity.getIsPouncing() || this.entity.getIsStalking() || this.entity.getTarget() != null || !this.entity.isOnGround()){
            return false;
        }

        return true;
    }

    @Override
    public void tick() {
        super.tick();
        this.entity.getNavigation().stop();
        if(tickCount < this.maxTickCount){
            this.tickCount++;
        }
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        super.start();
        this.entity.setCroaking(true);
        this.entity.getNavigation().stop();
    }

    @Override
    public void stop() {
        super.stop();
        this.entity.setCroaking(false);
        this.tickCount = 0;
    }
}
