package com.redmiproclient.modules;

import net.minecraft.block.Blocks;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class AutoCrystal extends Module {
    private long lastPlace = 0;
    private long lastBreak = 0;
    private long minDelay = 100;
    private long maxDelay = 180;
    private boolean legitMode = true;
    private double minHealthToUse = 6.0;
    private double maxSelfDamage = 3.0;
    private Random random = new Random();

    public AutoCrystal() {
        super("AutoCrystal");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.player == null || mc.world == null) return;
        if (mc.player.getHealth() < minHealthToUse) return;
        if (!mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)) return;

        long now = System.currentTimeMillis();

        EndCrystalEntity crystal = getNearestCrystal();
        if (crystal != null && canBreakSafely(crystal)) {
            long breakDelay = getRandomDelay();
            if (legitMode) breakDelay += random.nextInt(40);
            if (now - lastBreak >= breakDelay) {
                mc.interactionManager.attackEntity(mc.player, crystal);
                lastBreak = now;
                return;
            }
        }

        BlockPos placePos = findPlacePos();
        if (placePos != null && canPlaceSafely(placePos)) {
            long placeDelay = getRandomDelay();
            if (legitMode) {
                placeDelay += random.nextInt(50);
                if (random.nextInt(100) < 8) return;
            }
            if (now - lastPlace >= placeDelay) {
                Vec3d hitVec = new Vec3d(placePos.getX() + 0.5, placePos.getY() + 0.5, placePos.getZ() + 0.5);
                BlockHitResult hit = new BlockHitResult(hitVec, Direction.UP, placePos, false);
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
                lastPlace = now;
            }
        }
    }

    private EndCrystalEntity getNearestCrystal() {
        List<EndCrystalEntity> crystals = mc.world.getEntitiesByClass(
            EndCrystalEntity.class,
            mc.player.getBoundingBox().expand(5.0),
            e -> true
        );
        return crystals.stream()
            .min(Comparator.comparing(e -> mc.player.distanceTo(e)))
            .orElse(null);
    }

    private BlockPos findPlacePos() {
        for (int x = -3; x <= 3; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos pos = mc.player.getBlockPos().add(x, y, z);
                    if (mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN ||
                        mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK) {
                        if (mc.world.getBlockState(pos.up()).isAir()) {
                            return pos.up();
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean canBreakSafely(EndCrystalEntity crystal) {
        double distance = mc.player.distanceTo(crystal);
        double damage = 6.0 / (1.0 + distance * distance);
        return damage <= maxSelfDamage;
    }

    private boolean canPlaceSafely(BlockPos pos) {
        Box checkBox = new Box(pos).expand(3.0);
        List<EndCrystalEntity> nearby = mc.world.getEntitiesByClass(EndCrystalEntity.class, checkBox, e -> true);
        for (EndCrystalEntity c : nearby) {
            if (mc.player.distanceTo(c) < 2.5) return false;
        }
        return true;
    }

    private long getRandomDelay() {
        return minDelay + (long)(random.nextDouble() * (maxDelay - minDelay));
    }
                  }
