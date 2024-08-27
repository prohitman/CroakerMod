package com.prohitman.croakermod.server.entity.goals;

import com.prohitman.croakermod.core.ModSounds;
import com.prohitman.croakermod.server.entity.CroakerEntity;
import com.prohitman.croakermod.server.entity.IAttacking;
import net.minecraft.client.model.FrogModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.animal.frog.Frog;

public class AnimatedMeleeAttackGoal<T extends CroakerEntity & IAttacking> extends MeleeAttackGoal {
    private final T entity;
    private int attackDelay;
    private int attackDuration;
    private int ticksUntilNextAttack;
    private double attackReach;
    private boolean shouldCountTillNextAttack = false;
    private AttackType attackType;

    public AnimatedMeleeAttackGoal(CroakerEntity pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        entity = ((T) pMob);
    }

    @Override
    public void start() {
        super.start();
        this.attackType = AttackType.getTypeWithID(this.entity.getRandom().nextInt(4));

        System.out.println("Atack Type: " + this.attackType.name());

        this.entity.setAttackType(attackType.ordinal());

        this.attackDuration = attackType.getAttackDuration();
        this.attackDelay = attackType.getAttackTick();
        this.attackReach = attackType.getAttackReach();

        ticksUntilNextAttack = attackDuration - attackDelay;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity pEnemy, double pDistToEnemySqr) {
        if (isEnemyWithinAttackDistance(pEnemy, pDistToEnemySqr)) {
            shouldCountTillNextAttack = true;

            if(isTimeToStartAttackAnimation()) {
                entity.setAttacking(true);
            }

            if(isTimeToAttack()) {
                this.mob.getLookControl().setLookAt(pEnemy.getX(), pEnemy.getEyeY(), pEnemy.getZ());
                performAttack(pEnemy);
            }
        } else {
            resetAttackCooldown();
            shouldCountTillNextAttack = false;
            entity.setAttacking(false);
            entity.setAttackAnimationTimeOut(0);
        }
    }

    private boolean isEnemyWithinAttackDistance(LivingEntity pEnemy, double pDistToEnemySqr) {
        return pDistToEnemySqr <= (this.getAttackReachSqr(pEnemy) + attackReach);
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(attackDuration);
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected boolean isTimeToStartAttackAnimation() {
        return this.ticksUntilNextAttack <= attackDelay;
    }

    protected int getTicksUntilNextAttack() {
        return this.ticksUntilNextAttack;
    }

    protected void performAttack(LivingEntity pEnemy) {
        this.resetAttackCooldown();
        this.mob.swing(InteractionHand.MAIN_HAND);
        this.mob.doHurtTarget(pEnemy);
        this.mob.playSound(ModSounds.CROAKER_ATTACK.get(), 1, 1);
        this.attackType = AttackType.getTypeWithID(this.entity.getRandom().nextInt(4));

        System.out.println("Atack Type: " + this.attackType.name());

        this.entity.setAttackType(attackType.ordinal());
    }

    @Override
    public void tick() {
        super.tick();
        if(shouldCountTillNextAttack) {
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        }
    }

    @Override
    public void stop() {
        entity.setAttacking(false);
        super.stop();
    }

    @Override
    protected double getAttackReachSqr(LivingEntity pAttackTarget) {
        return super.getAttackReachSqr(pAttackTarget) + attackReach;
    }
}
