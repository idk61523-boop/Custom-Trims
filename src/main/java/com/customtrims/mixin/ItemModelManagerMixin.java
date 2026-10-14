package com.customtrims.mixin;

import com.customtrims.ShieldApplier;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemModelManager.class)
public abstract class ItemModelManagerMixin {

    @WrapMethod(method = "update(Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/world/World;Lnet/minecraft/util/HeldItemContext;I)V")
    private void customtrims$wrapUpdate(ItemRenderState state, ItemStack stack, ItemDisplayContext dc,
                                        World world, HeldItemContext ctx, int seed, Operation<Void> original) {
        boolean prev = ShieldApplier.active;
        if (ctx != null && (Object) ctx == (Object) MinecraftClient.getInstance().player) {
            ShieldApplier.active = true;
        }
        try {
            original.call(state, stack, dc, world, ctx, seed);
        } finally {
            ShieldApplier.active = prev;
        }
    }

    @WrapMethod(method = "clearAndUpdate(Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/world/World;Lnet/minecraft/util/HeldItemContext;I)V")
    private void customtrims$wrapClearAndUpdate(ItemRenderState state, ItemStack stack, ItemDisplayContext dc,
                                                World world, HeldItemContext ctx, int seed, Operation<Void> original) {
        boolean prev = ShieldApplier.active;
        if (ctx != null && (Object) ctx == (Object) MinecraftClient.getInstance().player) {
            ShieldApplier.active = true;
        }
        try {
            original.call(state, stack, dc, world, ctx, seed);
        } finally {
            ShieldApplier.active = prev;
        }
    }
}
