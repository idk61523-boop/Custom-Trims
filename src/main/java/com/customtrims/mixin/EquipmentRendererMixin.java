package com.customtrims.mixin;

import com.customtrims.TrimApplier;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EquipmentRenderer.class)
public abstract class EquipmentRendererMixin {
    // Подменяем ItemStack брони на копию с TRIM-компонентом ТОЛЬКО на время отрисовки.
    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    private ItemStack customtrims$applyTrim(ItemStack stack, @Local(argsOnly = true) Object state) {
        return TrimApplier.apply(stack, state);
    }
}
