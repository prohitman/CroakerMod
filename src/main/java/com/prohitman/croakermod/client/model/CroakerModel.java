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

        IBone head = this.getAnimationProcessor().getBone("Head");
        IBone neck1 = this.getAnimationProcessor().getBone("Neck1");

        if (head != null) {
            EntityModelData extraData = (EntityModelData) animationEvent.getExtraDataOfType(EntityModelData.class).get(0);

            float normalizedHeadYaw = extraData.netHeadYaw % 360;
            if (normalizedHeadYaw > 180) {
                normalizedHeadYaw -= 360;
            } else if (normalizedHeadYaw < -180) {
                normalizedHeadYaw += 360;
            }

            float normalizedHeadPitch = extraData.headPitch % 360;
            if(normalizedHeadPitch > 180){
                normalizedHeadPitch -= 180;
            } else if(normalizedHeadPitch < -180){
                normalizedHeadPitch += 360;
            }

            head.setRotationY((((normalizedHeadYaw) / 2)) * ((float)Math.PI / 270));
            head.setRotationX((((normalizedHeadPitch) / 2)) * ((float)Math.PI / 270));

            if(neck1 != null){
                neck1.setRotationY(((normalizedHeadYaw)) * ((float)Math.PI / 270));
                neck1.setRotationX(((normalizedHeadPitch)) * ((float)Math.PI / 270));
            }
        }
    }
}
