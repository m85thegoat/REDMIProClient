package com.redmiproclient;

import com.redmiproclient.modules.*;
import com.redmiproclient.gui.ClickGUI;
import com.redmiproclient.gui.AutoBowSettingsGUI;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class RedmiProClient implements ModInitializer {
    public static RedmiProClient INSTANCE;
    public static KeyBinding openGuiKey;
    public static KeyBinding panicKey;
    public static KeyBinding toggleAutoBowKey;
    public static KeyBinding openAutoBowSettingsKey;

    // Modules
    public KillAura killAura;
    public AutoCrystal autoCrystal;
    public AutoEat autoEat;
    public AutoMace autoMace;
    public AutoScaffold autoScaffold;
    public AutoArmor autoArmor;
    public Velocity velocity;
    public ESP esp;
    public CrystalDeflector crystalDeflector;
    public AutoRun autoRun;
    public FastRender fastRender;
    public EntityCulling entityCulling;
    public ParticleMultiplier particleMultiplier;
    public NoFog noFog;
    public Hitbox hitbox;
    public Reach reach;
    public AutoPot autoPot;
    public AutoPearl autoPearl;
    public AutoTotem autoTotem;
    public AutoTool autoTool;
    public AutoWeapon autoWeapon;
    public AimAssist aimAssist;
    public AntiBot antiBot;
    public Coordinates coordinates;
    public AutoBow autoBow;

    @Override
    public void onInitialize() {
        INSTANCE = this;

        // Initialize modules
        killAura = new KillAura();
        autoCrystal = new AutoCrystal();
        autoEat = new AutoEat();
        autoMace = new AutoMace();
        autoScaffold = new AutoScaffold();
        autoArmor = new AutoArmor();
        velocity = new Velocity();
        esp = new ESP();
        crystalDeflector = new CrystalDeflector();
        autoRun = new AutoRun();
        fastRender = new FastRender();
        entityCulling = new EntityCulling();
        particleMultiplier = new ParticleMultiplier();
        noFog = new NoFog();
        hitbox = new Hitbox();
        reach = new Reach();
        autoPot = new AutoPot();
        autoPearl = new AutoPearl();
        autoTotem = new AutoTotem();
        autoTool = new AutoTool();
        autoWeapon = new AutoWeapon();
        aimAssist = new AimAssist();
        antiBot = new AntiBot();
        coordinates = new Coordinates();
        autoBow = new AutoBow();

        // Keybinds
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.redmiproclient.openGui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.redmiproclient"
        ));
        panicKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.redmiproclient.panic",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            "category.redmiproclient"
        ));
        toggleAutoBowKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.redmiproclient.toggleAutoBow",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_U,
            "category.redmiproclient"
        ));
        openAutoBowSettingsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.redmiproclient.openAutoBowSettings",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "category.redmiproclient",
            InputUtil.MOD_CONTROL
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (panicKey.wasPressed()) {
                disableAllModules();
                client.player.sendMessage(Text.literal("§c[Panic] All modules disabled!"), false);
            }
            while (openGuiKey.wasPressed()) {
                client.setScreen(new ClickGUI());
            }
            while (toggleAutoBowKey.wasPressed()) {
                autoBow.toggle();
                client.player.sendMessage(Text.literal("§eAutoBow " + (autoBow.isEnabled() ? "§aenabled" : "§cdisabled")), true);
            }
            while (openAutoBowSettingsKey.wasPressed()) {
                client.setScreen(new AutoBowSettingsGUI());
            }

            killAura.onTick();
            autoCrystal.onTick();
            autoEat.onTick();
            autoMace.onTick();
            autoScaffold.onTick();
            autoArmor.onTick();
            velocity.onTick();
            crystalDeflector.onTick();
            autoRun.onTick();
            fastRender.onTick();
            entityCulling.onTick();
            particleMultiplier.onTick();
            noFog.onTick();
            hitbox.onTick();
            reach.onTick();
            autoPot.onTick();
            autoPearl.onTick();
            autoTotem.onTick();
            autoTool.onTick();
            autoWeapon.onTick();
            aimAssist.onTick();
            antiBot.onTick();
            coordinates.onTick();
            autoBow.onTick();
        });

        WorldRenderEvents.LAST.register(context -> {
            if (client.player == null) return;
            esp.onRender(context.matrixStack());
            coordinates.onRender(context.matrixStack());
        });

        System.out.println("REDMI PRO Client loaded!");
    }

    private void disableAllModules() {
        killAura.setEnabled(false);
        autoCrystal.setEnabled(false);
        autoEat.setEnabled(false);
        autoMace.setEnabled(false);
        autoScaffold.setEnabled(false);
        autoArmor.setEnabled(false);
        velocity.setEnabled(false);
        esp.setEnabled(false);
        crystalDeflector.setEnabled(false);
        autoRun.setEnabled(false);
        fastRender.setEnabled(false);
        entityCulling.setEnabled(false);
        particleMultiplier.setEnabled(false);
        noFog.setEnabled(false);
        hitbox.setEnabled(false);
        reach.setEnabled(false);
        autoPot.setEnabled(false);
        autoPearl.setEnabled(false);
        autoTotem.setEnabled(false);
        autoTool.setEnabled(false);
        autoWeapon.setEnabled(false);
        aimAssist.setEnabled(false);
        antiBot.setEnabled(false);
        coordinates.setEnabled(false);
        autoBow.setEnabled(false);
    }
  }
