package com.fuzs.letmesleep.handler;

import com.fuzs.letmesleep.helper.ReflectionHelper;
import com.fuzs.letmesleep.helper.SetSpawnHelper;
import com.fuzs.letmesleep.network.NetworkHandler;
import com.fuzs.letmesleep.network.message.MessageRequestSpawn;
import com.fuzs.letmesleep.util.SetSpawnPoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.List;
import java.util.Optional;

public class SetSpawnHandler {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onSetSpawn(PlayerSetSpawnEvent evt) {

        boolean flag = ConfigBuildHandler.generalConfig.setSpawn == SetSpawnPoint.BUTTON || ConfigBuildHandler.generalConfig.setSpawn == SetSpawnPoint.CHAT;
        if (flag && evt.getEntityPlayer().world.isRemote && evt.getNewSpawn() != null && evt.isForced()) {

            MessageRequestSpawn message = new MessageRequestSpawn(evt.getNewSpawn());
            NetworkHandler.sendToServer(message);

        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onInitGuiPost(GuiScreenEvent.InitGuiEvent.Post evt) {

        if (evt.getGui() instanceof GuiSleepMP && this.mc.player != null) {

            EntityPlayerSP player = this.mc.player;
            BlockPos pos = player.bedLocation;

            if (pos != null && SetSpawnHelper.isNewSpawnAllowed(player.world, player, pos, SetSpawnPoint.BUTTON)) {

                GuiSleepMP screen = (GuiSleepMP) evt.getGui();
                GuiButton setSpawn = new GuiButton(5, screen.width / 2 - 100, screen.height - 64, 200, 20,
                        new TextComponentTranslation("multiplayer.spawn.button").getFormattedText());
                evt.getButtonList().add(setSpawn);

            }

        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post evt) {

        if (evt.getGui() instanceof GuiSleepMP && this.mc.player != null) {

            EntityPlayerSP player = this.mc.player;
            BlockPos pos = player.bedLocation;

            if (evt.getButton().id == 5 && pos != null) {

                evt.getButton().visible = false;
                MessageRequestSpawn message = new MessageRequestSpawn(pos);
                NetworkHandler.sendToServer(message);

            }

        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onMouseClickedPre(GuiScreenEvent.MouseInputEvent.Pre evt) {

        if (evt.getGui() instanceof GuiChat) {

            if (Mouse.getEventButton() == 0) {

                ITextComponent itextcomponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

                if (itextcomponent != null) {

                    evt.setCanceled(this.handleComponentClicked(itextcomponent));

                }

            }

        }

    }

    private boolean handleComponentClicked(ITextComponent itextcomponent) {

        if (itextcomponent != null) {

            ClickEvent clickevent = itextcomponent.getStyle().getClickEvent();

            if (!GuiScreen.isShiftKeyDown() && clickevent != null) {

                if (clickevent.getAction() == ClickEvent.Action.CHANGE_PAGE) {

                    EntityPlayerSP player = this.mc.player;
                    BlockPos pos = player.bedLocation;

                    if (pos != null) {

                        MessageRequestSpawn message = new MessageRequestSpawn(pos);
                        NetworkHandler.sendToServer(message);
                        this.removeSpawnMessage();

                    }

                    return true;

                }

            }

        }

        return false;

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent evt) {

        if (this.mc.currentScreen instanceof GuiSleepMP) {

            this.removeSpawnMessage();

        }

    }

    private void removeSpawnMessage() {

        GuiNewChat chat = this.mc.ingameGUI.getChatGUI();
        List<ChatLine> chatLines = ReflectionHelper.getChatLines(chat);
        List<ChatLine> drawnChatLines = ReflectionHelper.getDrawnChatLines(chat);

        if (chatLines != null && drawnChatLines != null) {

            Optional.ofNullable(TextFormatting.getTextWithoutFormattingCodes(SetSpawnHelper.createRespawnMessage().getUnformattedText())).ifPresent(template -> {

                chatLines.removeIf(chatline -> template.equals(TextFormatting.getTextWithoutFormattingCodes(chatline.getChatComponent().getUnformattedText())));
                drawnChatLines.removeIf(chatline -> template.equals(TextFormatting.getTextWithoutFormattingCodes(chatline.getChatComponent().getUnformattedText())));

            });

        }

    }

}
