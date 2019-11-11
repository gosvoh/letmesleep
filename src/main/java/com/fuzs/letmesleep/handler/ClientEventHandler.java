package com.fuzs.letmesleep.handler;

import com.fuzs.letmesleep.helper.ReflectionHelper;
import com.fuzs.letmesleep.helper.TimeFormatHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ClockItem;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {

    private final Minecraft mc = Minecraft.getInstance();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent evt) {

        if (ConfigBuildHandler.WAKE_UP_CONFIG.persistentChat.get() && this.mc.currentScreen instanceof SleepInMultiplayerScreen && evt.getGui() == null) {

            TextFieldWidget textfield = ReflectionHelper.getInputField((SleepInMultiplayerScreen) this.mc.currentScreen);

            if (textfield != null) {

                String s = textfield.getText().trim();

                if (!s.isEmpty()) {

                    evt.setGui(new ChatScreen(s));

                }

            }

        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onMakeTooltip(ItemTooltipEvent evt) {

        if (ConfigBuildHandler.SLEEP_TIMINGS_CONFIG.timeClock.get() && this.mc.world != null && evt.getItemStack().getItem() instanceof ClockItem) {

            long time = this.mc.world.getDayTime();
            String s = TimeFormatHelper.formatTime(time);
            evt.getToolTip().add(1, new StringTextComponent(s).setStyle(new Style().setColor(TextFormatting.GRAY)));

        }

    }

}
