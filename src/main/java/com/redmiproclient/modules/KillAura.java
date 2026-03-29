package com.redmiproclient.modules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class KillAura extends Module {
    // Timing
    private long lastAttackTime = 0;
    private long lastRotateTime = 0;
    private long lastSwitchTime = 0;
    
    // Range
    private float baseRange = 3.0f;
    private float maxRange = 3.3f;
    private float currentRange = 3.0f;
    
    // Attack timing (human-like)
    private long minDelay = 100;
    private long maxDelay = 220;
    private double missChance = 0.08;
    
    // Targeting mode
    public enum TargetingMode { SINGLE, SWITCH, MULTI, AIM_ASSIST }
    private TargetingMode targetingMode = TargetingMode.SINGLE;
    
    // Rotation
    private boolean rotate = true;
    private boolean aimAssistMode = true;
    private boolean silentRotate = true;
    private float rotationSpeed = 0.5f;
    
    // Anti-detection
    private Random random = new Random();
    private double[] clickTimes = new double[20];
    private int clickIndex = 0;
    
    // Target management
    private Entity currentTarget = null;
    private List<Entity> targetList = new ArrayList<>();
    private int currentTargetIndex = 0;
    
    // Info
    private String currentTargetName = "";
    private float currentTargetHealth = 0;
    private float currentTargetDistance = 0;

    public KillAura() {
        super("KillAura");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.player == null || mc.world == null) return;

        targetList = getValidTargets();
        if (targetList.isEmpty()) {
            currentTarget = null;
            return;
        }
        
        selectTarget();
        if (currentTarget == null) return;
        updateTargetInfo();
        if (!canHit(currentTarget)) return;
        
        currentRange = getDynamicRange();
        if (mc.player.distanceTo(currentTarget) > currentRange) return;
        
        handleRotations();
        handleAttack();
    }
    
    private List<Entity> getValidTargets() {
        return mc.world.getEntities().stream()
            .filter(e -> e instanceof LivingEntity && e != mc.player)
            .filter(e -> ((LivingEntity)e).isAlive())
            .filter(e -> mc.player.distanceTo(e) <= maxRange)
            .sorted(Comparator.comparing(e -> mc.player.distanceTo(e)))
            .collect(Collectors.toList());
    }
    
    private void selectTarget() {
        switch (targetingMode) {
            case SINGLE:
                currentTarget = targetList.isEmpty() ? null : targetList.get(0);
                break;
            case SWITCH:
                if (System.currentTimeMillis() - lastSwitchTime > 1500) {
                    currentTargetIndex = (currentTargetIndex + 1) % targetList.size();
                    lastSwitchTime = System.currentTimeMillis();
                }
                currentTarget = targetList.get(currentTargetIndex);
                break;
            case MULTI:
                for (Entity target : targetList) {
                    if (mc.player.distanceTo(target) <= currentRange && canHit(target)) {
                        attackEntity(target);
                    }
                }
                return;
            case AIM_ASSIST:
                currentTarget = targetList.get(0);
                break;
        }
    }
    
    private void handleRotations() {
        if (!rotate) return;
        float[] rotations = getRotationsWithGCD(currentTarget);
        if (aimAssistMode) {
            smoothRotate(rotations);
        } else {
            if (silentRotate) {
                mc.player.setYaw(rotations[0]);
                mc.player.setPitch(rotations[1]);
            } else {
                mc.player.setYaw(rotations[0]);
                mc.player.setPitch(rotations[1]);
            }
        }
    }
    
    private void handleAttack() {
        if (targetingMode == TargetingMode.AIM_ASSIST) return;
        long now = System.currentTimeMillis();
        long delay = getRandomDelay();
        recordClickTime(now);
        if (now - lastAttackTime >= delay) {
            if (random.nextDouble() > missChance) {
                attackEntity(currentTarget);
            }
            lastAttackTime = now;
        }
    }
    
    private void attackEntity(Entity target) {
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
    }
    
    private boolean canHit(Entity target) {
        Vec3d eyes = mc.player.getEyePos();
        Vec3d targetPos = target.getBoundingBox().getCenter();
        BlockHitResult hit = mc.world.raycast(new RaycastContext(
            eyes, targetPos,
            RaycastContext.ShapeType.COLLIDER,
            RaycastContext.FluidHandling.NONE,
            mc.player
        ));
        return hit.getType() == HitResult.Type.MISS;
    }
    
    private float getDynamicRange() {
        float range = baseRange;
        try {
            float ping = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency();
            range += ping / 600.0f;
        } catch (Exception e) {}
        range += (random.nextDouble() - 0.5) * 0.15;
        return Math.min(range, maxRange);
    }
    
    private float[] getRotationsWithGCD(Entity target) {
        Vec3d pos = target.getBoundingBox().getCenter();
        double diffX = pos.x - mc.player.getX();
        double diffY = pos.y + (target.getHeight() / 2) - (mc.player.getEyeY());
        double diffZ = pos.z - mc.player.getZ();
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, dist));
        // GCD bypass
        float gcdOffset = (float) (random.nextGaussian() * 0.005);
        yaw += gcdOffset;
        pitch += gcdOffset;
        // Jitter
        yaw += (float)((random.nextDouble() - 0.5) * 1.5);
        pitch += (float)((random.nextDouble() - 0.5) * 1.0);
        return new float[]{MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch)};
    }
    
    private void smoothRotate(float[] targetRot) {
        long now = System.currentTimeMillis();
        long delta = now - lastRotateTime;
        if (delta > 50) {
            float yawDiff = MathHelper.wrapDegrees(targetRot[0] - mc.player.getYaw());
            float pitchDiff = targetRot[1] - mc.player.getPitch();
            float step = rotationSpeed * (delta / 50f);
            yawDiff = MathHelper.clamp(yawDiff, -step, step);
            pitchDiff = MathHelper.clamp(pitchDiff, -step, step);
            mc.player.setYaw(mc.player.getYaw() + yawDiff);
            mc.player.setPitch(mc.player.getPitch() + pitchDiff);
            lastRotateTime = now;
        }
    }
    
    private long getRandomDelay() {
        double entropy = calculateClickEntropy();
        long baseDelay = minDelay + (long)(random.nextDouble() * (maxDelay - minDelay));
        if (entropy < 0.5) baseDelay += random.nextInt(40);
        return baseDelay;
    }
    
    private void recordClickTime(long time) {
        clickTimes[clickIndex] = time;
        clickIndex = (clickIndex + 1) % clickTimes.length;
    }
    
    private double calculateClickEntropy() {
        double sum = 0;
        int valid = 0;
        for (int i = 1; i < clickTimes.length; i++) {
            if (clickTimes[i] > 0 && clickTimes[i-1] > 0) {
                sum += clickTimes[i] - clickTimes[i-1];
                valid++;
            }
        }
        if (valid < 2) return 1.0;
        double mean = sum / valid;
        double variance = 0;
        for (int i = 1; i < clickTimes.length; i++) {
            if (clickTimes[i] > 0 && clickTimes[i-1] > 0) {
                double interval = clickTimes[i] - clickTimes[i-1];
                variance += Math.pow(interval - mean, 2);
            }
        }
        variance /= valid;
        double stdDev = Math.sqrt(variance);
        return Math.min(1.0, stdDev / 40.0);
    }
    
    private void updateTargetInfo() {
        if (currentTarget instanceof PlayerEntity) {
            PlayerEntity p = (PlayerEntity) currentTarget;
            currentTargetName = p.getName().getString();
            currentTargetHealth = p.getHealth();
            currentTargetDistance = mc.player.distanceTo(currentTarget);
        }
    }
    
    public float getCurrentRange() { return currentRange; }
    public String getCurrentTargetName() { return currentTargetName; }
    public float getCurrentTargetHealth() { return currentTargetHealth; }
    public float getCurrentTargetDistance() { return currentTargetDistance; }
    public TargetingMode getTargetingMode() { return targetingMode; }
    public void setTargetingMode(TargetingMode mode) { this.targetingMode = mode; }
    public void setAimAssistMode(boolean enabled) { this.aimAssistMode = enabled; }
    public void setRotate(boolean rotate) { this.rotate = rotate; }
  }
