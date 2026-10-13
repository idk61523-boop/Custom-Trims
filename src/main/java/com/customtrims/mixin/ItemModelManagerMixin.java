package com.customtrims.mixin;

import com.customtrims.ShieldApplier;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemModelManager.class)
public abstract class ItemModelManagerMixin {

    @ModifyVariable(
        method = "update(Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/world/World;Lnet/minecraft/util/HeldItemContext;I)V",
        at = @At("HEAD"),
        argsOnly = true
    )
    private ItemStack customtrims$shieldUpdate(ItemStack stack, @Local(argsOnly = true) HeldItemContext ctx) {
        if (ctx == null || (Object) ctx != (Object) MinecraftClient.getInstance().player) return stack;
        return ShieldApplier.apply(stack);
    }

    @ModifyVariable(
        method = "clearAndUpdate(Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/world/World;Lnet/minecraft/util/HeldItemContext;I)V",
        at = @At("HEAD"),
        argsOnly = true
    )
    private ItemStack customtrims$shieldClearUpdate(ItemStack stack, @Local(argsOnly = true) HeldItemContext ctx) {
        if (ctx == null || (Object) ctx != (Object) MinecraftClient.getInstance().player) return stack;
        return ShieldApplier.apply(stack);
    }
}
