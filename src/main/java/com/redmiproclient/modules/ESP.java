package com.redmiproclient.modules;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class ESP extends Module {
    private boolean recording = false;

    public ESP() {
        super("ESP");
    }

    @Override
    public void onRender(Object matrixStackObj) {
        if (!isEnabled()) return;
        if (recording) return;

        MatrixStack matrices = (MatrixStack) matrixStackObj;
        Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity && entity != mc.player) {
                drawBox(matrices, entity, cameraPos, 1.0f, 0.0f, 0.0f);
            } else if (entity instanceof ChestBlockEntity) {
                drawBox(matrices, entity, cameraPos, 1.0f, 0.8f, 0.0f);
            }
        }
    }

    private void drawBox(MatrixStack matrices, Entity entity, Vec3d cameraPos, float r, float g, float b) {
        Box box = entity.getBoundingBox();
        double x = box.minX - cameraPos.x;
        double y = box.minY - cameraPos.y;
        double z = box.minZ - cameraPos.z;

        matrices.push();
        matrices.translate(x, y, z);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        float width = (float) (box.maxX - box.minX);
        float height = (float) (box.maxY - box.minY);
        float depth = (float) (box.maxZ - box.minZ);

        Vec3d[] corners = {
            new Vec3d(0, 0, 0), new Vec3d(width, 0, 0),
            new Vec3d(width, 0, 0), new Vec3d(width, 0, depth),
            new Vec3d(width, 0, depth), new Vec3d(0, 0, depth),
            new Vec3d(0, 0, depth), new Vec3d(0, 0, 0),

            new Vec3d(0, height, 0), new Vec3d(width, height, 0),
            new Vec3d(width, height, 0), new Vec3d(width, height, depth),
            new Vec3d(width, height, depth), new Vec3d(0, height, depth),
            new Vec3d(0, height, depth), new Vec3d(0, height, 0),

            new Vec3d(0, 0, 0), new Vec3d(0, height, 0),
            new Vec3d(width, 0, 0), new Vec3d(width, height, 0),
            new Vec3d(width, 0, depth), new Vec3d(width, height, depth),
            new Vec3d(0, 0, depth), new Vec3d(0, height, depth)
        };

        for (int i = 0; i < corners.length; i += 2) {
            Vec3d p1 = corners[i];
            Vec3d p2 = corners[i+1];
            buffer.vertex((float)p1.x, (float)p1.y, (float)p1.z).color(r, g, b, 0.8f).next();
            buffer.vertex((float)p2.x, (float)p2.y, (float)p2.z).color(r, g, b, 0.8f).next();
        }

        tessellator.draw();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        matrices.pop();
    }

    public void setRecording(boolean rec) { recording = rec; }
  }
