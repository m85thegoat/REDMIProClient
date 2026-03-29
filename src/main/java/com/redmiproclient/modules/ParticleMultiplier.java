package com.redmiproclient.modules;

public class ParticleMultiplier extends Module {
    public ParticleMultiplier() {
        super("ParticleMultiplier");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.options == null) return;
        mc.options.getParticles().setValue(net.minecraft.client.option.ParticlesMode.MINIMAL);
    }
}
