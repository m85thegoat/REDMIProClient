package com.redmiproclient.modules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

import java.util.Comparator;
import java.util.List;

public class AutoWeapon extends Module {
    public AutoWeapon() {
        super("AutoWeapon");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.player == null) return;

        Entity target = findNearestEnemy();
        if (target != null && mc.player.distanceTo(target) < 4.0) {
            int bestSlot = findBestWeapon();
            if (bestSlot != -1 && bestSlot != mc.player.getInventory().selectedSlot) {
                mc.player.getInventory().selectedSlot = bestSlot;
            }
        }
    }

    private Entity findNearestEnemy() {
        List<Entity> entities = mc.world.getEntities();
        return entities.stream()
            .filter(e -> e instanceof LivingEntity && e != mc.player)
            .min(Comparator.comparing(e -> mc.player.distanceTo(e)))
            .orElse(null);
    }

    private int findBestWeapon() {
        float bestDamage = 0;
        int bestSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof SwordItem) {
                SwordItem sword = (SwordItem) stack.getItem();
                float damage = sword.getAttackDamage();
                if (damage > bestDamage) {
                    bestDamage = damage;
                    bestSlot = i;
                }
            }
        }
        return bestSlot;
    }
                }
