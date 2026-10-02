package com.customtrims;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class InvTrimRenderer {

    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (!(screen instanceof HandledScreen<?> handled)) return;

            ScreenEvents.afterRender(screen).register((scr, context, mouseX, mouseY, delta) -> {
                for (Slot slot : handled.getScreenHandler().slots) {
                    ItemStack stack = slot.getStack();
                    if (stack.isEmpty()) continue;
                    if (stack.contains(DataComponentTypes.TRIM)) continue;

                    String key = net.minecraft.registry.Registries.ITEM
                        .getId(stack.getItem()).getPath();
                    TrimConfig.Entry entry = TrimConfig.get(key);
                    if (entry == null) continue;

                    if (client.world == null) continue;
                    DynamicRegistryManager reg = client.world.getRegistryManager();

                    Optional<RegistryEntry.Reference<ArmorTrimPattern>> pat = reg
                        .getOrThrow(RegistryKeys.TRIM_PATTERN)
                        .getOptional(RegistryKey.of(RegistryKeys.TRIM_PATTERN,
                            Identifier.of("minecraft", entry.pattern())));
                    Optional<RegistryEntry.Reference<ArmorTrimMaterial>> mat = reg
                        .getOrThrow(RegistryKeys.TRIM_MATERIAL)
                        .getOptional(RegistryKey.of(RegistryKeys.TRIM_MATERIAL,
                            Identifier.of("minecraft", entry.material())));

                    if (pat.isEmpty() || mat.isEmpty()) continue;

                    ItemStack copy = stack.copy();
                    copy.set(DataComponentTypes.TRIM, new ArmorTrim(mat.get(), pat.get()));
                    // slot.x и slot.y уже содержат абсолютные координаты относительно окна
                    // drawItem рисует иконку размером 16x16, слот тоже 16x16
                    context.drawItem(copy, handled.x + slot.x, handled.y + slot.y);
                }
            });
        });
    }
}
