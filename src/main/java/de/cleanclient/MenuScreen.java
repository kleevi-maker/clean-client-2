package de.cleanclient;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MenuScreen extends Screen {
    private static final int PW = 400, PH = 250, SIDE = 96, ROW = 22, SROW = 20;

    private String category = ModuleManager.CATEGORIES.get(0);
    private final List<Hit> hits = new ArrayList<>();
    private Hit dragging;
    private float open = 0f;
    private float fade = 0f;
    private long last = System.nanoTime();

    private record Hit(int x, int y, int w, int h, String cat, Module m, Setting s) {
        boolean contains(double mx, double my) {
            return mx >= x && mx < x + w && my >= y && my < y + h;
        }
    }

    public MenuScreen() {
        super(Text.literal("Clean Client"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fillGradient(0, 0, width, height, Render.alpha(0x88000000, fade), Render.alpha(0x44000000, fade));
    }

    private int a(int color) {
        return Render.alpha(color, fade);
    }

    private void txt(DrawContext ctx, String s, int x, int y, int color) {
        int c = a(color);
        if ((c >>> 24) < 10) return;
        ctx.drawTextWithShadow(textRenderer, s, x, y, c);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        long now = System.nanoTime();
        float dt = Math.min(0.1f, (now - last) / 1e9f);
        last = now;
        open = Math.min(1f, open + dt * 5f);
        fade = 1f - (float) Math.pow(1f - open, 3);
        float follow = Math.min(1f, dt * 14f);

        renderBackground(ctx, mouseX, mouseY, delta);
        hits.clear();

        Module design = ModuleManager.get("Design");
        int alpha = (int) (design.num("Menü-Deckkraft") / 100.0 * 255);
        int accent = CleanClient.accent();
        int px = (width - PW) / 2;
        int py = (height - PH) / 2 + (int) ((1f - fade) * 16);

        // Panel mit Schatten, Glanz und leuchtender Akzentkante
        Render.shadow(ctx, px, py, PW, PH, 8, 8, fade);
        Render.rect(ctx, px, py, PW, PH, 8, a((alpha << 24) | 0x121218));
        ctx.fillGradient(px + 8, py + 4, px + PW - 8, py + PH / 2, a(0x16FFFFFF), 0x00FFFFFF);
        Render.glow(ctx, px + 24, py, PW - 48, 2, 1, accent, fade);
        Render.rect(ctx, px + 24, py, PW - 48, 2, 1, a(accent));

        // Seitenleiste
        Render.rect(ctx, px + 6, py + 8, SIDE - 6, PH - 16, 6, a((alpha << 24) | 0x0C0C10));
        txt(ctx, "CLEAN CLIENT", px + 14, py + 18, accent);

        int cy = py + 40;
        for (String c : ModuleManager.CATEGORIES) {
            boolean sel = c.equals(category);
            Hit h = new Hit(px + 10, cy, SIDE - 14, 20, c, null, null);
            hits.add(h);
            if (sel) {
                Render.rect(ctx, h.x, h.y, h.w, h.h, 5, a((accent & 0xFFFFFF) | 0x50000000));
                Render.rect(ctx, h.x, h.y + 4, 2, h.h - 8, 1, a(accent));
            } else if (h.contains(mouseX, mouseY)) {
                Render.rect(ctx, h.x, h.y, h.w, h.h, 5, a(0x22FFFFFF));
            }
            txt(ctx, c, h.x + 10, h.y + 6, sel ? 0xFFFFFFFF : 0xFFAAAAAA);
            cy += 24;
        }

        // Module und Einstellungen
        int mx0 = px + SIDE + 8, mw = PW - SIDE - 18;
        int y = py + 12;
        for (Module m : ModuleManager.MODULES) {
            if (!m.category.equals(category)) continue;
            m.anim += ((m.enabled ? 1f : 0f) - m.anim) * follow;

            Hit h = new Hit(mx0, y, mw, ROW, null, m, null);
            hits.add(h);
            boolean hover = h.contains(mouseX, mouseY);
            Render.rect(ctx, h.x, h.y, h.w, h.h, 6, a(hover ? 0x3CFFFFFF : 0x22FFFFFF));
            if (m.anim > 0.02f) {
                Render.rect(ctx, h.x, h.y + 4, 2, h.h - 8, 1, Render.alpha(accent, m.anim * fade));
            }
            txt(ctx, m.name, h.x + 10, h.y + 7, Render.lerp(0xFFAAAAAA, 0xFFFFFFFF, m.anim));
            if (!m.settings.isEmpty()) {
                txt(ctx, m.expanded ? "v" : ">", h.x + h.w - 44, h.y + 7, 0xFF888888);
            }
            pill(ctx, h.x + h.w - 30, h.y + 6, m.anim, accent);
            y += ROW + 3;

            if (m.expanded) {
                for (Setting s : m.settings) {
                    Hit sh = new Hit(mx0 + 8, y, mw - 8, SROW, null, m, s);
                    hits.add(sh);
                    Render.rect(ctx, sh.x, sh.y, sh.w, sh.h, 5, a(0x16FFFFFF));
                    txt(ctx, s.name, sh.x + 8, sh.y + 6, 0xFFCCCCCC);
                    if (s.isBool) {
                        s.anim += ((s.b ? 1f : 0f) - s.anim) * follow;
                        pill(ctx, sh.x + sh.w - 30, sh.y + 5, s.anim, accent);
                    } else {
                        int bx = sh.x + 90, bw = sh.w - 106;
                        double t = (s.v - s.min) / (s.max - s.min);
                        int fill = (int) (bw * t);
                        Render.rect(ctx, bx, sh.y + 8, bw, 4, 2, a(0x55FFFFFF));
                        Render.rect(ctx, bx, sh.y + 8, Math.max(4, fill), 4, 2, a(accent));
                        Render.glow(ctx, bx + fill - 3, sh.y + 5, 6, 10, 3, accent, fade);
                        Render.rect(ctx, bx + fill - 3, sh.y + 5, 6, 10, 3, a(0xFFFFFFFF));
                        String val = String.format(Locale.ROOT, "%.0f", s.v);
                        txt(ctx, val, sh.x + sh.w - 8 - textRenderer.getWidth(val), sh.y + 6, 0xFF999999);
                    }
                    y += SROW + 3;
                }
            }
        }

        txt(ctx, "Linksklick: an/aus  |  Rechtsklick: Einstellungen", px + SIDE + 12, py + PH - 16, 0xFF666666);
    }

    /** Animierter Schalter: Knopf gleitet, Farbe blendet über. */
    private void pill(DrawContext ctx, int x, int y, float t, int accent) {
        int track = Render.lerp(0xFF444450, accent, t);
        if (t > 0.05f) Render.glow(ctx, x, y, 22, 10, 5, accent, t * fade);
        Render.rect(ctx, x, y, 22, 10, 5, a(track));
        int kx = x + 1 + Math.round(12 * t);
        Render.rect(ctx, kx, y + 1, 8, 8, 4, a(0xFFFFFFFF));
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mx = click.x(), my = click.y();
        int button = click.button();
        for (int i = hits.size() - 1; i >= 0; i--) {
            Hit h = hits.get(i);
            if (!h.contains(mx, my)) continue;
            if (h.cat != null) {
                category = h.cat;
            } else if (h.s == null) {
                if (button == 0) h.m.enabled = !h.m.enabled;
                else if (button == 1) h.m.expanded = !h.m.expanded;
            } else if (h.s.isBool) {
                h.s.b = !h.s.b;
            } else {
                dragging = h;
                slide(mx);
            }
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (dragging != null) {
            slide(click.x());
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        dragging = null;
        return super.mouseReleased(click);
    }

    private void slide(double mx) {
        Setting s = dragging.s;
        int bx = dragging.x + 90, bw = dragging.w - 106;
        double t = MathHelper.clamp((mx - bx) / bw, 0, 1);
        s.v = Math.round(s.min + t * (s.max - s.min));
    }

    @Override
    public void removed() {
        ModuleManager.save();
    }
}
