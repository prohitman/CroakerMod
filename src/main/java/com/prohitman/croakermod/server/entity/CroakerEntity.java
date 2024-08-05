package com.prohitman.croakermod.server.entity;

import com.prohitman.croakermod.server.entity.goals.*;
import jdk.jfr.Enabled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;
import software.bernie.shadowed.eliotlash.mclib.math.functions.limit.Min;

import java.util.Optional;

public class CroakerEntity extends AbstractClimberMob implements Enemy, IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private static final EntityDataAccessor<Boolean> BUSY = SynchedEntityData.defineId(CroakerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> POUNCING = SynchedEntityData.defineId(CroakerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STALKING = SynchedEntityData.defineId(CroakerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CROAKING = SynchedEntityData.defineId(CroakerEntity.class, EntityDataSerializers.BOOLEAN);

    public int jumpCooldown = 0;
    public boolean shouldTickPounce = false;
    private boolean loadedCroakSoundInstance = false;
    private CroakingSoundInstance croakingSound;

    public CroakerEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.xpReward = 25;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new CRandomLookAroundGoal(this));
        this.goalSelector.addGoal(1, new CStrollGoal(this, 1.0D, 0.001F));
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new CAttackGoal(this, 1.3d, true));
        this.goalSelector.addGoal(3, new CroakingGoal(this));
        this.goalSelector.addGoal(6, new CLookAtPlayerGoal(this, Player.class, 50, 0.01f));
        this.goalSelector.addGoal(6, new FollowPlayerGoal(this, 0.8d, 15, 35));

        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 100)
                .add(Attributes.MOVEMENT_SPEED, (double)0.3F)
                .add(Attributes.ATTACK_DAMAGE, 0.001D)
                .add(Attributes.ARMOR, 5.0D)
                .add(Attributes.MAX_HEALTH, 5.0D);
    }

    public static boolean checkCroakerSpawnRules(EntityType<? extends PathfinderMob> pAnimal, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return pLevel.getBlockState(pPos.below()).is(BlockTags.FROGS_SPAWNABLE_ON) && Monster.isDarkEnoughToSpawn(pLevel, pPos, pRandom);
    }

    public boolean getIsBusy(){
        return entityData.get(BUSY);
    }
    public void setBusy(boolean is_busy){
        entityData.set(BUSY, is_busy);
    }
    public boolean getIsPouncing(){
        return entityData.get(POUNCING);
    }
    public void setPouncing(boolean is_pouncing){
        entityData.set(POUNCING, is_pouncing);
    }
    public boolean getIsStalking(){
        return entityData.get(STALKING);
    }
    public void setStalking(boolean is_stalking){
        entityData.set(STALKING, is_stalking);
    }
    public boolean getIsCroaking(){
        return entityData.get(CROAKING);
    }
    public void setCroaking(boolean is_croaking){
        entityData.set(CROAKING, is_croaking);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BUSY, false);
        this.entityData.define(POUNCING, false);
        this.entityData.define(STALKING, false);
        this.entityData.define(CROAKING, false);

    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("busy", this.getIsBusy());
        pCompound.putBoolean("pouncing", this.getIsPouncing());
        pCompound.putBoolean("stalking", this.getIsStalking());
        pCompound.putBoolean("croaking", this.getIsCroaking());
        pCompound.putBoolean("soundLoaded", this.loadedCroakSoundInstance);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setBusy(pCompound.getBoolean("busy"));
        this.setPouncing(pCompound.getBoolean("pouncing"));
        this.setStalking(pCompound.getBoolean("stalking"));
        this.setCroaking(pCompound.getBoolean("croaking"));
        this.loadedCroakSoundInstance = pCompound.getBoolean("soundLoaded");
    }

    @Override
    public void tick() {
        super.tick();

        if(!this.level.isClientSide){
            if(jumpCooldown != 0){
               jumpCooldown--;
            }

            Player player = this.getLevel().getNearestPlayer(this.getX(), this.getY(), this.getZ(), 50, true);
            if(player != null){
                this.setBusy(true);
                if(this.tickCount % 20 == 0){
                    System.out.println("Distance from player: " + this.distanceTo(player));
                }
                if(this.distanceTo(player) <= 15){
                    this.setTarget(player);
                }
            }

            if(this.getHealth() < 0.3 * this.getMaxHealth() && this.getTarget() != null && this.getRandom().nextBoolean()){
                double distance = this.distanceTo(this.getTarget());
                if(distance <= 4){
                    Vec3 vec3 = DefaultRandomPos.getPosAway(this, 16, 7, this.getTarget().position());

                    if(vec3 != null){
                        Path path = this.getNavigation().createPath(vec3.x, vec3.y, vec3.z, 0);
                        if(path != null){
                            //this.setTarget(null);
                            this.getNavigation().moveTo(path, 1.2);
                        }
                    }
                }
            }

            if(this.canPounce() && !this.shouldTickPounce){
                this.startPounce();
                this.shouldTickPounce = true;
            }
            if(this.shouldTickPounce){
                if(this.canContinuePounce()){
                    this.pounceTick();
                } else {
                    this.pounceStop();
                    this.shouldTickPounce = false;
                }
            }
        } else {
            if(this.getIsCroaking() && !this.loadedCroakSoundInstance){
                croakingSound = new CroakingSoundInstance(this);
                Minecraft.getInstance().getSoundManager().play(croakingSound);
                this.loadedCroakSoundInstance = true;
            } else if(!this.getIsCroaking() && this.loadedCroakSoundInstance){
                Minecraft.getInstance().getSoundManager().stop(croakingSound);
                this.loadedCroakSoundInstance = false;
            }
        }
    }

    @Override
    public int getMaxHeadYRot() {
        return 60;
    }//75

    @Override
    public int getMaxHeadXRot() {
        return 30;
    }//40

    @Override
    public int getHeadRotSpeed() {
        return 10;
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

    public boolean canPounce(){
        if(this.jumpCooldown != 0){
            return false;
        }
        if(!this.isOnGround()){
            return false;
        }

        Player livingentity = (Player) this.getTarget();
        if(livingentity == null){
        }
        if (livingentity != null && livingentity.isAlive()) {
            double distance = this.distanceTo(livingentity);
            if(distance <= 2 || distance >= 15){
                return false;
            }
            if (livingentity.getMotionDirection() != livingentity.getDirection()) {
                return false;
            } else {
                boolean flag = CroakerEntity.isPathClear(this, livingentity);
                if (!flag) {
                    this.getNavigation().createPath(livingentity, 0);
                }

                return flag;
            }
        } else {
            return false;
        }
    }

    public boolean canContinuePounce(){
        LivingEntity livingentity = this.getTarget();
        if (livingentity != null && livingentity.isAlive()) {
            double distance = this.distanceTo(livingentity);

            if(distance <= 2 || distance >= 15){
                return false;
            }
            double d0 = this.getDeltaMovement().y;
            return (!(d0 * d0 < (double)0.05F) || !(Math.abs(this.getXRot()) < 15.0F) || !this.isOnGround());
        } else {
            return false;
        }
    }

    public void startPounce() {
        this.setPouncing(true);
        LivingEntity livingentity = this.getTarget();
        if (livingentity != null) {
            this.getLookControl().setLookAt(livingentity, 60.0F, 30.0F);
            Vec3 vec3 = (new Vec3(livingentity.getX() - this.getX(), livingentity.getY() - this.getY(), livingentity.getZ() - this.getZ())).normalize();
            double d0 = Mth.nextDouble(this.getRandom(), 0.15d, 0.3d);
            double d1 = Mth.nextDouble(this.getRandom(), 0, 0.35d);

            this.setDeltaMovement(vec3.x * 0.9D * 1.2 + d0, 1.1D * 1.1 + d1, vec3.z * 0.9D * 1.2 + d0);
        }

        this.getNavigation().stop();
    }

    public void pounceStop() {
        this.jumpCooldown = 250;
        this.setPouncing(false);
    }

    public void pounceTick() {
        LivingEntity livingentity = this.getTarget();
        if (livingentity != null) {
            this.getLookControl().setLookAt(livingentity, 60.0F, 30.0F);
        }

        Vec3 vec3 = this.getDeltaMovement();
        if (vec3.y * vec3.y < (double)0.03F && this.getXRot() != 0.0F) {
            this.setXRot(Mth.rotlerp(this.getXRot(), 0.0F, 0.2F));
        } else {
            double d0 = vec3.horizontalDistance();
            double d1 = Math.signum(-vec3.y) * Math.acos(d0 / vec3.length()) * (double)(180F / (float)Math.PI);
            this.setXRot((float)d1);
        }

        if (livingentity != null && this.distanceTo(livingentity) <= 3.0F) {
            this.doHurtTarget(livingentity);
        }
    }

    public static boolean isPathClear(CroakerEntity pFox, LivingEntity pLivingEntity) {
        double d0 = pLivingEntity.getZ() - pFox.getZ();
        double d1 = pLivingEntity.getX() - pFox.getX();
        double d2 = d0 / d1;
        int i = 6;

        for(int j = 0; j < 12; ++j) {
            double d3 = d2 == 0.0D ? 0.0D : d0 * (double)((float)j / 6.0F);
            double d4 = d2 == 0.0D ? d1 * (double)((float)j / 6.0F) : d3 / d2;

            for(int k = 1; k < 8; ++k) {
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
    public double getFluidJumpThreshold() {
        return 1.25;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }


    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if(this.getIsCroaking()){
            return super.getAmbientSound();
        }
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

    protected void playStepSound(BlockPos pPos, BlockState pState) {
        if(this.random.nextFloat() < 0.3){
            this.playSound(SoundEvents.FROG_STEP, 0.5F, 1.9f);
        }
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
