package com.prohitman.croakermod.server.entity;

import com.prohitman.croakermod.server.entity.goals.*;
import jdk.jfr.Enabled;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class CroakerEntity extends PathfinderMob implements Enemy, IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private static final EntityDataAccessor<Boolean> BUSY = SynchedEntityData.defineId(CroakerEntity.class, EntityDataSerializers.BOOLEAN);

    public int jumpCooldown = 0;
    public CroakerEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.maxUpStep = 1f;
        this.xpReward = 25;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new CRandomLookAroundGoal(this));
        this.goalSelector.addGoal(1, new CStrollGoal(this, 1.0D, 0.25f));
       // this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1d, true));
        this.goalSelector.addGoal(3, new CPounceGoal(this));

        this.goalSelector.addGoal(8, new CLookAtPlayerGoal(this, Player.class, 50, 0.01f));
        this.goalSelector.addGoal(8, new FollowPlayerGoal(this, 0.85d, 10, 35));

        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 100D)
                .add(Attributes.MOVEMENT_SPEED, (double)0.3F)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.ARMOR, 5.0D)
                .add(Attributes.MAX_HEALTH, 50.0D);
    }

    public boolean getIsBusy(){
        return entityData.get(BUSY);
    }

    public void setBusy(boolean is_busy){
        entityData.set(BUSY, is_busy);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BUSY, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("busy", this.getIsBusy());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setBusy(pCompound.getBoolean("busy"));
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getTarget() != null){
            //this.setBusy(true);
        }
        if(!this.level.isClientSide){
            if(jumpCooldown != 0){
                jumpCooldown--;
            }
        }
    }

    /*
    ATTACKING
     */

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        if(pEntity instanceof Player player){
            if(player.getUseItem().is(Items.SHIELD) && player.isUsingItem()){
                player.getCooldowns().addCooldown(player.getUseItem().getItem(), 100);
                player.stopUsingItem();
                player.level.broadcastEntityEvent(player, (byte)30);

                return false;
            }
        }
        return super.doHurtTarget(pEntity);
    }

    /*
    STRAFING
     */

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(pSource.getDirectEntity() instanceof Player player && this.isOnGround() && this.random.nextInt(2) == 0){
            boolean left;
            Vec3 arrowPos = player.position();
            Vec3 rightVector = this.getLookAngle().yRot(0.5F * (float) Math.PI).add(this.position());
            Vec3 leftVector = this.getLookAngle().yRot(-0.5F * (float) Math.PI).add(this.position());
            if (arrowPos.distanceTo(rightVector) < arrowPos.distanceTo(leftVector)) {
                left = false;
            } else if (arrowPos.distanceTo(rightVector) > arrowPos.distanceTo(leftVector)) {
                left = true;
            } else {
                left = this.getRandom().nextBoolean();
            }
            Vec3 vector3d2 = player.getLookAngle().yRot((float) ((left ? -0.5F : 0.5F) * Math.PI)).normalize();
            //emu.setAnimation(left ? EntityEmu.ANIMATION_DODGE_LEFT : EntityEmu.ANIMATION_DODGE_RIGHT);
            this.hasImpulse = true;
            if (!this.horizontalCollision) {
                this.move(MoverType.SELF, new Vec3(vector3d2.x() * 0.75F, 0.2F, vector3d2.z() * 0.75F));
            }

            this.setDeltaMovement(this.getDeltaMovement().add(vector3d2.x() * 1F, 0.45F, vector3d2.z() * 1F));
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    /*
    JUMPING
     */

    public static boolean isPathClear(CroakerEntity pFox, LivingEntity pLivingEntity) {
        double d0 = pLivingEntity.getZ() - pFox.getZ();
        double d1 = pLivingEntity.getX() - pFox.getX();
        double d2 = d0 / d1;
        int i = 6;

        for(int j = 0; j < 7; ++j) {
            double d3 = d2 == 0.0D ? 0.0D : d0 * (double)((float)j / 6.0F);
            double d4 = d2 == 0.0D ? d1 * (double)((float)j / 6.0F) : d3 / d2;

            for(int k = 1; k < 5; ++k) {
                if (!pFox.level.getBlockState(new BlockPos(pFox.getX() + d4, pFox.getY() + (double)k, pFox.getZ() + d3)).getMaterial().isReplaceable()) {
                    return false;
                }
            }
        }

        return true;
    }


    @Override
    public void registerControllers(AnimationData animationData) {

    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.FROG_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.FROG_DEATH;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.FROG_HURT;
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    public boolean canBeLeashed(Player pPlayer) {
        return false;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
