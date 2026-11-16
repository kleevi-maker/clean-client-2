package de.cleanclient;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ModuleManager {
    public static final List<Module> MODULES = new ArrayList<>();
    public static final List<String> CATEGORIES = List.of("HUD", "Client");

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("cleanclient.properties");
    }

    public static void init() {
        MODULES.add(new Module("FPS", "HUD", true)
                .add(Setting.bool("Hintergrund", true)));
        MODULES.add(new Module("Koordinaten", "HUD", true)
                .add(Setting.bool("Hintergrund", true)));
        MODULES.add(new Module("Blickrichtung", "HUD", false)
                .add(Setting.bool("Hintergrund", true)));
        MODULES.add(new Module("Keystrokes", "HUD", true)
                .add(Setting.number("Größe", 20, 14, 32)));
        MODULES.add(new Module("Design", "Client", true)
                .add(Setting.number("Akzent-Farbton", 210, 0, 360))
                .add(Setting.number("Menü-Deckkraft", 85, 40, 100)));
        load();
    }

    public static Module get(String name) {
        for (Module m : MODULES) if (m.name.equals(name)) return m;
        return null;
    }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) return;
        Properties p = new Properties();
        try (Reader r = Files.newBufferedReader(f)) {
            p.load(r);
        } catch (IOException e) {
            return;
        }
        for (Module m : MODULES) {
            String en = p.getProperty(m.name + ".enabled");
            if (en != null) m.enabled = Boolean.parseBoolean(en);
            for (Setting s : m.settings) {
                String val = p.getProperty(m.name + "." + s.name);
                if (val == null) continue;
                try {
                    if (s.isBool) s.b = Boolean.parseBoolean(val);
                    else s.v = Double.parseDouble(val);
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    public static void save() {
        Properties p = new Properties();
        for (Module m : MODULES) {
            p.setProperty(m.name + ".enabled", String.valueOf(m.enabled));
            for (Setting s : m.settings) {
                p.setProperty(m.name + "." + s.name, s.isBool ? String.valueOf(s.b) : String.valueOf(s.v));
            }
        }
        try (Writer w = Files.newBufferedWriter(file())) {
            p.store(w, "Clean Client");
        } catch (IOException ignored) {
        }
    }
}
