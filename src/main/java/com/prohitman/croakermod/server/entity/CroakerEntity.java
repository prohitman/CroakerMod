package com.prohitman.croakermod.server.entity;

import com.prohitman.croakermod.server.entity.goals.*;
import jdk.jfr.Enabled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Optional;

public class CroakerEntity extends PathfinderMob implements Enemy, IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private static final EntityDataAccessor<Boolean> BUSY = SynchedEntityData.defineId(CroakerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> POUNCING = SynchedEntityData.defineId(CroakerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(CroakerEntity.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Boolean> ATTACHED = SynchedEntityData.defineId(CroakerEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<BlockPos> ATTACHED_POS = SynchedEntityData.defineId(CroakerEntity.class, EntityDataSerializers.BLOCK_POS);
    protected static final EntityDataAccessor<Direction> ATTACHED_DIR = SynchedEntityData.defineId(CroakerEntity.class, EntityDataSerializers.DIRECTION);
    public int attachAngle = 0;
    private int attachCooldown;
    private Block sessionAttachPoint;

    public int jumpCooldown = 0;
    public CroakerEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.lookControl = new CroakerLookController();
        this.maxUpStep = 1.25f;
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
    public boolean getIsPouncing(){
        return entityData.get(POUNCING);
    }
    public void setPouncing(boolean is_pouncing){
        entityData.set(POUNCING, is_pouncing);
    }
    public Direction getAttachedDir() {
        return this.entityData.get(ATTACHED_DIR);
    }
    private void setAttachedDir(Direction dir) {
        this.entityData.set(ATTACHED_DIR, dir);
    }

    private BlockPos getAttachedPos() {
        return this.entityData.get(ATTACHED_POS);
    }

    private void setAttachedPos(BlockPos pos) {
        this.entityData.set(ATTACHED_POS, pos);
    }
    public boolean isAttached() {
        return this.entityData.get(ATTACHED) && !this.getAttachedPos().equals(BlockPos.ZERO);
    }
    public void setAttached(boolean attached) {
        this.entityData.set(ATTACHED, attached);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BUSY, false);
        this.entityData.define(POUNCING, false);
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(ATTACHED, false);
        this.entityData.define(ATTACHED_POS, BlockPos.ZERO);
        this.entityData.define(ATTACHED_DIR, Direction.DOWN);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("busy", this.getIsBusy());
        pCompound.putBoolean("pouncing", this.getIsPouncing());
        pCompound.putBoolean("Attached", this.isAttached());
        if (this.getAttachedDir() != null) {
            pCompound.putString("Attached_Dir", this.getAttachedDir().getSerializedName());
        } else {
            pCompound.putString("Attached_Dir", Direction.DOWN.getSerializedName());
        }
        if(this.getAttachedPos() != null) {
            pCompound.putInt("Attached_Pos_X", this.getAttachedPos().getX());
            pCompound.putInt("Attached_Pos_Y", this.getAttachedPos().getY());
            pCompound.putInt("Attached_Pos_Z", this.getAttachedPos().getZ());
        } else {
            pCompound.putInt("Attached_Pos_X", 0);
            pCompound.putInt("Attached_Pos_Y", 0);
            pCompound.putInt("Attached_Pos_Z", 0);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setBusy(pCompound.getBoolean("busy"));
        this.setPouncing(pCompound.getBoolean("pouncing"));
        this.setAttached(pCompound.getBoolean("Attached"));
        this.setAttachedDir(Direction.byName(pCompound.getString("Attached_Dir")));
        this.setAttachedPos(new BlockPos(pCompound.getInt("Attached_Pos_X"), pCompound.getInt("Attached_Pos_Y"), pCompound.getInt("Attached_Pos_Z")));
        if(isAttached()) {
            attachCooldown = 60;
        }
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
            this.setClimbing(this.horizontalCollision);
            if(isClimbing()){
                if(attachAngle < 45){
                    attachAngle++;
                }
                BlockPos currentPos = blockPosition();
                Optional<BlockPos> Opos = BlockPos.betweenClosedStream(currentPos, currentPos.relative(getDirection()))
                        .filter(this::isValidAttachmentBlock)
                        .findFirst();

                if (Opos.isPresent()) {
                    BlockPos pos = Opos.get();
                    if (pos.getY() != getBlockY() || pos.distToCenterSqr(position()) >= 0.5) return;

                    //attachCooldown = getRandom().nextIntBetweenInclusive(120, 200);
                    setAttachedPos(pos);
                    //stopNavigation();
                    setAttachmentDirection(pos);
                    setYRot(getAttachedDir().toYRot() + 45);
                    //setAttached(true);

                }
                if(getAttachedDir() != Direction.DOWN){
                    setXRot(getAttachedDir().toYRot());
                }
            } else if(getAttachedDir() != Direction.DOWN){
                if(attachAngle > 0){
                    attachAngle--;
                }
                this.setAttachedDir(Direction.DOWN);
                this.setAttachedPos(BlockPos.ZERO);
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

    /*
    CLIMBING
     */
    public boolean onClimbable() {
        return this.isClimbing();
    }

    public boolean isClimbing() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setClimbing(boolean pClimbing) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (pClimbing) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 = (byte)(b0 & -2);
        }

        this.entityData.set(DATA_FLAGS_ID, b0);
    }

    /*@Override
    public void aiStep() {
        super.aiStep();

        if (level.isClientSide) return;

        if (getAttachedPos().equals(BlockPos.ZERO)) {
            if (isClimbing()*//* && !this.onGround || minorHorizontalCollision && !this.onGroun*//*) {
                handleHorizontalCollision();
            }
        } else*//* if (isAttached())*//* {
            handleAttachment();
        }

       // adjustMovement();
    }*/

    private void handleHorizontalCollision() {
        BlockPos currentPos = blockPosition();
        Optional<BlockPos> Opos = BlockPos.betweenClosedStream(currentPos, currentPos.relative(getDirection()))
                .filter(this::isValidAttachmentBlock)
                .findFirst();

        if (Opos.isPresent()) {
            BlockPos pos = Opos.get();
            if (pos.getY() != getBlockY() || pos.distToCenterSqr(position()) >= 0.5) return;

            attachCooldown = getRandom().nextIntBetweenInclusive(120, 200);
            setAttachedPos(pos);
            //stopNavigation();
            setAttachmentDirection(pos);
            setYRot(getAttachedDir().toYRot());
            //setAttached(true);
        }
    }

    private void handleVerticalCollision() {
        BlockPos currentPos = blockPosition();
        Optional<BlockPos> Opos = BlockPos.betweenClosedStream(currentPos, currentPos.relative(Direction.UP))
                .filter(this::isValidAttachmentBlock)
                .findFirst();

        if (Opos.isPresent()) {
            BlockPos pos = Opos.get();
            if (pos.getY() - 1 != getBlockY() || pos.distToCenterSqr(position()) >= 0.5) return;

            attachCooldown = getRandom().nextIntBetweenInclusive(200, 600);
            setAttachedPos(pos);
            setAttachedDir(Direction.UP);
            setAttached(true);
        }
    }

    private void handleAttachment() {
        if (sessionAttachPoint != null && level.getBlockState(getAttachedPos()).getBlock() != sessionAttachPoint) {
            resetAttachment();
        } else if (sessionAttachPoint == null) {
            sessionAttachPoint = level.getBlockState(getAttachedPos()).getBlock();
        }

        if (attachCooldown == 0) {
            if (getRandom().nextBoolean()) {
                resetAttachment();
            } else {
                attachCooldown = getRandom().nextIntBetweenInclusive(120, 200);
            }
        } else {
            attachCooldown -= 1;
            adjustRotation();
        }
    }

    private void adjustMovement() {
        Vec3 vec3 = getDeltaMovement();
        if (!onGround && vec3.y < 0.0D) {
            setDeltaMovement(vec3.multiply(isAttached() ? 0.0D : 1D, isAttached() ? 0.0D : 0.4D, isAttached() ? 0.0D : 1D));
        }
    }

    private void resetAttachment() {
        setAttached(false);
        setAttachedPos(BlockPos.ZERO);
        setAttachedDir(Direction.DOWN);
    }

    private void adjustRotation() {
        if (getAttachedDir() != Direction.DOWN && getAttachedDir() != Direction.UP) {
            float dirRot = getAttachedDir().toYRot();
            if (getYRot() != dirRot) setYRot(dirRot);
        }
    }

    private void stopNavigation() {
        getNavigation().stop();
    }

    private void setAttachmentDirection(BlockPos pos) {
        if (pos.getZ() > getBlockZ()) {
            setAttachedDir(Direction.SOUTH);
        } else if (pos.getZ() < getBlockZ()) {
            setAttachedDir(Direction.NORTH);
        } else if (pos.getX() < getBlockX()) {
            setAttachedDir(Direction.WEST);
        } else if (pos.getX() > getBlockX()) {
            setAttachedDir(Direction.EAST);
        }
    }

    private boolean isValidAttachmentBlock(BlockPos blockPos) {
        BlockState state = level.getBlockState(blockPos);
        return !state.isAir() /*&& !state.isStickyBlock() && !state.isRandomlyTicking() && !state.hasBlockEntity() && state.getShape(level, blockPos) == Shapes.block()*/;
    }
    protected PathNavigation createNavigation(Level pLevel) {
        return new WallClimberNavigation(this, pLevel);
    }

    /*
    LOOK/MOVE CONTROLLER
     */
    public class CroakerLookController extends LookControl {
        public CroakerLookController() {
            super(CroakerEntity.this);
        }

        protected boolean resetXRotOnTick() {
            return !CroakerEntity.this.getIsPouncing();
        }
    }

    @Override
    public void registerControllers(AnimationData animationData) {

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
