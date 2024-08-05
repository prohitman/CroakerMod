package com.prohitman.croakermod.server.entity.goals;

import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class FollowPlayerGoal extends Goal {
    private final Mob mob;
    private final Predicate<Player> followPredicate;
    @Nullable
    private Player followingPlayer;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private float oldWaterCost;
    private final float areaSize;

    /**
     * Constructs a goal allowing a mob to follow others. The mob must have Ground or Flying navigation.
     */
    public FollowPlayerGoal(Mob pMob, double pSpeedModifier, float pStopDistance, float pAreaSize) {
        this.mob = pMob;
        this.followPredicate = (player) -> {
            return player != null && !player.isSpectator() && !player.isCreative();
        };
        this.speedModifier = pSpeedModifier;
        this.navigation = pMob.getNavigation();
        this.stopDistance = pStopDistance;
        this.areaSize = pAreaSize;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(pMob.getNavigation() instanceof GroundPathNavigation) && !(pMob.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
        }
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        Player player = this.mob.level.getNearestPlayer(mob, areaSize);
        if(player != null && !player.isInvisible() && !player.isSpectator() && !player.isCreative()){
            this.followingPlayer = player;
            return true;
        }

        return false;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        return this.followingPlayer != null && !this.navigation.isDone() && this.mob.distanceToSqr(this.followingPlayer) > (double)(this.stopDistance * this.stopDistance);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.mob.getPathfindingMalus(BlockPathTypes.WATER);
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        ((CroakerEntity)this.mob).setBusy(true);
        ((CroakerEntity)this.mob).setStalking(true);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        this.followingPlayer = null;
        this.navigation.stop();
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        ((CroakerEntity)this.mob).setBusy(false);
        ((CroakerEntity)this.mob).setStalking(false);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        if (this.followingPlayer != null && !this.mob.isLeashed()) {
            this.mob.getLookControl().setLookAt(this.followingPlayer, 10.0F, (float)this.mob.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                double d0 = this.mob.getX() - this.followingPlayer.getX();
                double d1 = this.mob.getY() - this.followingPlayer.getY();
                double d2 = this.mob.getZ() - this.followingPlayer.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (!(d3 <= (double)(this.stopDistance * this.stopDistance))) {
                    this.navigation.moveTo(this.followingPlayer, this.speedModifier);
                } else {
                    this.navigation.stop();
                    //LookControl lookcontrol = this.followingPlayer.();
                    if (d3 <= (double)this.stopDistance /*|| followingPlayer.getLo.getWantedX() == this.mob.getX() && lookcontrol.getWantedY() == this.mob.getY() && lookcontrol.getWantedZ() == this.mob.getZ()*/) {
                        double d4 = this.followingPlayer.getX() - this.mob.getX();
                        double d5 = this.followingPlayer.getZ() - this.mob.getZ();
                        this.navigation.moveTo(this.mob.getX() - d4, this.mob.getY(), this.mob.getZ() - d5, this.speedModifier);
                    }
                }
            }
        }
    }
}
