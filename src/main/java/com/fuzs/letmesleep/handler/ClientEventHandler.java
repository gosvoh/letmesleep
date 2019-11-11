package com.fuzs.letmesleep.handler;

import com.fuzs.letmesleep.helper.ReflectionHelper;
import com.fuzs.letmesleep.helper.TimeFormatHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemClock;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEventHandler {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent evt) {

        if (ConfigBuildHandler.wakeUpConfig.persistentChat && this.mc.currentScreen instanceof GuiSleepMP && evt.getGui() == null) {

            GuiTextField textfield = ReflectionHelper.getInputField((GuiSleepMP) this.mc.currentScreen);

            if (textfield != null) {

                String s = textfield.getText().trim();

                if (!s.isEmpty()) {

                    evt.setGui(new GuiChat(s));

                }

            }

        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onMakeTooltip(ItemTooltipEvent evt) {

        if (ConfigBuildHandler.sleepTimingsConfig.timeClock && this.mc.world != null && evt.getItemStack().getItem() instanceof ItemClock) {

            long time = this.mc.world.getWorldTime();
            String s = TimeFormatHelper.formatTime(time);
            evt.getToolTip().add(1, new TextComponentString(s).setStyle(new Style().setColor(TextFormatting.GRAY)).getFormattedText());

        }

    }

}
