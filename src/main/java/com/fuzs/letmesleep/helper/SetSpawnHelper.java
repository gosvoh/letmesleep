package com.fuzs.letmesleep.helper;

import com.fuzs.letmesleep.handler.ConfigBuildHandler;
import com.fuzs.letmesleep.util.SetSpawnPoint;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SetSpawnHelper {

    public static TextComponentTranslation createRespawnMessage() {

        Style style = new Style().setColor(TextFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, ""))
                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("multiplayer.spawn.tooltip")));
        ITextComponent itextcomponent = new TextComponentTranslation("multiplayer.spawn.confirm").setStyle(style);

        return new TextComponentTranslation("multiplayer.spawn.message", itextcomponent);

    }

    @SuppressWarnings("ConstantConditions")
    public static boolean isNewSpawnAllowed(World world, EntityPlayer player, @Nonnull BlockPos bed, @Nullable SetSpawnPoint type) {

        if (type != null && type != ConfigBuildHandler.generalConfig.setSpawn) {
            return false;
        }

        BlockPos spawn = player.getBedLocation(player.dimension);
        boolean flag1 = !bed.equals(spawn);
        boolean flag2 = ConfigBuildHandler.generalConfig.setSpawnAlways || spawn == null
                || EntityPlayer.getBedSpawnLocation(world, spawn, false) == null;

        return flag1 && flag2;

    }

}
