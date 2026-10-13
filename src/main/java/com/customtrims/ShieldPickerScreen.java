package com.customtrims.screen;

import com.customtrims.ShieldApplier;
import com.customtrims.ShieldConfig;
import com.customtrims.TrimData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShieldPickerScreen extends Screen {
    private static final int LAYERS = 6;

    private final Screen parent;
    private List<String> patterns = new ArrayList<>();
    private boolean loaded = false;
    private int base = 0;
    private final int[] layerPat = new int[LAYERS];
    private final int[] layerCol = new int[LAYERS];

    private final List<ButtonWidget> baseButtons = new ArrayList<>();
    private final ButtonWidget[] colorButtons = new ButtonWidget[LAYERS];
    private final ItemStack[] dyeIcons = new ItemStack[16];
    private ItemStack shieldStack;
    private int leftX, topY;

    public ShieldPickerScreen(Screen parent) {
        super(Text.literal("Shield banner"));
        this.parent = parent;
    }

    private static String pretty(String id) {
        String s = id.replace('_', ' ');
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private int step(int cur, int dir) {
        int n = patterns.size();
        int v = cur + dir;
        if (v < -1) v = n - 1;
        if (v >= n) v = -1;
        return v;
    }

    private ShieldConfig.Data currentData() {
        List<ShieldConfig.Layer> ls = new ArrayList<>();
        for (int i = 0; i < LAYERS; i++) {
            if (layerPat[i] >= 0) {
                ls.add(new ShieldConfig.Layer(patterns.get(layerPat[i]), DyeColor.byIndex(layerCol[i]).getId()));
            }
        }
        return new ShieldConfig.Data(DyeColor.byIndex(base).getId(), ls);
    }

    @Override
    protected void init() {
        baseButtons.clear();
        topY = Math.max(4, (height - 232) / 2);
        leftX = width / 2 - 150;

        if (client == null || client.world == null) {
            addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> close())
                .dimensions(width / 2 - 50, height / 2 + 10, 100, 20).build());
            return;
        }

        if (patterns.isEmpty()) {
            patterns = client.world.getRegistryManager()
                .getOrThrow(RegistryKeys.BANNER_PATTERN)
                .streamEntries()
                .filter(e -> e.registryKey().getValue().getNamespace().equals("minecraft"))
                .map(e -> e.registryKey().getValue().getPath())
                .filter(p -> !p.equals("base"))
                .sorted()
                .toList();
        }

        shieldStack = new ItemStack(TrimData.item("shield"));
        for (int i = 0; i < 16; i++) {
            dyeIcons[i] = new ItemStack(TrimData.item(DyeColor.byIndex(i).getId() + "_dye"));
        }

        if (!loaded) {
            loaded = true;
            Arrays.fill(layerPat, -1);
            ShieldConfig.Data d = ShieldConfig.get();
            if (d != null) {
                base = DyeColor.byId(d.base(), DyeColor.WHITE).getIndex();
                if (d.layers() != null) {
                    for (int i = 0; i < LAYERS && i < d.layers().size(); i++) {
                        ShieldConfig.Layer l = d.layers().get(i);
                        layerPat[i] = patterns.indexOf(l.pattern());
                        layerCol[i] = DyeColor.byId(l.color(), DyeColor.WHITE).getIndex();
                    }
                }
            }
        }

        // Цвет основы: 16 красителей
        int baseY = topY + 28;
        for (int i = 0; i < 16; i++) {
            final int idx = i;
            ButtonWidget btn = ButtonWidget.builder(Text.empty(), b -> base = idx)
                .dimensions(leftX + (i % 8) * 20, baseY + (i / 8) * 20, 18, 18)
                .tooltip(Tooltip.of(Text.literal(pretty(DyeColor.byIndex(i).getId()))))
                .build();
            addDrawableChild(btn);
            baseButtons.add(btn);
        }

        // 6 слоёв: [<] название [>] [цвет] [x]
        int rowsY = topY + 82;
        for (int r = 0; r < LAYERS; r++) {
            final int row = r;
            int y = rowsY + r * 20;
            addDrawableChild(ButtonWidget.builder(Text.literal("<"), b -> layerPat[row] = step(layerPat[row], -1))
                .dimensions(leftX, y, 16, 18).build());
            addDrawableChild(ButtonWidget.builder(Text.literal(">"), b -> layerPat[row] = step(layerPat[row], 1))
                .dimensions(leftX + 120, y, 16, 18).build());
            colorButtons[r] = ButtonWidget.builder(Text.empty(), b -> layerCol[row] = (layerCol[row] + 1) % 16)
                .dimensions(leftX + 140, y, 20, 18)
                .tooltip(Tooltip.of(Text.literal("Layer color")))
                .build();
            addDrawableChild(colorButtons[r]);
            addDrawableChild(ButtonWidget.builder(Text.literal("x"), b -> layerPat[row] = -1)
                .dimensions(leftX + 162, y, 16, 18).build());
        }

        int btnY = rowsY + LAYERS * 20 + 6;
        addDrawableChild(ButtonWidget.builder(Text.literal("Save"), b -> {
            ShieldConfig.set(currentData());
            close();
        }).dimensions(width / 2 - 105, btnY, 68, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), b -> {
            ShieldConfig.clear();
            close();
        }).dimensions(width / 2 - 34, btnY, 68, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), b -> close())
            .dimensions(width / 2 + 37, btnY, 68, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        if (client == null || client.world == null) {
            ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("Join a world to edit the shield banner."),
                width / 2, height / 2 - 10, 0xFFFFFFFF);
            return;
        }

        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, topY + 4, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, Text.literal("Base color"), leftX, topY + 16, 0xFFAAAAAA);
        ctx.drawTextWithShadow(textRenderer, Text.literal("Layers"), leftX, topY + 70, 0xFFAAAAAA);

        // Иконки красителей и рамка выбранного цвета
        for (int i = 0; i < baseButtons.size(); i++) {
            ButtonWidget btn = baseButtons.get(i);
            ctx.drawItem(dyeIcons[i], btn.getX() + 1, btn.getY() + 1);
            if (i == base) {
                int x = btn.getX(), y = btn.getY(), w = btn.getWidth(), h = btn.getHeight(), clr = 0xFFFFFF55;
                ctx.fill(x,         y,         x + w, y + 1,     clr);
                ctx.fill(x,         y + h - 1, x + w, y + h,     clr);
                ctx.fill(x,         y,         x + 1, y + h,     clr);
                ctx.fill(x + w - 1, y,         x + w, y + h,     clr);
            }
        }

        // Названия паттернов и цвета слоёв
        int rowsY = topY + 82;
        for (int r = 0; r < LAYERS; r++) {
            int y = rowsY + r * 20;
            String name = layerPat[r] < 0 ? "-" : pretty(patterns.get(layerPat[r]));
            ctx.enableScissor(leftX + 18, y, leftX + 118, y + 18);
            ctx.drawTextWithShadow(textRenderer, Text.literal(name), leftX + 20, y + 5, 0xFFFFFFFF);
            ctx.disableScissor();
            ButtonWidget cb = colorButtons[r];
            ctx.drawItem(dyeIcons[layerCol[r]], cb.getX() + 2, cb.getY() + 1);
        }

        // Большое превью щита (увеличенная иконка)
        ShieldApplier.Built built = ShieldApplier.build(client.world.getRegistryManager(), currentData());
        ItemStack preview = ShieldApplier.withBuilt(shieldStack, built);
        int px = width / 2 + 45;
        int py = topY + 36;
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("Preview"), px + 40, topY + 22, 0xFFAAAAAA);
        var m = ctx.getMatrices();
        m.pushMatrix();
        m.translate((float) px, (float) py);
        m.scale(5f, 5f);
        ctx.drawItem(preview, 0, 0);
        m.popMatrix();
    }

    @Override
    public void close() { client.setScreen(parent); }
}
