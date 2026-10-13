package com.customtrims;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Баннер на щите. Хранится в config/customtrims_shield.json */
public final class ShieldConfig {
    public record Layer(String pattern, String color) {}
    public record Data(String base, List<Layer> layers) {}

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("customtrims_shield.json");
    private static Data data = null;
    private static int version = 0;

    private ShieldConfig() {}

    public static void load() {
        if (!Files.exists(FILE)) return;
        try (Reader r = Files.newBufferedReader(FILE)) {
            data = GSON.fromJson(r, Data.class);
            version++;
        } catch (Exception e) {
            System.err.println("[CustomTrims] Failed to read shield config: " + e);
        }
    }

    private static void save() {
        if (data == null) return;
        try (Writer w = Files.newBufferedWriter(FILE)) {
            GSON.toJson(data, w);
        } catch (Exception e) {
            System.err.println("[CustomTrims] Failed to save shield config: " + e);
        }
    }

    public static Data get() { return data; }
    public static int version() { return version; }

    public static void set(Data d) {
        data = d;
        version++;
        save();
    }

    public static void clear() {
        data = null;
        version++;
        try { Files.deleteIfExists(FILE); } catch (Exception ignored) {}
    }
}
