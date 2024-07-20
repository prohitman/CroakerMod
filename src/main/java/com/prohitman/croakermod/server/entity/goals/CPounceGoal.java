package com.prohitman.croakermod.server.entity.goals;

import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.client.model.FoxModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.JumpGoal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class CPounceGoal extends JumpGoal {
    private final CroakerEntity croaker;
    public CPounceGoal(CroakerEntity entity){
        this.croaker = entity;
    }
    public boolean canUse() {
        if(croaker.jumpCooldown != 0){
            return false;
        }
        if(!croaker.isOnGround()){
            return false;
        }

        LivingEntity livingentity = croaker.getTarget();
        if (livingentity != null && livingentity.isAlive()) {
            double distance = croaker.distanceTo(livingentity);
            if(distance <= 4 || distance >= 10){
                return false;
            }
            if (livingentity.getMotionDirection() != livingentity.getDirection()) {
                return false;
            } else {
                boolean flag = CroakerEntity.isPathClear(croaker, livingentity);
                if (!flag) {
                    croaker.getNavigation().createPath(livingentity, 0);
                }

                return flag;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
/*        if(!croaker.isOnGround()){
            return false;
        }*/
        LivingEntity livingentity = croaker.getTarget();
        if (livingentity != null && livingentity.isAlive()) {
/*            double distance = croaker.distanceTo(livingentity);
            if(distance <= 2 || distance >= 15){
                return false;
            }*/
            double d0 = croaker.getDeltaMovement().y;
            return (!(d0 * d0 < (double)0.05F) || !(Math.abs(croaker.getXRot()) < 15.0F) || !croaker.isOnGround());
        } else {
            return false;
        }
    }

    public boolean isInterruptable() {
        return false;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        //Fox.this.setJumping(true);
        //Fox.this.setIsPouncing(true);
        //Fox.this.setIsInterested(false);
        LivingEntity livingentity = croaker.getTarget();
        if (livingentity != null) {
            croaker.getLookControl().setLookAt(livingentity, 60.0F, 30.0F);
            Vec3 vec3 = (new Vec3(livingentity.getX() - croaker.getX(), livingentity.getY() - croaker.getY(), livingentity.getZ() - croaker.getZ())).normalize();
            double d0 = Mth.nextDouble(croaker.getRandom(), 0.1d, 0.15d);
            double d1 = Mth.nextDouble(croaker.getRandom(), 0, 0.4d);

            croaker.setDeltaMovement(croaker.getDeltaMovement().add(vec3.x * 0.9D + d0, 1.1D + d1, vec3.z * 0.9D + d0));
        }

        croaker.getNavigation().stop();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        //Fox.this.setIsCrouching(false);
        //Fox.this.crouchAmount = 0.0F;
        //Fox.this.crouchAmountO = 0.0F;
        //Fox.this.setIsInterested(false);
        //Fox.this.setIsPouncing(false);
        croaker.jumpCooldown = 100;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        LivingEntity livingentity = croaker.getTarget();
        if (livingentity != null) {
            croaker.getLookControl().setLookAt(livingentity, 60.0F, 30.0F);
        }

        //if (!croaker.isFaceplanted()) {
            Vec3 vec3 = croaker.getDeltaMovement();
            if (vec3.y * vec3.y < (double)0.03F && croaker.getXRot() != 0.0F) {
                croaker.setXRot(Mth.rotlerp(croaker.getXRot(), 0.0F, 0.2F));
            } else {
                double d0 = vec3.horizontalDistance();
                double d1 = Math.signum(-vec3.y) * Math.acos(d0 / vec3.length()) * (double)(180F / (float)Math.PI);
                croaker.setXRot((float)d1);
            }

        //}

        if (livingentity != null && croaker.distanceTo(livingentity) <= 3.0F) {
            croaker.doHurtTarget(livingentity);
        } /*else if (Fox.this.getXRot() > 0.0F && Fox.this.onGround && (float)Fox.this.getDeltaMovement().y != 0.0F && Fox.this.level.getBlockState(Fox.this.blockPosition()).is(Blocks.SNOW)) {
            Fox.this.setXRot(60.0F);
            Fox.this.setTarget((LivingEntity)null);
            Fox.this.setFaceplanted(true);
        }*/
    }
}
