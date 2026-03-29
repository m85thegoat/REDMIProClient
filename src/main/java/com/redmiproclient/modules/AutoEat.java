package com.redmiproclient.modules;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class AutoEat extends Module {
    private int minHunger = 18;
    private long lastEatTime = 0;
    private long minDelay = 500;

    public AutoEat() {
        super("AutoEat");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.player == null || mc.world == null) return;

        HungerManager hunger = mc.player.getHungerManager();
        if (hunger.getFoodLevel() <= minHunger) {
            long now = System.currentTimeMillis();
            if (now - lastEatTime >= minDelay) {
                int slot = findFoodSlot();
                if (slot != -1) {
                    if (mc.player.getInventory().selectedSlot != slot)
                        mc.player.getInventory().selectedSlot = slot;
                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                    lastEatTime = now;
                }
            }
        }
    }

    private int findFoodSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem().isFood()) {
                FoodComponent food = stack.getItem().getFoodComponent();
                if (food != null && food.isAlwaysEdible()) return i;
            }
        }
        return -1;
    }
                  }
