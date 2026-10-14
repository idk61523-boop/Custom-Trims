package com.customtrims.mixin;

import com.customtrims.ShieldApplier;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.render.item.model.special.ShieldModelRenderer;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShieldModelRenderer.class)
public abstract class ShieldModelRendererMixin {

    @ModifyReturnValue(
        method = "getData(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/component/ComponentMap;",
        at = @At("RETURN")
    )
    private ComponentMap customtrims$banner(ComponentMap original, ItemStack stack) {
        return ShieldApplier.modify(original, stack);
    }
}
