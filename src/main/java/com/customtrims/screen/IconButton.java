package com.customtrims.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;

public class IconButton extends ButtonWidget {
    private final ItemStack icon;
    private final BooleanSupplier selected;
    private final BooleanSupplier marked;

    public IconButton(int x, int y, int size, Item item, Text tooltip,
                      BooleanSupplier selected, BooleanSupplier marked,
                      ButtonWidget.PressAction onPress) {
        super(x, y, size, size, tooltip, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.icon = new ItemStack(item);
        this.selected = selected;
        this.marked = marked;
        setTooltip(net.minecraft.client.gui.tooltip.Tooltip.of(tooltip));
    }

    @Override
    protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        if (selected.getAsBoolean() || isHovered()) {
            ctx.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x44FFFFFF);
        }
        ctx.drawItem(icon, getX() + (getWidth() - 16) / 2, getY() + (getHeight() - 16) / 2);
        if (marked.getAsBoolean()) {
            ctx.fill(getX() + getWidth() - 6, getY() + 2,
                     getX() + getWidth() - 2, getY() + 6, 0xFF55FF55);
        }
        if (selected.getAsBoolean()) {
            int x = getX(), y = getY(), w = getWidth(), h = getHeight(), c = 0xFFFFFF55;
            ctx.fill(x,         y,         x + w, y + 1,     c);
            ctx.fill(x,         y + h - 1, x + w, y + h,     c);
            ctx.fill(x,         y,         x + 1, y + h,     c);
            ctx.fill(x + w - 1, y,         x + w, y + h,     c);
        }
    }
}
