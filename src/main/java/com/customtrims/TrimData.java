package com.customtrims;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

public final class TrimData {
    private TrimData() {}

    public static final List<String> SLOTS = List.of("helmet", "chestplate", "leggings", "boots");
    public static final List<String> ARMOR_MATERIALS =
        List.of("leather", "chainmail", "iron", "golden", "diamond", "netherite", "turtle");

    public static final List<String> PATTERNS = List.of(
        "sentry", "dune", "coast", "wild", "ward", "eye", "vex", "tide", "snout",
        "rib", "spire", "wayfinder", "shaper", "silence", "raiser", "host", "flow", "bolt");

    /** trim material id -> предмет-иконка */
    public static final List<String[]> TRIM_MATERIALS = List.of(
        new String[]{"quartz", "quartz"},
        new String[]{"iron", "iron_ingot"},
        new String[]{"netherite", "netherite_ingot"},
        new String[]{"redstone", "redstone"},
        new String[]{"copper", "copper_ingot"},
        new String[]{"gold", "gold_ingot"},
        new String[]{"emerald", "emerald"},
        new String[]{"diamond", "diamond"},
        new String[]{"lapis", "lapis_lazuli"},
        new String[]{"amethyst", "amethyst_shard"},
        new String[]{"resin", "resin_brick"});

    public static Item item(String path) {
        return Registries.ITEM.get(Identifier.of("minecraft", path));
    }

    public static boolean exists(String path) {
        return Registries.ITEM.containsId(Identifier.of("minecraft", path));
    }

    public static Item patternIcon(String pattern) { return item(pattern + "_armor_trim_smithing_template"); }

    public static String pretty(String id) {
        return Character.toUpperCase(id.charAt(0)) + id.substring(1);
    }
}
