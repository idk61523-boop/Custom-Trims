package com.customtrims.screen;

import com.customtrims.TrimConfig;
import com.customtrims.TrimData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

public class TrimPickerScreen extends Screen {
    private static final int CELL = 22;
    private static final int PER_ROW = 9;

    private final Screen parent;
    private final String itemKey;
    private final Item armorItem;
    private String selPattern;
    private String selMaterial;

    private record IconEntry(ButtonWidget btn, ItemStack icon, String id, boolean isPattern) {}
    private final List<IconEntry> iconEntries = new ArrayList<>();

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
        iconEntries.clear();
        int left = (width - PER_ROW * CELL) / 2;
        int patY = 60;

        for (int i = 0; i < TrimData.PATTERNS.size(); i++) {
            final String p = TrimData.PATTERNS.get(i);
            ButtonWidget btn = ButtonWidget.builder(Text.empty(), b -> selPattern = p)
                .dimensions(left + (i % PER_ROW) * CELL, patY + (i / PER_ROW) * CELL, CELL - 2, CELL - 2)
                .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal(TrimData.pretty(p))))
                .build();
            addDrawableChild(btn);
            iconEntries.add(new IconEntry(btn, new ItemStack(TrimData.patternIcon(p)), p, true));
        }

        int matY = patY + 2 * CELL + 28;

        for (int i = 0; i < TrimData.TRIM_MATERIALS.size(); i++) {
            final String[] m = TrimData.TRIM_MATERIALS.get(i);
            ButtonWidget btn = ButtonWidget.builder(Text.empty(), b -> selMaterial = m[0])
                .dimensions(left + (i % PER_ROW) * CELL, matY + (i / PER_ROW) * CELL, CELL - 2, CELL - 2)
                .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal(TrimData.pretty(m[0]))))
                .build();
            addDrawableChild(btn);
            iconEntries.add(new IconEntry(btn, new ItemStack(TrimData.item(m[1])), m[0], false));
        }

        int btnY = matY + 2 * CELL + 12;

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
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("Ores"),
            width / 2, 60 + 2 * CELL + 14, 0xFFFFFFFF);

        for (IconEntry e : iconEntries) {
            ctx.drawItem(e.icon(), e.btn().getX() + 3, e.btn().getY() + 3);
            boolean sel = e.isPattern() ? e.id().equals(selPattern) : e.id().equals(selMaterial);
            if (sel) {
                int x = e.btn().getX(), y = e.btn().getY(), w = e.btn().getWidth(), h = e.btn().getHeight(), clr = 0xFFFFFF55;
                ctx.fill(x,         y,         x + w, y + 1,     clr);
                ctx.fill(x,         y + h - 1, x + w, y + h,     clr);
                ctx.fill(x,         y,         x + 1, y + h,     clr);
                ctx.fill(x + w - 1, y,         x + w, y + h,     clr);
            }
        }
    }

    @Override
    public void close() { client.setScreen(parent); }
}
