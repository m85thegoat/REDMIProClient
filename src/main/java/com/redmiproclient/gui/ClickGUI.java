package com.redmiproclient.gui;

import com.redmiproclient.RedmiProClient;
import com.redmiproclient.modules.Module;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends Screen {
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 24;
    private static final int CATEGORY_WIDTH = 150;
    
    private int startX, startY;
    private float hue = 0f;
    private int mouseX, mouseY;
    private long lastRenderTime = 0;
    private String searchQuery = "";
    private boolean showSearch = false;
    private int selectedCategory = 0;
    
    private final String[] categories = {"Combat", "Movement", "Render", "World", "Misc"};
    
    private List<Module> getModulesInCategory(String category) {
        List<Module> result = new ArrayList<>();
        switch (category) {
            case "Combat":
                result.add(RedmiProClient.INSTANCE.killAura);
                result.add(RedmiProClient.INSTANCE.autoCrystal);
                result.add(RedmiProClient.INSTANCE.autoBow);
                result.add(RedmiProClient.INSTANCE.crystalDeflector);
                result.add(RedmiProClient.INSTANCE.autoMace);
                result.add(RedmiProClient.INSTANCE.autoPot);
                result.add(RedmiProClient.INSTANCE.autoPearl);
                result.add(RedmiProClient.INSTANCE.autoTotem);
                result.add(RedmiProClient.INSTANCE.autoWeapon);
                result.add(RedmiProClient.INSTANCE.aimAssist);
                result.add(RedmiProClient.INSTANCE.antiBot);
                result.add(RedmiProClient.INSTANCE.hitbox);
                result.add(RedmiProClient.INSTANCE.reach);
                break;
            case "Movement":
                result.add(RedmiProClient.INSTANCE.autoRun);
                result.add(RedmiProClient.INSTANCE.autoScaffold);
                break;
            case "Render":
                result.add(RedmiProClient.INSTANCE.esp);
                result.add(RedmiProClient.INSTANCE.fastRender);
                result.add(RedmiProClient.INSTANCE.entityCulling);
                result.add(RedmiProClient.INSTANCE.particleMultiplier);
                result.add(RedmiProClient.INSTANCE.noFog);
                result.add(RedmiProClient.INSTANCE.coordinates);
                break;
            case "World":
                result.add(RedmiProClient.INSTANCE.autoEat);
                result.add(RedmiProClient.INSTANCE.autoArmor);
                result.add(RedmiProClient.INSTANCE.autoTool);
                break;
            case "Misc":
                result.add(RedmiProClient.INSTANCE.velocity);
                break;
        }
        return result;
    }

    public ClickGUI() {
        super(Text.literal("REDMI PRO Client"));
    }

    @Override
    protected void init() {
        startX = width / 2 - CATEGORY_WIDTH / 2;
        startY = height / 2 - 150;
        lastRenderTime = System.currentTimeMillis();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        renderBackground(matrices);
        
        long now = System.currentTimeMillis();
        float deltaTime = (now - lastRenderTime) / 1000f;
        lastRenderTime = now;
        hue += 0.003f * deltaTime * 60;
        if (hue > 1f) hue -= 1f;
        
        String title = "§l§nREDMI PRO CLIENT";
        drawCenteredTextWithShadow(matrices, textRenderer, title, width / 2, startY - 35, getRainbowColor(0.5f));
        
        String version = "v1.0.0 | Anti-Cheat Bypass Edition";
        drawCenteredTextWithShadow(matrices, textRenderer, version, width / 2, startY - 20, 0xAAAAAA);
        
        // Category tabs
        int categoryTabWidth = (CATEGORY_WIDTH - 20) / categories.length;
        int categoryStartX = startX + 10;
        for (int i = 0; i < categories.length; i++) {
            int x = categoryStartX + i * categoryTabWidth;
            int color = (selectedCategory == i) ? getRainbowColor(0.7f) : 0x444444;
            fill(matrices, x, startY - 10, x + categoryTabWidth - 2, startY + 5, color);
            drawCenteredTextWithShadow(matrices, textRenderer, categories[i], x + (categoryTabWidth - 2) / 2, startY - 7, 0xFFFFFF);
        }
        
        // Search bar
        int searchY = startY + 15;
        int searchX = startX + 10;
        fill(matrices, searchX, searchY, searchX + CATEGORY_WIDTH - 20, searchY + 18, 0x66000000);
        drawTextWithShadow(matrices, textRenderer, showSearch ? searchQuery : "🔍 Search...", searchX + 5, searchY + 4, 0xAAAAAA);
        
        // Modules list
        List<Module> modules = getModulesInCategory(categories[selectedCategory]);
        if (!searchQuery.isEmpty()) {
            modules = new ArrayList<>();
            for (Module m : getAllModules()) {
                if (m.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                    modules.add(m);
                }
            }
        }
        
        int y = startY + 45;
        for (Module module : modules) {
            if (y + BUTTON_HEIGHT > height - 50) break;
            drawRainbowButton(matrices, startX, y, module.getName(), module.isEnabled());
            y += BUTTON_HEIGHT + 5;
        }
        
        super.render(matrices, mouseX, mouseY, delta);
    }

    private List<Module> getAllModules() {
        List<Module> all = new ArrayList<>();
        for (String cat : categories) {
            all.addAll(getModulesInCategory(cat));
        }
        return all;
    }

    private void drawRainbowButton(MatrixStack matrices, int x, int y, String text, boolean active) {
        int color;
        if (active) {
            Color c = Color.getHSBColor(hue, 0.8f, 0.8f);
            color = c.getRGB();
        } else {
            color = 0xFFAAAAAA;
        }
        
        if (isHovering(x, y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
            if (active) {
                Color c = new Color(color);
                Color brighter = c.brighter();
                color = brighter.getRGB();
            } else {
                color = 0xFFCCCCCC;
            }
        }
        
        fill(matrices, x, y, x + BUTTON_WIDTH, y + BUTTON_HEIGHT, color);
        drawCenteredTextWithShadow(matrices, textRenderer, text, x + BUTTON_WIDTH / 2, y + 5, 0xFFFFFF);
    }

    private boolean isHovering(int x, int y, int w, int h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    private int getRainbowColor(float offset) {
        float h = (hue + offset) % 1.0f;
        return Color.HSBtoRGB(h, 0.8f, 0.8f);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Category tabs
        int categoryTabWidth = (CATEGORY_WIDTH - 20) / categories.length;
        int categoryStartX = startX + 10;
        for (int i = 0; i < categories.length; i++) {
            int x = categoryStartX + i * categoryTabWidth;
            if (mouseX >= x && mouseX <= x + categoryTabWidth - 2 && mouseY >= startY - 10 && mouseY <= startY + 5) {
                selectedCategory = i;
                return true;
            }
        }
        
        // Search bar
        int searchY = startY + 15;
        int searchX = startX + 10;
        if (mouseX >= searchX && mouseX <= searchX + CATEGORY_WIDTH - 20 && mouseY >= searchY && mouseY <= searchY + 18) {
            showSearch = true;
            return true;
        }
        
        // Module toggles
        List<Module> modules = getModulesInCategory(categories[selectedCategory]);
        if (!searchQuery.isEmpty()) {
            modules = new ArrayList<>();
            for (Module m : getAllModules()) {
                if (m.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                    modules.add(m);
                }
            }
        }
        int y = startY + 45;
        for (Module module : modules) {
            if (mouseX >= startX && mouseX <= startX + BUTTON_WIDTH && mouseY >= y && mouseY <= y + BUTTON_HEIGHT) {
                module.toggle();
                return true;
            }
            y += BUTTON_HEIGHT + 5;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
        }
