package com.redmiproclient.modules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class AntiBot extends Module {
    public AntiBot() {
        super("AntiBot");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.world == null) return;

        for (Entity e : mc.world.getEntities()) {
            if (e instanceof PlayerEntity && e != mc.player) {
                if (e.getName().getString().toLowerCase().contains("bot") ||
                    e.getName().getString().toLowerCase().contains("npc")) {
                    e.setInvisible(true);
                    e.setNoGravity(true);
                }
            }
        }
    }
}
