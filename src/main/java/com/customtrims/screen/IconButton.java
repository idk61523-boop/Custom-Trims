package com.customtrims.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;

/** Кнопка с иконкой предмета. Подсветка выбранного и маркер "настроено". */
public class IconButton extends ButtonWidget {
    private final ItemStack icon;
    private final BooleanSupplier selected;
    private final BooleanSupplier marked;

    public IconButton(int x, int y, int size, Item item, Text tooltip,
                      BooleanSupplier selected, BooleanSupplier marked, PressAction onPress) {
        super(x, y, size, size, tooltip, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.icon = new ItemStack(item);
        this.selected = selected;
        this.marked = marked;
        setTooltip(net.minecraft.client.gui.tooltip.Tooltip.of(tooltip));
    }

    @Override
    protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.renderWidget(ctx, mouseX, mouseY, delta);
        ctx.drawItem(icon, getX() + (getWidth() - 16) / 2, getY() + (getHeight() - 16) / 2);
        if (marked.getAsBoolean()) {
            ctx.fill(getX() + getWidth() - 6, getY() + 2, getX() + getWidth() - 2, getY() + 6, 0xFF55FF55);
        }
        if (selected.getAsBoolean()) {
            ctx.drawBorder(getX(), getY(), getWidth(), getHeight(), 0xFFFFFF55);
        }
    }
}
