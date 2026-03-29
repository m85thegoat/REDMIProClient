package com.redmiproclient.modules;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class AutoScaffold extends Module {
    private long lastPlaceTime = 0;
    private long lastRotateTime = 0;
    private int blocksPerSecond = 8;
    private boolean legitMode = true;
    private boolean rotate = true;
    private Random random = new Random();
    private int pauseCounter = 0;

    public AutoScaffold() {
        super("AutoScaffold");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.player == null || mc.world == null) return;

        BlockPos pos = getBlockToPlace();
        if (pos == null) return;
        BlockHitResult hitResult = getHitResult(pos);
        if (hitResult == null) return;
        int blockSlot = getBlockSlot();
        if (blockSlot == -1) return;

        if (legitMode && pauseCounter > 0) {
            pauseCounter--;
            return;
        }
        if (legitMode && random.nextInt(100) < 5) {
            pauseCounter = 3 + random.nextInt(8);
            return;
        }

        int oldSlot = mc.player.getInventory().selectedSlot;
        mc.player.getInventory().selectedSlot = blockSlot;

        long delay = getRandomDelay();
        long now = System.currentTimeMillis();
        if (now - lastPlaceTime >= delay) {
            if (rotate) smoothRotateToBlock(pos);
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
            lastPlaceTime = now;
        }
    }

    private BlockPos getBlockToPlace() {
        BlockPos playerPos = mc.player.getBlockPos();
        BlockPos under = playerPos.down();
        if (mc.world.getBlockState(under).isAir()) return under;
        BlockPos front = playerPos.offset(mc.player.getHorizontalFacing());
        BlockPos underFront = front.down();
        if (mc.world.getBlockState(underFront).isAir()) return underFront;
        return null;
    }

    private BlockHitResult getHitResult(BlockPos pos) {
        Vec3d hitVec = new Vec3d(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
        return new BlockHitResult(hitVec, Direction.UP, pos, false);
    }

    private int getBlockSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) stack.getItem()).getBlock();
                if (block != Blocks.AIR) return i;
            }
        }
        return -1;
    }

    private void smoothRotateToBlock(BlockPos pos) {
        double diffX = pos.getX() + 0.5 - mc.player.getX();
        double diffY = pos.getY() + 0.5 - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = pos.getZ() + 0.5 - mc.player.getZ();
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float targetYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(diffY, dist));
        targetYaw += (float)((random.nextDouble() - 0.5) * 1.5);
        targetPitch += (float)((random.nextDouble() - 0.5) * 1.0);

        long now = System.currentTimeMillis();
        long delta = now - lastRotateTime;
        if (delta > 20) {
            float yawDiff = targetYaw - mc.player.getYaw();
            float pitchDiff = targetPitch - mc.player.getPitch();
            float step = 5.0f * (delta / 20f);
            yawDiff = MathHelper.clamp(yawDiff, -step, step);
            pitchDiff = MathHelper.clamp(pitchDiff, -step, step);
            mc.player.setYaw(mc.player.getYaw() + yawDiff);
            mc.player.setPitch(mc.player.getPitch() + pitchDiff);
            lastRotateTime = now;
        }
    }

    private long getRandomDelay() {
        long baseDelay = 1000 / blocksPerSecond;
        return baseDelay + (long)(random.nextDouble() * baseDelay * 0.6);
    }

    private static class MathHelper {
        static float clamp(float value, float min, float max) {
            if (value < min) return min;
            if (value > max) return max;
            return value;
        }
    }
          }
