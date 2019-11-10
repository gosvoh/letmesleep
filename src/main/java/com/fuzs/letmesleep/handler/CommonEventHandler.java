package com.fuzs.letmesleep.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonEventHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onSleepingTimeCheck(SleepingTimeCheckEvent evt) {

        PlayerEntity player = evt.getPlayer();

        if (!player.world.isRemote) {

            long time = player.world.getDayTime() % 24000L;
            long start = ConfigBuildHandler.SLEEP_TIMINGS_CONFIG.bedtimeStart.get();
            long end = ConfigBuildHandler.SLEEP_TIMINGS_CONFIG.bedtimeEnd.get();
            boolean flag = end < start ? start <= time || time <= end : start <= time && time <= end;

            boolean thunder = ConfigBuildHandler.SLEEP_TIMINGS_CONFIG.bedtimeThunder.get() && player.world.getWorldInfo().isThundering();
            boolean rain = ConfigBuildHandler.SLEEP_TIMINGS_CONFIG.bedtimeRain.get() && player.world.getWorldInfo().isRaining();

            evt.setResult(flag || thunder || rain ? Event.Result.ALLOW : Event.Result.DENY);

        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onSetSpawn(PlayerSetSpawnEvent evt) {

        PlayerEntity player = evt.getPlayer();
        boolean flag = !player.world.isRemote && !evt.isForced();

        if (flag && evt.getNewSpawn() != null && !evt.getNewSpawn().equals(player.getBedLocation(player.dimension))) {

            player.sendStatusMessage(new TranslationTextComponent("block.minecraft.bed.spawn_set"), true);

        }

    }

}
