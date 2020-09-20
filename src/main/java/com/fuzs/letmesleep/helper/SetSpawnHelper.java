package com.fuzs.letmesleep.helper;

import com.fuzs.letmesleep.handler.ConfigBuildHandler;
import com.fuzs.letmesleep.util.SetSpawnPoint;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SetSpawnHelper {

    public static TranslationTextComponent createRespawnMessage() {

        ITextComponent itextcomponent = TextComponentUtils.func_240647_a_(new TranslationTextComponent("multiplayer.spawn.confirm"))
                .func_240700_a_(component -> component.func_240712_a_(TextFormatting.GREEN).func_240715_a_(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, ""))
                        .func_240716_a_(new HoverEvent(HoverEvent.Action.field_230550_a_, new TranslationTextComponent("multiplayer.spawn.tooltip"))));

        return new TranslationTextComponent("multiplayer.spawn.message", itextcomponent);

    }

    @SuppressWarnings("ConstantConditions")
    public static boolean isNewSpawnAllowed(World world, PlayerEntity player, @Nonnull BlockPos bed, @Nullable SetSpawnPoint type) {

        if (type != null && type != ConfigBuildHandler.GENERAL_CONFIG.setSpawn.get()) {
            return false;
        }

        BlockPos spawn = player.getBedPosition().orElse(null);
        boolean flag1 = !bed.equals(spawn);
        boolean flag2 = ConfigBuildHandler.GENERAL_CONFIG.setSpawnAlways.get() || spawn == null
                || !PlayerEntity.func_234567_a_(world.getServer().func_241755_D_(), spawn, false, false).isPresent();

        return flag1 && flag2;

    }

}
