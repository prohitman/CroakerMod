package com.prohitman.croakermod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.prohitman.croakermod.client.model.CroakerModel;
import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.client.renderer.entity.PhantomRenderer;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class CroakerRenderer extends GeoEntityRenderer<CroakerEntity> {
    public CroakerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CroakerModel());
        this.shadowRadius = 1.5f;

        addLayer(new GlowEyesLayer(this));
    }

    @Override
    protected void applyRotations(CroakerEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        if (animatable.getIsPouncing()) {
            //float f = -Mth.lerp(ageInTicks, animatable.xRotO, animatable.getXRot());
            //poseStack.mulPose(Vector3f.XP.rotationDegrees(f));
        }

    }
}
