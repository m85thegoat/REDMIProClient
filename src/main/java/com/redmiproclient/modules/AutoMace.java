package com.redmiproclient.modules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;

public class AutoMace extends Module {
    private long lastAttackTime = 0;
    private long minDelay = 200;
    private long maxDelay = 400;
    private float minFallDistance = 1.5f;

    public AutoMace() {
        super("AutoMace");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.player == null || mc.world == null) return;
        if (!mc.player.getMainHandStack().isOf(Items.MACE)) return;
        if (mc.player.fallDistance < minFallDistance) return;

        Entity target = findTargetBelow();
        if (target == null) return;

        long now = System.currentTimeMillis();
        long delay = minDelay + (long)(Math.random() * (maxDelay - minDelay));
        if (now - lastAttackTime >= delay) {
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
            lastAttackTime = now;
        }
    }

    private Entity findTargetBelow() {
        Vec3d playerPos = mc.player.getPos();
        return mc.world.getEntities().stream()
            .filter(e -> e instanceof LivingEntity && e != mc.player)
            .filter(e -> Math.abs(e.getX() - playerPos.x) <= 2.0)
            .filter(e -> Math.abs(e.getZ() - playerPos.z) <= 2.0)
            .filter(e -> e.getY() < playerPos.y && e.getY() > playerPos.y - 5.0)
            .min(Comparator.comparing(e -> playerPos.distanceTo(e.getPos())))
            .orElse(null);
    }
}
