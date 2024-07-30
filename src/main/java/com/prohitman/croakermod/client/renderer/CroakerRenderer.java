package com.prohitman.croakermod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.prohitman.croakermod.client.model.CroakerModel;
import com.prohitman.croakermod.climbing.common.entity.mob.IClimberEntity;
import com.prohitman.croakermod.climbing.common.entity.mob.Orientation;
import com.prohitman.croakermod.climbing.common.entity.mob.PathingTarget;
import com.prohitman.croakermod.server.entity.CroakerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import java.util.List;

public class CroakerRenderer extends GeoEntityRenderer<CroakerEntity> {
    public CroakerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CroakerModel());
        this.shadowRadius = 1.5f;

        addLayer(new GlowEyesLayer(this));
    }

    @Override
    public void render(CroakerEntity animatable, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(animatable != null) {
            //System.out.println("Early bird");
            //poseStack.pushPose();
            Orientation orientation = ((IClimberEntity) animatable).getOrientation();
            Orientation renderOrientation = ((IClimberEntity) animatable).calculateOrientation(partialTicks);
            ((IClimberEntity) animatable).setRenderOrientation(renderOrientation);

            float verticalOffset = ((IClimberEntity) animatable).getVerticalOffset(partialTicks);

            float x = ((IClimberEntity) animatable).getAttachmentOffset(Direction.Axis.X, partialTicks) - (float) renderOrientation.normal.x * verticalOffset;
            float y = ((IClimberEntity) animatable).getAttachmentOffset(Direction.Axis.Y, partialTicks) - (float) renderOrientation.normal.y * verticalOffset;
            float z = ((IClimberEntity) animatable).getAttachmentOffset(Direction.Axis.Z, partialTicks) - (float) renderOrientation.normal.z * verticalOffset;

            poseStack.translate(x, y, z);

            poseStack.mulPose(Vector3f.YP.rotationDegrees(renderOrientation.yaw));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(renderOrientation.pitch));
            poseStack.mulPose(Vector3f.YP.rotationDegrees((float) Math.signum(0.5f - orientation.componentY - orientation.componentZ - orientation.componentX) * renderOrientation.yaw));

            //poseStack.popPose();
        }
        super.render(animatable, entityYaw, partialTicks, poseStack, bufferSource, packedLight);

        Orientation orientation = animatable.getOrientation();
        Orientation renderOrientation = animatable.getRenderOrientation();

        if(renderOrientation != null) {
            //System.out.println("Late bird");
            //poseStack.pushPose();

            float verticalOffset = animatable.getVerticalOffset(partialTicks);

            float x = animatable.getAttachmentOffset(Direction.Axis.X, partialTicks) - (float) renderOrientation.normal.x * verticalOffset;
            float y = animatable.getAttachmentOffset(Direction.Axis.Y, partialTicks) - (float) renderOrientation.normal.y * verticalOffset;
            float z = animatable.getAttachmentOffset(Direction.Axis.Z, partialTicks) - (float) renderOrientation.normal.z * verticalOffset;

            poseStack.mulPose(Vector3f.YP.rotationDegrees(-(float) Math.signum(0.5f - orientation.componentY - orientation.componentZ - orientation.componentX) * renderOrientation.yaw));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-renderOrientation.pitch));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-renderOrientation.yaw));
            if(Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
                LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.LINES), new AABB(0, 0, 0, 0, 0, 0).inflate(0.2f), 1.0f, 1.0f, 1.0f, 1.0f);

                double rx = animatable.xo + (animatable.getX() - animatable.xo) * partialTicks;
                double ry = animatable.yo + (animatable.getY() - animatable.yo) * partialTicks;
                double rz = animatable.zo + (animatable.getZ() - animatable.zo) * partialTicks;

                Vec3 movementTarget = animatable.getTrackedMovementTarget();

                if(movementTarget != null) {
                    LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.LINES), new AABB(movementTarget.x() - 0.25f, movementTarget.y() - 0.25f, movementTarget.z() - 0.25f, movementTarget.x() + 0.25f, movementTarget.y() + 0.25f, movementTarget.z() + 0.25f).move(-rx - x, -ry - y, -rz - z), 0.0f, 1.0f, 1.0f, 1.0f);
                }

                List<PathingTarget> pathingTargets = animatable.getTrackedPathingTargets();

                if(pathingTargets != null) {
                    int i = 0;

                    for(PathingTarget pathingTarget : pathingTargets) {
                        BlockPos pos = pathingTarget.pos;

                        LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.LINES), new AABB(pos).move(-rx - x, -ry - y, -rz - z), 1.0f, i / (float) (pathingTargets.size() - 1), 0.0f, 0.15f);

                        poseStack.pushPose();
                        poseStack.translate(pos.getX() + 0.5D - rx - x, pos.getY() + 0.5D - ry - y, pos.getZ() + 0.5D - rz - z);

                        poseStack.mulPose(pathingTarget.side.getOpposite().getRotation());

                        LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.LINES), new AABB(-0.501D, -0.501D, -0.501D, 0.501D, -0.45D, 0.501D), 1.0f, i / (float) (pathingTargets.size() - 1), 0.0f, 1.0f);

                        Matrix4f matrix4f = poseStack.last().pose();
                        VertexConsumer builder = bufferSource.getBuffer(RenderType.LINES);
                        Matrix3f matrix3f = poseStack.last().normal();

                        builder.vertex(matrix4f, -0.501f, -0.45f, -0.501f).color(1.0f, i / (float) (pathingTargets.size() - 1), 0.0f, 1.0f).normal(matrix3f, 1, 1, 1).endVertex();
                        builder.vertex(matrix4f, 0.501f, -0.45f, 0.501f).color(1.0f, i / (float) (pathingTargets.size() - 1), 0.0f, 1.0f).normal(matrix3f, 1, 1, 1).endVertex();
                        builder.vertex(matrix4f, -0.501f, -0.45f, 0.501f).color(1.0f, i / (float) (pathingTargets.size() - 1), 0.0f, 1.0f).normal(matrix3f, 1, 1, 1).endVertex();
                        builder.vertex(matrix4f, 0.501f, -0.45f, -0.501f).color(1.0f, i / (float) (pathingTargets.size() - 1), 0.0f, 1.0f).normal(matrix3f, 1, 1, 1).endVertex();

                        poseStack.popPose();

                        i++;
                    }
                }

                Matrix4f matrix4f = poseStack.last().pose();
                VertexConsumer builder = bufferSource.getBuffer(RenderType.LINES);
                Matrix3f matrix3f = poseStack.last().normal();

                //vertex, color, uv, overlay, uv2, normal, endvertex
                builder.vertex(matrix4f, 0, 0, 0).color(0, 1, 1, 1).normal(matrix3f, 1, 1, 1).endVertex();
                builder.vertex(matrix4f, (float) orientation.normal.x * 2, (float) orientation.normal.y * 2, (float) orientation.normal.z * 2).color(1.0f, 0.0f, 1.0f, 1.0f).normal(matrix3f, 1, 1, 1).endVertex();

                LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.LINES), new AABB(0, 0, 0, 0, 0, 0).move((float) orientation.normal.x * 2, (float) orientation.normal.y * 2, (float) orientation.normal.z * 2).inflate(0.025f), 1.0f, 0.0f, 1.0f, 1.0f);

                poseStack.pushPose();

                poseStack.translate(-x, -y, -z);

                matrix4f = poseStack.last().pose();
                matrix3f = poseStack.last().normal();

                builder.vertex(matrix4f, 0, animatable.getBbHeight() * 0.5f, 0).color(0, 1, 1, 1).normal(matrix3f, 1, 1, 1).endVertex();
                builder.vertex(matrix4f, (float) orientation.localX.x, animatable.getBbHeight() * 0.5f + (float) orientation.localX.y, (float) orientation.localX.z).color(1.0f, 0.0f, 0.0f, 1.0f).normal(matrix3f, 1, 1, 1).endVertex();

                LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.LINES), new AABB(0, 0, 0, 0, 0, 0).move((float) orientation.localX.x, animatable.getBbHeight() * 0.5f + (float) orientation.localX.y, (float) orientation.localX.z).inflate(0.025f), 1.0f, 0.0f, 0.0f, 1.0f);

                builder.vertex(matrix4f, 0, animatable.getBbHeight() * 0.5f, 0).color(0, 1, 1, 1).normal(matrix3f, 1, 1, 1).endVertex();
                builder.vertex(matrix4f, (float) orientation.localY.x, animatable.getBbHeight() * 0.5f + (float) orientation.localY.y, (float) orientation.localY.z).color(0.0f, 1.0f, 0.0f, 1.0f).normal(matrix3f, 1, 1, 1).endVertex();

                LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.LINES), new AABB(0, 0, 0, 0, 0, 0).move((float) orientation.localY.x, animatable.getBbHeight() * 0.5f + (float) orientation.localY.y, (float) orientation.localY.z).inflate(0.025f), 0.0f, 1.0f, 0.0f, 1.0f);

                builder.vertex(matrix4f, 0, animatable.getBbHeight() * 0.5f, 0).color(0, 1, 1, 1).normal(matrix3f, 1, 1, 1).endVertex();
                builder.vertex(matrix4f, (float) orientation.localZ.x, animatable.getBbHeight() * 0.5f + (float) orientation.localZ.y, (float) orientation.localZ.z).color(0.0f, 0.0f, 1.0f, 1.0f).normal(matrix3f, 1, 1, 1).endVertex();

                LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.LINES), new AABB(0, 0, 0, 0, 0, 0).move((float) orientation.localZ.x, animatable.getBbHeight() * 0.5f + (float) orientation.localZ.y, (float) orientation.localZ.z).inflate(0.025f), 0.0f, 0.0f, 1.0f, 1.0f);

                poseStack.popPose();
            }

            poseStack.translate(-x, -y, -z);

            //poseStack.popPose();
        }
    }
}
