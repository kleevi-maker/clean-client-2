package de.cleanclient;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.util.Locale;

public class Hud {
    private static final int BG = 0x99101014;

    public static void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;
        TextRenderer tr = mc.textRenderer;
        int y = 8;

        Module fps = ModuleManager.get("FPS");
        if (fps.enabled) {
            line(ctx, tr, 8, y, mc.getCurrentFps() + " FPS", fps.bool("Hintergrund"));
            y += 18;
        }

        Module coords = ModuleManager.get("Koordinaten");
        if (coords.enabled) {
            String t = String.format(Locale.ROOT, "XYZ %.0f / %.0f / %.0f",
                    mc.player.getX(), mc.player.getY(), mc.player.getZ());
            line(ctx, tr, 8, y, t, coords.bool("Hintergrund"));
            y += 18;
        }

        Module dir = ModuleManager.get("Blickrichtung");
        if (dir.enabled) {
            float yaw = MathHelper.wrapDegrees(mc.player.getYaw());
            String[] names = {"Süd", "Südwest", "West", "Nordwest", "Nord", "Nordost", "Ost", "Südost"};
            int idx = Math.floorMod(Math.round(yaw / 45f), 8);
            line(ctx, tr, 8, y, names[idx], dir.bool("Hintergrund"));
            y += 18;
        }

        Module keys = ModuleManager.get("Keystrokes");
        if (keys.enabled) {
            int s = (int) keys.num("Größe");
            int g = 3;
            int x = 8;
            int ky = ctx.getScaledWindowHeight() - (s * 2 + g) - 8;
            key(ctx, tr, x + s + g, ky, s, "W", mc.options.forwardKey.isPressed());
            key(ctx, tr, x, ky + s + g, s, "A", mc.options.leftKey.isPressed());
            key(ctx, tr, x + s + g, ky + s + g, s, "S", mc.options.backKey.isPressed());
            key(ctx, tr, x + (s + g) * 2, ky + s + g, s, "D", mc.options.rightKey.isPressed());
        }
    }

    private static void line(DrawContext ctx, TextRenderer tr, int x, int y, String text, boolean bg) {
        int w = tr.getWidth(text);
        int accent = CleanClient.accent();
        if (bg) {
            Render.shadow(ctx, x - 5, y - 4, w + 10, 15, 5, 3, 1f);
            Render.rect(ctx, x - 5, y - 4, w + 10, 15, 5, BG);
            ctx.fillGradient(x - 3, y - 3, x + w + 3, y + 4, 0x12FFFFFF, 0x00FFFFFF);
            Render.rect(ctx, x - 5, y - 2, 2, 11, 1, accent);
        }
        ctx.drawTextWithShadow(tr, text, x, y, 0xFFFFFFFF);
    }

    private static void key(DrawContext ctx, TextRenderer tr, int x, int y, int s, String label, boolean down) {
        int accent = CleanClient.accent();
        Render.shadow(ctx, x, y, s, s, 5, 3, 1f);
        if (down) Render.glow(ctx, x, y, s, s, 5, accent, 1f);
        Render.rect(ctx, x, y, s, s, 5, down ? Render.alpha(accent, 0.9f) : BG);
        if (!down) ctx.fillGradient(x + 2, y + 2, x + s - 2, y + s / 2, 0x14FFFFFF, 0x00FFFFFF);
        int tx = x + (s - tr.getWidth(label)) / 2;
        int ty = y + (s - 8) / 2;
        ctx.drawTextWithShadow(tr, label, tx, ty, down ? 0xFF101014 : 0xFFFFFFFF);
    }
}
