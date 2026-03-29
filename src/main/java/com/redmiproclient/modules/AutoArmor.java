package com.redmiproclient.modules;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmor extends Module {
    private long lastEquipTime = 0;
    private long equipDelay = 200;

    public AutoArmor() {
        super("AutoArmor");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.player == null || mc.world == null) return;

        long now = System.currentTimeMillis();
        if (now - lastEquipTime < equipDelay) return;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.ARMOR) continue;
            ItemStack current = mc.player.getEquippedStack(slot);
            int bestSlot = findBestArmorSlot(slot);
            if (bestSlot != -1) {
                if (current.isEmpty() || isBetterArmor(mc.player.getInventory().getStack(bestSlot), current)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, bestSlot, 0, SlotActionType.QUICK_MOVE, mc.player);
                    lastEquipTime = now;
                    break;
                }
            }
        }
    }

    private int findBestArmorSlot(EquipmentSlot slot) {
        int best = -1;
        int bestProtection = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof ArmorItem) {
                ArmorItem armor = (ArmorItem) stack.getItem();
                if (armor.getSlotType() == slot) {
                    int protection = armor.getProtection();
                    if (protection > bestProtection) {
                        bestProtection = protection;
                        best = i;
                    }
                }
            }
        }
        return best;
    }

    private boolean isBetterArmor(ItemStack newArmor, ItemStack oldArmor) {
        if (oldArmor.isEmpty()) return true;
        if (newArmor.isEmpty()) return false;
        ArmorItem newItem = (ArmorItem) newArmor.getItem();
        ArmorItem oldItem = (ArmorItem) oldArmor.getItem();
        return newItem.getProtection() > oldItem.getProtection();
    }
                    }
