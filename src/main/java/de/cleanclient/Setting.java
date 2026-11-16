package de.cleanclient;

public class Setting {
    public final String name;
    public final boolean isBool;
    public boolean b;
    public double v, min, max;
    public float anim;

    private Setting(String name, boolean isBool) {
        this.name = name;
        this.isBool = isBool;
    }

    public static Setting bool(String name, boolean def) {
        Setting s = new Setting(name, true);
        s.b = def;
        s.anim = def ? 1f : 0f;
        return s;
    }

    public static Setting number(String name, double def, double min, double max) {
        Setting s = new Setting(name, false);
        s.v = def;
        s.min = min;
        s.max = max;
        return s;
    }
}
