package com.redmiproclient.modules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

import java.util.Comparator;
import java.util.List;

public class AimAssist extends Module {
    private float speed = 0.3f;
    private float range = 5.0f;

    public AimAssist() {
        super("AimAssist");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.player == null) return;

        Entity target = findNearestEnemy();
        if (target != null && mc.player.distanceTo(target) <= range) {
            float[] rotations = getRotationsTo(target);
            smoothRotate(rotations);
        }
    }

    private Entity findNearestEnemy() {
        List<Entity> entities = mc.world.getEntities();
        return entities.stream()
            .filter(e -> e instanceof LivingEntity && e != mc.player)
            .min(Comparator.comparing(e -> mc.player.distanceTo(e)))
            .orElse(null);
    }

    private float[] getRotationsTo(Entity target) {
        double diffX = target.getX() - mc.player.getX();
        double diffY = target.getY() + target.getHeight() / 2 - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = target.getZ() - mc.player.getZ();
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, dist));
        return new float[]{MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch)};
    }

    private void smoothRotate(float[] targetRot) {
        float yawDiff = MathHelper.wrapDegrees(targetRot[0] - mc.player.getYaw());
        float pitchDiff = targetRot[1] - mc.player.getPitch();
        yawDiff = MathHelper.clamp(yawDiff, -speed, speed);
        pitchDiff = MathHelper.clamp(pitchDiff, -speed, speed);
        mc.player.setYaw(mc.player.getYaw() + yawDiff);
        mc.player.setPitch(mc.player.getPitch() + pitchDiff);
    }
          }
