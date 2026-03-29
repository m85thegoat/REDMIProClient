package com.redmiproclient.gui;

import com.redmiproclient.RedmiProClient;
import com.redmiproclient.modules.AutoBow;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class AutoBowSettingsGUI extends Screen {
    private AutoBow autoBow;
    private double range;
    private double aimError;
    private double missChance;
    private double smoothSpeed;
    private boolean legitMode;
    private boolean rotate;

    public AutoBowSettingsGUI() {
        super(Text.literal("AutoBow Settings"));
        this.autoBow = RedmiProClient.INSTANCE.autoBow;
        this.range = autoBow.getRange();
        this.aimError = autoBow.getAimError();
        this.missChance = autoBow.getMissChance();
        this.smoothSpeed = autoBow.getSmoothSpeed();
        this.legitMode = autoBow.isLegitMode();
        this.rotate = autoBow.isRotate();
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int y = height / 2 - 60;

        // Range slider (5-50 blocks)
        this.addDrawableChild(new SliderWidget(centerX - 100, y, 200, 20, Text.literal("Range: " + range + " blocks"), range / 50.0) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal(String.format("Range: %.1f blocks", range)));
            }
            @Override
            protected void applyValue() {
                range = value * 50;
                autoBow.setRange(range);
            }
        });
        y += 30;

        // Aim Error slider (0-5 degrees)
        this.addDrawableChild(new SliderWidget(centerX - 100, y, 200, 20, Text.literal("Aim Error: " + aimError + "°"), aimError / 5.0) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal(String.format("Aim Error: %.2f°", aimError)));
            }
            @Override
            protected void applyValue() {
                aimError = value * 5;
                autoBow.setAimError(aimError);
            }
        });
        y += 30;

        // Miss Chance slider (0-50%)
        this.addDrawableChild(new SliderWidget(centerX - 100, y, 200, 20, Text.literal("Miss Chance: " + (missChance * 100) + "%"), missChance / 0.5) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal(String.format("Miss Chance: %.0f%%", missChance * 100)));
            }
            @Override
            protected void applyValue() {
                missChance = value * 0.5;
                autoBow.setMissChance(missChance);
            }
        });
        y += 30;

        // Smooth Speed slider (1-15 deg/tick)
        this.addDrawableChild(new SliderWidget(centerX - 100, y, 200, 20, Text.literal("Smooth Speed: " + smoothSpeed + "°/tick"), smoothSpeed / 15.0) {
            @Override
            protected void updateMessage() {
                setMessage(Text.literal(String.format("Smooth Speed: %.1f°/tick", smoothSpeed)));
            }
            @Override
            protected void applyValue() {
                smoothSpeed = value * 15;
                autoBow.setSmoothSpeed(smoothSpeed);
            }
        });
        y += 30;

        // Legit Mode toggle button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Legit Mode: " + (legitMode ? "ON" : "OFF")), button -> {
            legitMode = !legitMode;
            autoBow.setLegitMode(legitMode);
            button.setMessage(Text.literal("Legit Mode: " + (legitMode ? "ON" : "OFF")));
        }).dimensions(centerX - 100, y, 200, 20).build());
        y += 30;

        // Rotate toggle button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Auto Rotate: " + (rotate ? "ON" : "OFF")), button -> {
            rotate = !rotate;
            autoBow.setRotate(rotate);
            button.setMessage(Text.literal("Auto Rotate: " + (rotate ? "ON" : "OFF")));
        }).dimensions(centerX - 100, y, 200, 20).build());
        y += 40;

        // Close button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> this.close())
                .dimensions(centerX - 50, y, 100, 20).build());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawCenteredTextWithShadow(matrices, textRenderer, "AutoBow Settings", width / 2, height / 2 - 80, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }
          }
