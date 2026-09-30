package com.customtrims.screen;

import com.customtrims.TrimConfig;
import com.customtrims.TrimData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Главное меню: строки = части брони, столбцы = материал брони. */
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
                String mat = TrimData.ARMOR_MATERIALS.get(c);
                String slot = TrimData.SLOTS.get(r);
                String key = mat + "_" + slot;
                if (!TrimData.exists(key)) continue; // turtle есть только у шлема

                var item = TrimData.item(key);
                addDrawableChild(new IconButton(left + c * CELL, top + r * CELL, CELL - 2, item,
                    Text.literal(TrimData.pretty(mat) + " " + TrimData.pretty(slot)),
                    () -> false,
                    () -> TrimConfig.get(key) != null,
                    b -> client.setScreen(new TrimPickerScreen(this, key, item))));
            }
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
            .dimensions(width / 2 - 50, top + TrimData.SLOTS.size() * CELL + 16, 100, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, 16, 0xFFFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("Click armor to choose a trim. Green dot = custom trim set."),
            width / 2, 30, 0xFFAAAAAA);
    }

    @Override
    public void close() { client.setScreen(parent); }
}
