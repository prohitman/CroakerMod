package com.prohitman.croakermod.client.model;

import com.prohitman.croakermod.CroakerMod;
import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.client.model.FrogModel;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.frog.Frog;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class CroakerModel extends AnimatedGeoModel<CroakerEntity> {
    private float headTilt = 0;
    @Override
    public ResourceLocation getModelResource(CroakerEntity object) {
        return new ResourceLocation(CroakerMod.MODID, "geo/croaker.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CroakerEntity object) {
        return new ResourceLocation(CroakerMod.MODID, "textures/entity/croaker.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CroakerEntity animatable) {
        return new ResourceLocation(CroakerMod.MODID, "animations/croaker.animation.json");
    }

    @Override
    public void setCustomAnimations(CroakerEntity animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);

/*        IBone head = this.getAnimationProcessor().getBone("Head");
        IBone neck1 = this.getAnimationProcessor().getBone("Neck1");
        IBone body = this.getAnimationProcessor().getBone("Body");

        EntityModelData extraData = (EntityModelData) animationEvent.getExtraDataOfType(EntityModelData.class).get(0);

        if (head != null) {
            float headYaw = extraData.netHeadYaw;
            float headPitch = extraData.headPitch;

            // Determine the target pitch when climbing or not climbing
            float targetPitch = animatable.isClimbing() ? 45.0F : 0.0F;

            // Interpolate the pitch angle smoothly over time
            float currentPitch = head.getRotationX() * (180F / (float) Math.PI); // Convert radians to degrees
            headTilt = Mth.lerp(animationEvent.getPartialTick(), headTilt, targetPitch); // Use a fixed lerp factor for smooth transition
            if(headTilt != 0){
                System.out.println(headTilt);
            }

            // Apply the interpolated rotation to the head and neck
            head.setRotationY((headYaw / 2) * ((float) Math.PI / 180F)); // Converting degrees to radians
            head.setRotationX((headPitch + headTilt / 2) * ((float) Math.PI / 180F)); // Converting degrees to radians

            if (neck1 != null) {
                neck1.setRotationY(headYaw * ((float) Math.PI / 180F)); // Converting degrees to radians
                neck1.setRotationX(headPitch + headTilt * ((float) Math.PI / 180F)); // Converting degrees to radians
            }

        }*/

        IBone head = this.getAnimationProcessor().getBone("Head");
        IBone neck1 = this.getAnimationProcessor().getBone("Neck1");
        IBone body = this.getAnimationProcessor().getBone("Body");

        if (head != null) {
            EntityModelData extraData = (EntityModelData) animationEvent.getExtraDataOfType(EntityModelData.class).get(0);

            head.setRotationY(((extraData.netHeadYaw / 2)) * ((float)Math.PI / 270F));
            head.setRotationX((((extraData.headPitch) / 2)) * ((float)Math.PI / 270F));
            if(neck1 != null){
                neck1.setRotationY(((extraData.netHeadYaw)) * ((float)Math.PI / 270F));
                neck1.setRotationX(((extraData.headPitch)) * ((float)Math.PI / 270F));
            }
        }
    }
}
