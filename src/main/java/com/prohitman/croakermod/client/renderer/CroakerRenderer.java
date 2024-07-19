package com.prohitman.croakermod.client.renderer;

import com.prohitman.croakermod.client.model.CroakerModel;
import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class CroakerRenderer extends GeoEntityRenderer<CroakerEntity> {
    public CroakerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CroakerModel());
        this.shadowRadius = 1.5f;
    }
}
