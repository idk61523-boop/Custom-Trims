package com.customtrims;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.Optional;

public final class TrimApplier {
    private TrimApplier() {}

    /** Возвращает копию стака с визуальным тримом, либо исходный стак. Ничего не отправляется на сервер. */
    public static ItemStack apply(ItemStack stack, Object state) {
        if (stack == null || stack.isEmpty() || stack.contains(DataComponentTypes.TRIM)) return stack;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return stack;
        // только на самом игроке, чтобы не менять чужую броню
        if (!(state instanceof PlayerEntityRenderState ps) || ps.id != mc.player.getId()) return stack;

        TrimConfig.Entry e = TrimConfig.get(Registries.ITEM.getId(stack.getItem()).getPath());
        if (e == null) return stack;

        DynamicRegistryManager reg = mc.world.getRegistryManager();
        Optional<RegistryEntry.Reference<ArmorTrimPattern>> pattern = reg.getOrThrow(RegistryKeys.TRIM_PATTERN)
            .getOptional(RegistryKey.of(RegistryKeys.TRIM_PATTERN, Identifier.of("minecraft", e.pattern())));
        Optional<RegistryEntry.Reference<ArmorTrimMaterial>> material = reg.getOrThrow(RegistryKeys.TRIM_MATERIAL)
            .getOptional(RegistryKey.of(RegistryKeys.TRIM_MATERIAL, Identifier.of("minecraft", e.material())));
        if (pattern.isEmpty() || material.isEmpty()) return stack;

        ItemStack copy = stack.copy();
        copy.set(DataComponentTypes.TRIM, new ArmorTrim(material.get(), pattern.get()));
        return copy;
    }
}
