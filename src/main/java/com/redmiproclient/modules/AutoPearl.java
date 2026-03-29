package com.redmiproclient.modules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

import java.util.Comparator;
import java.util.List;

public class AutoPearl extends Module {
    private long lastPearl = 0;
    private long cooldown = 5000;

    public AutoPearl() {
        super("AutoPearl");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.player == null) return;

        Entity target = findNearestEnemy();
        if (target == null) return;

        if (mc.player.getHealth() < 10 || mc.player.distanceTo(target) > 15) {
            int slot = findPearl();
            if (slot != -1) {
                long now = System.currentTimeMillis();
                if (now - lastPearl >= cooldown) {
                    int oldSlot = mc.player.getInventory().selectedSlot;
                    mc.player.getInventory().selectedSlot = slot;
                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                    mc.player.getInventory().selectedSlot = oldSlot;
                    lastPearl = now;
                }
            }
        }
    }

    private Entity findNearestEnemy() {
        List<Entity> entities = mc.world.getEntities();
        return entities.stream()
            .filter(e -> e instanceof PlayerEntity && e != mc.player)
            .min(Comparator.comparing(e -> mc.player.distanceTo(e)))
            .orElse(null);
    }

    private int findPearl() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.ENDER_PEARL) return i;
        }
        return -1;
    }
          }
