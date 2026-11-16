package de.cleanclient;

import net.minecraft.client.gui.DrawContext;

/** Kleine Zeichenhilfen: runde Ecken, Schatten, Farbmischung. */
public class Render {

    public static int alpha(int color, float a) {
        int al = Math.max(0, Math.min(255, (int) (((color >>> 24) & 0xFF) * a)));
        return (al << 24) | (color & 0xFFFFFF);
    }

    public static int lerp(int a, int b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int al = (int) (((a >>> 24) & 0xFF) + (((b >>> 24) & 0xFF) - ((a >>> 24) & 0xFF)) * t);
        int r = (int) (((a >> 16) & 0xFF) + (((b >> 16) & 0xFF) - ((a >> 16) & 0xFF)) * t);
        int g = (int) (((a >> 8) & 0xFF) + (((b >> 8) & 0xFF) - ((a >> 8) & 0xFF)) * t);
        int bl = (int) ((a & 0xFF) + ((b & 0xFF) - (a & 0xFF)) * t);
        return (al << 24) | (r << 16) | (g << 8) | bl;
    }

    /** Gefülltes Rechteck mit abgerundeten Ecken. */
    public static void rect(DrawContext c, int x, int y, int w, int h, int r, int color) {
        if ((color >>> 24) == 0) return;
        r = Math.min(r, Math.min(w, h) / 2);
        if (r <= 0) {
            c.fill(x, y, x + w, y + h, color);
            return;
        }
        c.fill(x, y + r, x + w, y + h - r, color);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = (int) Math.round(r - Math.sqrt(r * r - dy * dy));
            c.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
            c.fill(x + inset, y + h - i - 1, x + w - inset, y + h - i, color);
        }
    }

    /** Weicher Schatten aus mehreren transparenten Schichten. */
    public static void shadow(DrawContext c, int x, int y, int w, int h, int r, int size, float fade) {
        for (int i = size; i >= 1; i--) {
            rect(c, x - i, y - i + 3, w + 2 * i, h + 2 * i, r + i, alpha(0x0E000000, fade));
        }
    }

    /** Leuchten um ein Element in der Akzentfarbe. */
    public static void glow(DrawContext c, int x, int y, int w, int h, int r, int color, float fade) {
        for (int i = 3; i >= 1; i--) {
            rect(c, x - i, y - i, w + 2 * i, h + 2 * i, r + i, alpha((color & 0xFFFFFF) | 0x14000000, fade));
        }
    }
}
