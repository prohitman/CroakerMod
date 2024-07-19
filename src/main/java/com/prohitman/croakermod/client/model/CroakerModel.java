package com.prohitman.croakermod.client.model;

import com.prohitman.croakermod.CroakerMod;
import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

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
}
