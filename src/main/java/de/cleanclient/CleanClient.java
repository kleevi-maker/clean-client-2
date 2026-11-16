package de.cleanclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;

public class CleanClient implements ClientModInitializer {
    private static KeyBinding openMenu;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();

        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("cleanclient", "main"));
        openMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cleanclient.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new MenuScreen());
            }
        });

        HudRenderCallback.EVENT.register((ctx, tickCounter) -> Hud.render(ctx));
    }

    /** Akzentfarbe (ARGB) aus dem Design-Modul. */
    public static int accent() {
        Module d = ModuleManager.get("Design");
        float hue = d == null ? 0.58f : (float) d.num("Akzent-Farbton") / 360f;
        return 0xFF000000 | (Color.HSBtoRGB(hue, 0.6f, 1f) & 0xFFFFFF);
    }
}
