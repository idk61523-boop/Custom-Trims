package com.customtrims.screen;

import com.customtrims.TrimConfig;
import com.customtrims.TrimData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class TrimPickerScreen extends Screen {
    private static final int CELL = 22;
    private static final int PER_ROW = 9;

    private final Screen parent;
    private final String itemKey;
    private final Item armorItem;
    private String selPattern;
    private String selMaterial;

    public TrimPickerScreen(Screen parent, String itemKey, Item armorItem) {
        super(Text.literal("Choose trim"));
        this.parent = parent;
        this.itemKey = itemKey;
        this.armorItem = armorItem;
        TrimConfig.Entry e = TrimConfig.get(itemKey);
        if (e != null) { selPattern = e.pattern(); selMaterial = e.material(); }
    }

    @Override
    protected void init() {
        int left = (width - PER_ROW * CELL) / 2;
        int patY = 60;

        for (int i = 0; i < TrimData.PATTERNS.size(); i++) {
            final String p = TrimData.PATTERNS.get(i);
            final var icon = new ItemStack(TrimData.patternIcon(p));
            addDrawableChild(new ButtonWidget(
                    left + (i % PER_ROW) * CELL, patY + (i / PER_ROW) * CELL,
                    CELL - 2, CELL - 2,
                    Text.literal(TrimData.pretty(p)),
                    btn -> selPattern = p,
                    DEFAULT_NARRATION_SUPPLIER) {
                @Override
                public void drawIcon(DrawContext ctx, int mx, int my, float delta) {
                    ctx.drawItem(icon, getX() + 3, getY() + 3);
                    if (p.equals(selPattern)) {
                        int x = getX(), y = getY(), w = getWidth(), h = getHeight(), c = 0xFFFFFF55;
                        ctx.fill(x, y, x + w, y + 1, c);
                        ctx.fill(x, y + h - 1, x + w, y + h, c);
                        ctx.fill(x, y, x + 1, y + h, c);
                        ctx.fill(x + w - 1, y, x + w, y + h, c);
                    }
                }
            });
        }

        int matY = patY + 2 * CELL + 28;

        for (int i = 0; i < TrimData.TRIM_MATERIALS.size(); i++) {
            final String[] m = TrimData.TRIM_MATERIALS.get(i);
            final var icon = new ItemStack(TrimData.item(m[1]));
            addDrawableChild(new ButtonWidget(
                    left + (i % PER_ROW) * CELL, matY + (i / PER_ROW) * CELL,
                    CELL - 2, CELL - 2,
                    Text.literal(TrimData.pretty(m[0])),
                    btn -> selMaterial = m[0],
                    DEFAULT_NARRATION_SUPPLIER) {
                @Override
                public void drawIcon(DrawContext ctx, int mx, int my, float delta) {
                    ctx.drawItem(icon, getX() + 3, getY() + 3);
                    if (m[0].equals(selMaterial)) {
                        int x = getX(), y = getY(), w = getWidth(), h = getHeight(), c = 0xFFFFFF55;
                        ctx.fill(x, y, x + w, y + 1, c);
                        ctx.fill(x, y + h - 1, x + w, y + h, c);
                        ctx.fill(x, y, x + 1, y + h, c);
                        ctx.fill(x + w - 1, y, x + w, y + h, c);
                    }
                }
            });
        }

        int btnY = matY + 2 * CELL + 12;

        addDrawableChild(ButtonWidget.builder(Text.literal("Save"), btn -> {
            if (selPattern != null && selMaterial != null) {
                TrimConfig.set(itemKey, new TrimConfig.Entry(selPattern, selMaterial));
                close();
            }
        }).dimensions(width / 2 - 105, btnY, 68, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), btn -> {
            TrimConfig.remove(itemKey);
            close();
        }).dimensions(width / 2 - 34, btnY, 68, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), btn -> close())
            .dimensions(width / 2 + 37, btnY, 68, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawItem(new ItemStack(armorItem), width / 2 - 8, 14);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("Trims"), width / 2, 48, 0xFFFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("Ores"),
            width / 2, 60 + 2 * CELL + 14, 0xFFFFFFFF);
    }

    @Override
    public void close() { client.setScreen(parent); }
}
