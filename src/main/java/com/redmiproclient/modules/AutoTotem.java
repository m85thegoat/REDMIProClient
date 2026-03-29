package com.redmiproclient.modules;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotem extends Module {
    public AutoTotem() {
        super("AutoTotem");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.player == null) return;

        if (mc.player.getHealth() < 10) {
            ItemStack offhand = mc.player.getOffHandStack();
            if (offhand.getItem() != Items.TOTEM_OF_UNDYING) {
                int totemSlot = findTotem();
                if (totemSlot != -1) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, totemSlot, 40, SlotActionType.SWAP, mc.player);
                }
            }
        }
    }

    private int findTotem() {
        for (int i = 0; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) return i;
        }
        return -1;
    }
}
