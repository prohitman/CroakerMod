package com.prohitman.croakermod.server;

import com.mojang.logging.LogUtils;
import com.prohitman.croakermod.CroakerMod;
import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CroakerMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEvents{
    @SubscribeEvent
    public static void onProjectileHit(ProjectileImpactEvent event) {
        //LogUtils.getLogger().trace(event.getEntity().level.getHeight() +" "+ event.getEntity().level.getMinBuildHeight());
        if (event.getRayTraceResult() instanceof EntityHitResult hitResult
                && hitResult.getEntity() instanceof CroakerEntity croaker && croaker.isOnGround() && croaker.getIsBusy() && !event.getEntity().level.isClientSide) {
            if (event.getEntity() instanceof AbstractArrow arrow) {
                //fixes soft crash with vanilla
                arrow.setPierceLevel((byte) 0);
            }
            //if (emu.getAnimation() != EntityEmu.ANIMATION_DODGE_RIGHT && emu.getAnimation() != EntityEmu.ANIMATION_DODGE_LEFT) {
            boolean left;
            Vec3 arrowPos = event.getEntity().position();
            Vec3 rightVector = croaker.getLookAngle().yRot(0.5F * (float) Math.PI).add(croaker.position());
            Vec3 leftVector = croaker.getLookAngle().yRot(-0.5F * (float) Math.PI).add(croaker.position());
            if (arrowPos.distanceTo(rightVector) < arrowPos.distanceTo(leftVector)) {
                left = false;
            } else if (arrowPos.distanceTo(rightVector) > arrowPos.distanceTo(leftVector)) {
                left = true;
            } else {
                left = croaker.getRandom().nextBoolean();
            }
            Vec3 vector3d2 = event.getEntity().getDeltaMovement().yRot((float) ((left ? -0.5F : 0.5F) * Math.PI)).normalize();
            //emu.setAnimation(left ? EntityEmu.ANIMATION_DODGE_LEFT : EntityEmu.ANIMATION_DODGE_RIGHT);
            croaker.hasImpulse = true;
            if (!croaker.horizontalCollision) {
                croaker.move(MoverType.SELF, new Vec3(vector3d2.x() * 0.75F, 0.2F, vector3d2.z() * 0.75F));
            }
            croaker.setDeltaMovement(croaker.getDeltaMovement().add(vector3d2.x() * 1F, 0.45F, vector3d2.z() * 1F));
            event.setCanceled(true);
            //}
        }
    }
}
