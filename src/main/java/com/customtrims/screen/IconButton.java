package com.customtrims.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;

import java.util.function.BooleanSupplier;

public class IconButton extends PressableWidget {
    private final ItemStack icon;
    private final MutableText label;
    private final BooleanSupplier selected;
    private final BooleanSupplier marked;
    private final Runnable onPress;

    public IconButton(int x, int y, int size, Item item, MutableText tooltip,
                      BooleanSupplier selected, BooleanSupplier marked, Runnable onPress) {
        super(x, y, size, size, tooltip);
        this.icon = new ItemStack(item);
        this.label = tooltip;
        this.selected = selected;
        this.marked = marked;
        this.onPress = onPress;
        setTooltip(net.minecraft.client.gui.tooltip.Tooltip.of(tooltip));
    }

    @Override
    public void onPress() {
        onPress.run();
    }

    @Override
    public void drawIcon(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.drawItem(icon, getX() + (getWidth() - 16) / 2, getY() + (getHeight() - 16) / 2);
        if (marked.getAsBoolean()) {
            ctx.fill(getX() + getWidth() - 6, getY() + 2, getX() + getWidth() - 2, getY() + 6, 0xFF55FF55);
        }
        if (selected.getAsBoolean()) {
            int x = getX(); int y = getY(); int w = getWidth(); int h = getHeight(); int c = 0xFFFFFF55;
            ctx.fill(x,         y,         x + w, y + 1,     c);
            ctx.fill(x,         y + h - 1, x + w, y + h,     c);
            ctx.fill(x,         y,         x + 1, y + h,     c);
            ctx.fill(x + w - 1, y,         x + w, y + h,     c);
        }
    }

    @Override
    protected MutableText getNarrationMessage() {
        return label.copy();
    }
}
