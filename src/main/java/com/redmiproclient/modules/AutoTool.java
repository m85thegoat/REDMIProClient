package com.redmiproclient.modules;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class AutoTool extends Module {
    public AutoTool() {
        super("AutoTool");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.player == null) return;

        HitResult hit = mc.crosshairTarget;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            BlockState state = mc.world.getBlockState(blockHit.getBlockPos());
            int bestSlot = findBestTool(state);
            if (bestSlot != -1 && bestSlot != mc.player.getInventory().selectedSlot) {
                mc.player.getInventory().selectedSlot = bestSlot;
            }
        }
    }

    private int findBestTool(BlockState state) {
        float bestSpeed = 1.0f;
        int bestSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof ToolItem) {
                float speed = stack.getMiningSpeedMultiplier(state);
                if (speed > bestSpeed) {
                    bestSpeed = speed;
                    bestSlot = i;
                }
            }
        }
        return bestSlot;
    }
  }
