package com.prohitman.croakermod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.prohitman.croakermod.CroakerMod;
import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class GlowEyesLayer extends GeoLayerRenderer<CroakerEntity> {
    private static final ResourceLocation texture = new ResourceLocation(CroakerMod.MODID, "textures/entity/croaker_eyes.png");
    private static final RenderType CROAKER_EYES = RenderType.eyes(texture);

    public GlowEyesLayer(IGeoRenderer<CroakerEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, CroakerEntity entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        VertexConsumer vertexconsumer = bufferIn.getBuffer(CROAKER_EYES);

        this.getRenderer().render(this.getEntityModel().getModel(this.getEntityModel().getModelResource(entityLivingBaseIn)), entityLivingBaseIn, 0, this.getRenderType(texture), matrixStackIn, bufferIn, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.8F);
        //this.renderModel(this.getEntityModel(), texture, matrixStackIn, bufferIn, 15728640, entityLivingBaseIn, 0, );
        //renderToBuffer(matrixStackIn, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public RenderType getRenderType(ResourceLocation textureLocation) {
        return CROAKER_EYES;
    }
}
