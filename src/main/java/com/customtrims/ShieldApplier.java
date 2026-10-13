package com.customtrims;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public final class ShieldApplier {
    public record Built(DyeColor base, BannerPatternsComponent patterns) {}

    private static Built cache;
    private static Object cacheRegistry;
    private static int cacheVersion = -1;

    private ShieldApplier() {}

    /** Возвращает копию щита с баннером из конфига либо исходный стак. */
    public static ItemStack apply(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return stack;
        if (!"shield".equals(Registries.ITEM.getId(stack.getItem()).getPath())) return stack;
        if (ShieldConfig.get() == null) return stack;
        if (hasRealBanner(stack)) return stack;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return stack;
        DynamicRegistryManager reg = mc.world.getRegistryManager();

        if (cache == null || cacheRegistry != reg || cacheVersion != ShieldConfig.version()) {
            cache = build(reg, ShieldConfig.get());
            cacheRegistry = reg;
            cacheVersion = ShieldConfig.version();
        }
        return withBuilt(stack, cache);
    }

    private static boolean hasRealBanner(ItemStack stack) {
        BannerPatternsComponent p = stack.get(DataComponentTypes.BANNER_PATTERNS);
        return stack.get(DataComponentTypes.BASE_COLOR) != null
            || (p != null && !p.equals(BannerPatternsComponent.DEFAULT));
    }

    public static Built build(DynamicRegistryManager reg, ShieldConfig.Data data) {
        DyeColor base = DyeColor.byId(data.base(), DyeColor.WHITE);
        BannerPatternsComponent.Builder builder = new BannerPatternsComponent.Builder();
        if (data.layers() != null) {
            for (ShieldConfig.Layer layer : data.layers()) {
                DyeColor color = DyeColor.byId(layer.color(), DyeColor.WHITE);
                reg.getOrThrow(RegistryKeys.BANNER_PATTERN)
                    .getOptional(RegistryKey.of(RegistryKeys.BANNER_PATTERN,
                        Identifier.of("minecraft", layer.pattern())))
                    .ifPresent(entry -> builder.add(entry, color));
            }
        }
        return new Built(base, builder.build());
    }

    public static ItemStack withBuilt(ItemStack stack, Built built) {
        ItemStack copy = stack.copy();
        copy.set(DataComponentTypes.BASE_COLOR, built.base());
        copy.set(DataComponentTypes.BANNER_PATTERNS, built.patterns());
        return copy;
    }
}
