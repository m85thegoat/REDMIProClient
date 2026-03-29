package com.redmiproclient.modules;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class AutoPot extends Module {
    private long lastThrow = 0;
    private long minDelay = 1000;

    public AutoPot() {
        super("AutoPot");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.player == null) return;

        if (mc.player.getHealth() < 10) {
            int slot = findPotion(Items.SPLASH_POTION, "healing");
            if (slot != -1) throwPotion(slot);
        }

        if (mc.player.getStatusEffect(StatusEffects.SPEED) == null) {
            int slot = findPotion(Items.SPLASH_POTION, "speed");
            if (slot != -1) throwPotion(slot);
        }
    }

    private int findPotion(Item item, String effect) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == item) {
                if (stack.getName().getString().toLowerCase().contains(effect)) return i;
            }
        }
        return -1;
    }

    private void throwPotion(int slot) {
        long now = System.currentTimeMillis();
        if (now - lastThrow < minDelay) return;
        int oldSlot = mc.player.getInventory().selectedSlot;
        mc.player.getInventory().selectedSlot = slot;
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        mc.player.getInventory().selectedSlot = oldSlot;
        lastThrow = now;
    }
          }
