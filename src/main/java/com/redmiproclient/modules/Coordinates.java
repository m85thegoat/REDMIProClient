package com.redmiproclient.modules;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class Coordinates extends Module {
    public Coordinates() {
        super("Coordinates");
    }

    @Override
    public void onRender(Object matrixStackObj) {
        if (!isEnabled()) return;
        if (mc.player == null) return;

        MatrixStack matrices = (MatrixStack) matrixStackObj;
        TextRenderer tr = mc.textRenderer;
        int x = mc.getWindow().getScaledWidth() - 110;
        int y = 10;

        String coords = String.format("XYZ: %.1f, %.1f, %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
        String direction = "Facing: " + getDirection();
        String fps = "FPS: " + mc.getCurrentFps();

        tr.draw(matrices, coords, x, y, 0xFFFFFF);
        tr.draw(matrices, direction, x, y + 10, 0xFFFFFF);
        tr.draw(matrices, fps, x, y + 20, 0xFFFFFF);
    }

    private String getDirection() {
        float yaw = mc.player.getYaw();
        if (yaw < 0) yaw += 360;
        if (yaw >= 315 || yaw < 45) return "South";
        if (yaw >= 45 && yaw < 135) return "West";
        if (yaw >= 135 && yaw < 225) return "North";
        return "East";
    }
    }
