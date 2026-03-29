package com.redmiproclient.modules;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Random;

public class CrystalDeflector extends Module {
    private long lastPlaceTime = 0;
    private long minDelay = 150;
    private long maxDelay = 250;
    private double detectionRange = 4.5;
    private double attackAngleThreshold = 45.0;
    private Random random = new Random();
    private boolean onlyIfHoldingWeapon = true;

    public CrystalDeflector() {
        super("CrystalDeflector");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.player == null || mc.world == null) return;
        if (!mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)) return;

        PlayerEntity threat = getThreat();
        if (threat == null) return;
        if (!isAboutToAttack(threat)) return;

        BlockPos placementPos = getPlacementPos(threat);
        if (placementPos == null) return;

        long now = System.currentTimeMillis();
        long delay = minDelay + random.nextInt((int)(maxDelay - minDelay));
        if (now - lastPlaceTime >= delay) {
            Vec3d hitVec = new Vec3d(placementPos.getX() + 0.5, placementPos.getY() + 0.5, placementPos.getZ() + 0.5);
            BlockHitResult hit = new BlockHitResult(hitVec, Direction.UP, placementPos, false);
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
            lastPlaceTime = now;
        }
    }

    private PlayerEntity getThreat() {
        List<PlayerEntity> players = mc.world.getPlayers();
        for (PlayerEntity player : players) {
            if (player == mc.player) continue;
            if (mc.player.distanceTo(player) > detectionRange) continue;
            if (onlyIfHoldingWeapon && !isHoldingWeapon(player)) continue;
            return player;
        }
        return null;
    }

    private boolean isAboutToAttack(PlayerEntity player) {
        Vec3d toYou = mc.player.getPos().subtract(player.getPos()).normalize();
        Vec3d lookDir = player.getRotationVector();
        double angle = Math.toDegrees(Math.acos(lookDir.dotProduct(toYou)));
        if (angle > attackAngleThreshold) return false;

        Vec3d velocity = player.getVelocity();
        Vec3d toYouDir = mc.player.getPos().subtract(player.getPos()).normalize();
        double closingSpeed = velocity.dotProduct(toYouDir);
        if (closingSpeed < 0.1) return false;

        return true;
    }

    private BlockPos getPlacementPos(PlayerEntity threat) {
        Vec3d playerPos = mc.player.getPos();
        Vec3d threatPos = threat.getPos();
        Vec3d direction = threatPos.subtract(playerPos).normalize();

        for (double d = 1.5; d <= 3.0; d += 0.5) {
            Vec3d posVec = playerPos.add(direction.multiply(d));
            BlockPos candidate = new BlockPos((int)Math.floor(posVec.x), (int)Math.floor(posVec.y), (int)Math.floor(posVec.z));
            if (mc.world.getBlockState(candidate).getBlock() == Blocks.OBSIDIAN ||
                mc.world.getBlockState(candidate).getBlock() == Blocks.BEDROCK) {
                if (mc.world.getBlockState(candidate.up()).isAir()) {
                    return candidate.up();
                }
            }
        }

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos pos = mc.player.getBlockPos().add(x, 0, z);
                if (mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN ||
                    mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK) {
                    if (mc.world.getBlockState(pos.up()).isAir()) {
                        return pos.up();
                    }
                }
            }
        }
        return null;
    }

    private boolean isHoldingWeapon(PlayerEntity player) {
        String item = player.getMainHandStack().getItem().toString().toLowerCase();
        return item.contains("sword") || item.contains("axe") || item.contains("trident");
    }
    }
