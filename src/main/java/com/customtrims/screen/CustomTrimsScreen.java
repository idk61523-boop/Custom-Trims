package com.customtrims.screen;

import com.customtrims.TrimConfig;
import com.customtrims.TrimData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

public class CustomTrimsScreen extends Screen {
    private static final int CELL = 24;
    private final Screen parent;

    private record ArmorEntry(ButtonWidget btn, String key, ItemStack icon) {}
    private final List<ArmorEntry> entries = new ArrayList<>();

    public CustomTrimsScreen(Screen parent) {
        super(Text.literal("Custom Trims"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        entries.clear();
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
                ButtonWidget btn = ButtonWidget.builder(Text.empty(),
                        b -> client.setScreen(new TrimPickerScreen(this, key, armorItem)))
                    .dimensions(left + c * CELL, top + r * CELL, CELL - 2, CELL - 2)
                    .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(
                        Text.literal(TrimData.pretty(mat) + " " + TrimData.pretty(slot))))
                    .build();
                addDrawableChild(btn);
                entries.add(new ArmorEntry(btn, key, new ItemStack(armorItem)));
            }
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
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

        for (ArmorEntry e : entries) {
            int x = e.btn().getX(), y = e.btn().getY();
            ctx.drawItem(e.icon(), x + 4, y + 4);
            if (TrimConfig.get(e.key()) != null)
                ctx.fill(x + CELL - 8, y + 2, x + CELL - 4, y + 6, 0xFF55FF55);
        }
    }

    @Override
    public void close() { client.setScreen(parent); }
}
