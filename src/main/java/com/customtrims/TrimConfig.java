package com.customtrims;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** Ключ = id предмета без namespace, например "netherite_chestplate". */
public final class TrimConfig {
    public record Entry(String pattern, String material) {}

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("customtrims.json");
    private static Map<String, Entry> entries = new LinkedHashMap<>();

    private TrimConfig() {}

    public static void load() {
        if (!Files.exists(FILE)) return;
        try (Reader r = Files.newBufferedReader(FILE)) {
            Map<String, Entry> m = GSON.fromJson(r, new TypeToken<Map<String, Entry>>() {}.getType());
            if (m != null) entries = new LinkedHashMap<>(m);
        } catch (Exception e) {
            System.err.println("[CustomTrims] Failed to read config: " + e);
        }
    }

    public static void save() {
        try (Writer w = Files.newBufferedWriter(FILE)) {
            GSON.toJson(entries, w);
        } catch (Exception e) {
            System.err.println("[CustomTrims] Failed to save config: " + e);
        }
    }

    public static Entry get(String itemKey) { return entries.get(itemKey); }
    public static void set(String itemKey, Entry e) { entries.put(itemKey, e); save(); }
    public static void remove(String itemKey) { entries.remove(itemKey); save(); }
}
