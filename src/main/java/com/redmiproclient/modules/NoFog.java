package com.redmiproclient.modules;

public class NoFog extends Module {
    public NoFog() {
        super("NoFog");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        // Fog removal requires a mixin; placeholder
    }
}
