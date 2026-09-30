package com.customtrims.screen;

import com.customtrims.TrimConfig;
import com.customtrims.TrimData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class CustomTrimsScreen extends Screen {
    private static final int CELL = 24;
    private final Screen parent;

    public CustomTrimsScreen(Screen parent) {
        super(Text.literal("Custom Trims"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cols = TrimData.ARMOR_MATERIALS.size();
        int left = (width - cols * CELL) / 2;
        int top = 50;

        for (int r = 0; r < TrimData.SLOTS.size(); r++) {
            for (int c = 0; c < cols; c++) {
                final String mat = TrimData.ARMOR_MATERIALS.get(c);
                final String slot = TrimData.SLOTS.get(r);
                final String key = mat + "_" + slot;
                if (!TrimData.exists(key)) continue;

                final var armorItem = TrimData.item(key);

                addDrawableChild(new ButtonWidget(left + c * CELL, top + r * CELL, CELL - 2, CELL - 2,
                        Text.literal(TrimData.pretty(mat) + " " + TrimData.pretty(slot)),
                        btn -> client.setScreen(new TrimPickerScreen(this, key, armorItem)),
                        DEFAULT_NARRATION_SUPPLIER) {
                    @Override
                    public void drawIcon(DrawContext ctx, int mx, int my, float delta) {
                        ctx.drawItem(new ItemStack(armorItem), getX() + 4, getY() + 4);
                        if (TrimConfig.get(key) != null) {
                            ctx.fill(getX() + getWidth() - 6, getY() + 2,
                                     getX() + getWidth() - 2, getY() + 6, 0xFF55FF55);
                        }
                    }
                });
            }
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), btn -> close())
            .dimensions(width / 2 - 50, top + TrimData.SLOTS.size() * CELL + 16, 100, 20)
            .build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, 16, 0xFFFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("Click armor to choose trim. Green dot = trim set."),
            width / 2, 30, 0xFFAAAAAA);
    }

    @Override
    public void close() { client.setScreen(parent); }
}
