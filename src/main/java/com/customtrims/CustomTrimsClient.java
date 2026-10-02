package com.customtrims;

import com.customtrims.screen.CustomTrimsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class CustomTrimsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TrimConfig.load();
        InvTrimRenderer.register();
        // Кнопка "Custom Trims" в Options -> Skin Customization (внешний вид)
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof SkinOptionsScreen) {
                Screens.getButtons(screen).add(
                    ButtonWidget.builder(Text.literal("Custom Trims"),
                            b -> client.setScreen(new CustomTrimsScreen(screen)))
                        .dimensions(6, scaledHeight - 26, 100, 20)
                        .build());
            }
        });
    }
}
