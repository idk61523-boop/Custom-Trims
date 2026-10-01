package com.customtrims.mixin;

import com.customtrims.TrimConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @ModifyVariable(method = "renderItem", at = @At("HEAD"), argsOnly = true)
    private static ItemStack customtrims$injectTrim(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return stack;
        if (stack.contains(DataComponentTypes.TRIM)) return stack;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return stack;

        String key = net.minecraft.registry.Registries.ITEM
            .getId(stack.getItem()).getPath();
        TrimConfig.Entry entry = TrimConfig.get(key);
        if (entry == null) return stack;

        DynamicRegistryManager reg = mc.world.getRegistryManager();
        Optional<RegistryEntry.Reference<ArmorTrimPattern>> pat = reg
            .getOrThrow(RegistryKeys.TRIM_PATTERN)
            .getOptional(RegistryKey.of(RegistryKeys.TRIM_PATTERN,
                Identifier.of("minecraft", entry.pattern())));
        Optional<RegistryEntry.Reference<ArmorTrimMaterial>> mat = reg
            .getOrThrow(RegistryKeys.TRIM_MATERIAL)
            .getOptional(RegistryKey.of(RegistryKeys.TRIM_MATERIAL,
                Identifier.of("minecraft", entry.material())));

        if (pat.isEmpty() || mat.isEmpty()) return stack;

        ItemStack copy = stack.copy();
        copy.set(DataComponentTypes.TRIM, new ArmorTrim(mat.get(), pat.get()));
        return copy;
    }
}
