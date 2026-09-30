package com.customtrims.screen;

import com.customtrims.TrimConfig;
import com.customtrims.TrimData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/** Окно выбора Trim (шаблон) и Ore (материал) для одной брони. */
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

    private int gridLeft() { return (width - PER_ROW * CELL) / 2; }

    @Override
    protected void init() {
        int left = gridLeft();
        int y = 60;

        for (int i = 0; i < TrimData.PATTERNS.size(); i++) {
            String p = TrimData.PATTERNS.get(i);
            addDrawableChild(new IconButton(left + (i % PER_ROW) * CELL, y + (i / PER_ROW) * CELL, CELL - 2,
                TrimData.patternIcon(p), Text.literal(TrimData.pretty(p)),
                () -> p.equals(selPattern), () -> false, b -> selPattern = p));
        }

        int oreY = y + 2 * CELL + 30;
        for (int i = 0; i < TrimData.TRIM_MATERIALS.size(); i++) {
            String[] m = TrimData.TRIM_MATERIALS.get(i);
            addDrawableChild(new IconButton(left + (i % PER_ROW) * CELL, oreY + (i / PER_ROW) * CELL, CELL - 2,
                TrimData.item(m[1]), Text.literal(TrimData.pretty(m[0])),
                () -> m[0].equals(selMaterial), () -> false, b -> selMaterial = m[0]));
        }

        int btnY = oreY + 2 * CELL + 16;
        addDrawableChild(ButtonWidget.builder(Text.literal("Save"), b -> {
            if (selPattern != null && selMaterial != null) {
                TrimConfig.set(itemKey, new TrimConfig.Entry(selPattern, selMaterial));
                close();
            }
        }).dimensions(width / 2 - 105, btnY, 68, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), b -> {
            TrimConfig.remove(itemKey);
            close();
        }).dimensions(width / 2 - 34, btnY, 68, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), b -> close())
            .dimensions(width / 2 + 37, btnY, 68, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawItem(new ItemStack(armorItem), width / 2 - 8, 14);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("Trims"), width / 2, 48, 0xFFFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("Ores"), width / 2, 60 + 2 * CELL + 16, 0xFFFFFFFF);
    }

    @Override
    public void close() { client.setScreen(parent); }
}
