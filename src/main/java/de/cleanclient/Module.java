package de.cleanclient;

import java.util.ArrayList;
import java.util.List;

public class Module {
    public final String name;
    public final String category;
    public boolean enabled;
    public boolean expanded;
    public float anim;
    public final List<Setting> settings = new ArrayList<>();

    public Module(String name, String category, boolean enabled) {
        this.name = name;
        this.category = category;
        this.enabled = enabled;
        this.anim = enabled ? 1f : 0f;
    }

    public Module add(Setting s) {
        settings.add(s);
        return this;
    }

    public Setting get(String settingName) {
        for (Setting s : settings) if (s.name.equals(settingName)) return s;
        return null;
    }

    public boolean bool(String settingName) {
        Setting s = get(settingName);
        return s != null && s.b;
    }

    public double num(String settingName) {
        Setting s = get(settingName);
        return s == null ? 0 : s.v;
    }
}
