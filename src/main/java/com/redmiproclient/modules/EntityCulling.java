package com.redmiproclient.modules;

import net.minecraft.entity.Entity;

public class EntityCulling extends Module {
    public EntityCulling() {
        super("EntityCulling");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.world == null) return;
        for (Entity e : mc.world.getEntities()) {
            if (mc.player != null && !mc.player.canSee(e)) {
                e.setInvisible(true);
            } else {
                e.setInvisible(false);
            }
        }
    }
}
