package com.redmiproclient.modules;

import net.minecraft.client.option.KeyBinding;

public class AutoRun extends Module {
    private boolean wasPressed = false;

    public AutoRun() {
        super("AutoRun");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        if (mc.player == null) return;

        KeyBinding forwardKey = mc.options.forwardKey;
        if (!forwardKey.isPressed()) {
            forwardKey.setPressed(true);
            wasPressed = true;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled && wasPressed && mc.player != null) {
            mc.options.forwardKey.setPressed(false);
            wasPressed = false;
        }
    }
}
