package com.prohitman.croakermod.client.model;

import com.prohitman.croakermod.CroakerMod;
import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.client.model.PigModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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
            head.setRotationY((extraData.netHeadYaw / 2) * ((float)Math.PI / 270F));
            head.setRotationX((extraData.headPitch / 2) * ((float)Math.PI / 270F));
            if(neck1 != null){
                neck1.setRotationY((extraData.netHeadYaw) * ((float)Math.PI / 270F));
                neck1.setRotationX((extraData.headPitch) * ((float)Math.PI / 270F));
            }
        }
    }
}
