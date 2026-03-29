package com.redmiproclient.modules;

import net.minecraft.client.render.CloudRenderMode;

public class FastRender extends Module {
    public FastRender() {
        super("FastRender");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.options == null) return;
        mc.options.getClouds().setValue(CloudRenderMode.OFF);
        mc.options.setWeather(false);
        mc.options.getVignette().setValue(false);
    }
}
