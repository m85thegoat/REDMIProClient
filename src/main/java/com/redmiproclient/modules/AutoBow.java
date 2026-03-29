package com.redmiproclient.modules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class AutoBow extends Module {
    private long lastShootTime = 0;
    private long minDelay = 1000;
    private long maxDelay = 2000;
    private double range = 30.0;
    private boolean rotate = true;
    private boolean legitMode = true;
    private Random random = new Random();
    private boolean isCharging = false;
    private long chargeStart = 0;

    private double aimError = 2.0;
    private double missChance = 0.15;
    private double smoothSpeed = 5.0;

    private Entity currentTarget = null;
    private int scanCooldown = 0;

    public AutoBow() {
        super("AutoBow");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.player == null || mc.world == null) return;

        long now = System.currentTimeMillis();
        if (now - lastShootTime < minDelay) return;

        if (!mc.player.getMainHandStack().isOf(Items.BOW)) return;

        if (scanCooldown <= 0) {
            currentTarget = getBestTarget();
            scanCooldown = 5;
        } else {
            scanCooldown--;
        }

        if (currentTarget == null) {
            if (isCharging && mc.player.getItemUseTime() > 0) mc.player.stopUsingItem();
            isCharging = false;
            return;
        }

        float[] rotations = calculateAim(currentTarget);
        if (rotate) {
            if (legitMode) smoothRotate(rotations);
            else {
                mc.player.setYaw(rotations[0]);
                mc.player.setPitch(rotations[1]);
            }
        }

        if (legitMode && random.nextDouble() < missChance) return;

        if (!isCharging) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            isCharging = true;
            chargeStart = now;
        } else {
            float bowPower = getBowPower(mc.player.getItemUseTime());
            float optimalPower = calculateOptimalPower(currentTarget);
            if (bowPower >= optimalPower || (now - chargeStart) > 3000) {
                mc.interactionManager.stopUsingItem(mc.player);
                isCharging = false;
                long delay = legitMode ? minDelay + (long)(random.nextDouble() * (maxDelay - minDelay)) : minDelay;
                lastShootTime = now + delay;
            }
        }
    }

    private Entity getBestTarget() {
        List<Entity> entities = mc.world.getEntities();
        return entities.stream()
                .filter(e -> e instanceof LivingEntity && e != mc.player)
                .filter(e -> ((LivingEntity) e).isAlive())
                .filter(e -> !e.isInvisible())
                .filter(e -> mc.player.canSee(e))
                .filter(e -> mc.player.distanceTo(e) <= range)
                .min(Comparator.comparing(e -> mc.player.distanceTo(e)))
                .orElse(null);
    }

    private float[] calculateAim(Entity target) {
        Vec3d playerPos = mc.player.getEyePos();
        Vec3d targetPos = target.getBoundingBox().getCenter();
        Vec3d targetVelocity = target.getVelocity();
        double distance = playerPos.distanceTo(targetPos);
        double timeToHit = distance / 40.0;
        Vec3d predictedPos = targetPos.add(targetVelocity.multiply(timeToHit));

        double dx = predictedPos.x - playerPos.x;
        double dy = predictedPos.y - playerPos.y;
        double dz = predictedPos.z - playerPos.z;
        double horizDist = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, horizDist));

        // Gravity compensation
        double gravity = 0.05;
        double v = 40.0;
        double g = gravity * (distance / 50.0);
        pitch -= (float) Math.toDegrees(Math.atan(g * distance / (v * v)));

        if (legitMode) {
            yaw += (float)((random.nextDouble() - 0.5) * aimError);
            pitch += (float)((random.nextDouble() - 0.5) * aimError * 0.75);
        }
        return new float[]{MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch)};
    }

    private float calculateOptimalPower(Entity target) {
        double distance = mc.player.distanceTo(target);
        float power = (float) Math.min(1.0, distance / 30.0);
        if (legitMode) {
            power += (float)((random.nextDouble() - 0.5) * 0.2);
            power = MathHelper.clamp(power, 0.2f, 1.0f);
        }
        return power;
    }

    private void smoothRotate(float[] targetRot) {
        float yawDiff = MathHelper.wrapDegrees(targetRot[0] - mc.player.getYaw());
        float pitchDiff = targetRot[1] - mc.player.getPitch();
        float speed = legitMode ? (float)smoothSpeed : 15.0f;
        yawDiff = MathHelper.clamp(yawDiff, -speed, speed);
        pitchDiff = MathHelper.clamp(pitchDiff, -speed, speed);
        mc.player.setYaw(mc.player.getYaw() + yawDiff);
        mc.player.setPitch(mc.player.getPitch() + pitchDiff);
    }

    private float getBowPower(int useTicks) {
        float f = (float)useTicks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) f = 1.0F;
        return f;
    }

    public double getRange() { return range; }
    public void setRange(double range) { this.range = range; }
    public double getAimError() { return aimError; }
    public void setAimError(double aimError) { this.aimError = aimError; }
    public double getMissChance() { return missChance; }
    public void setMissChance(double missChance) { this.missChance = missChance; }
    public double getSmoothSpeed() { return smoothSpeed; }
    public void setSmoothSpeed(double speed) { this.smoothSpeed = speed; }
    public boolean isLegitMode() { return legitMode; }
    public void setLegitMode(boolean legit) { this.legitMode = legit; }
    public boolean isRotate() { return rotate; }
    public void setRotate(boolean rotate) { this.rotate = rotate; }
          }
