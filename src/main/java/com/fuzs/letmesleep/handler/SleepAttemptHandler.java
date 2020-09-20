package com.fuzs.letmesleep.handler;

import com.fuzs.letmesleep.helper.ReflectionHelper;
import com.fuzs.letmesleep.helper.SetSpawnHelper;
import com.fuzs.letmesleep.helper.TimeFormatHelper;
import com.fuzs.letmesleep.util.SetSpawnPoint;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public class SleepAttemptHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerSleep(PlayerSleepInBedEvent evt) {

        PlayerEntity player = evt.getPlayer();
        World world = player.world;
        BlockPos at = evt.getPos();
        BlockPos spawn = player.getBedPosition().orElse(null);
        Direction direction = world.getBlockState(at).get(HorizontalBlock.HORIZONTAL_FACING);

        if (!world.isRemote) {

            boolean modified = evt.getResultStatus() != null;

            if (SetSpawnHelper.isNewSpawnAllowed(world, player, at, SetSpawnPoint.INTERACT) && !player.isSneaking()) {
                //player.setSpawnPoint(at, false, false, player.dimension); FIXME idk how to fix it
                if (!modified) {
                    evt.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);
                }
                return;
            }

            if (modified) {
                return;
            }

            if (player.isSleeping() || !player.isAlive()) {
                evt.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);
                return;
            }

            //FIXME idk how to fix it too
            if (!world.func_230315_m_().field_235999_c_()) {
                evt.setResult(PlayerEntity.SleepResult.NOT_POSSIBLE_HERE);
                return;
            }

            if (!net.minecraftforge.event.ForgeEventFactory.fireSleepingTimeCheck(player, player.getBedPosition())) {
                this.sendNotPossibleNowMessage(player);
                evt.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);
                return;
            }

            if (ConfigBuildHandler.SLEEP_CONFIG.rangeCheck.get() && !this.bedInRange(player, at, direction)) {
                evt.setResult(PlayerEntity.SleepResult.TOO_FAR_AWAY);
                return;
            }

            if (ConfigBuildHandler.SLEEP_CONFIG.obstructionCheck.get() && this.bedObstructed(player, at, direction)) {
                evt.setResult(PlayerEntity.SleepResult.OBSTRUCTED);
                return;
            }

            if (ConfigBuildHandler.SLEEP_CONFIG.monsterCheck.get() && !player.isCreative()) {

                List<MonsterEntity> list = world.getEntitiesWithinAABB(MonsterEntity.class, new AxisAlignedBB((double) at.getX() - 8.0D,
                        (double) at.getY() - 5.0D, (double) at.getZ() - 8.0D, (double) at.getX() + 8.0D,
                        (double) at.getY() + 5.0D, (double) at.getZ() + 8.0D), it -> {
                    boolean name = ConfigBuildHandler.SLEEP_CONFIG.namedMonsters.get() || !it.hasCustomName();
                    boolean persistent = ConfigBuildHandler.SLEEP_CONFIG.persistentMonsters.get() || !it.isNoDespawnRequired();
                    return it.isPreventingPlayerRest(player) && name && persistent && it.isAlive();
                });

                if (!list.isEmpty()) {

                    if (ConfigBuildHandler.SLEEP_CONFIG.glow.get()) {
                        list.forEach(it -> it.addPotionEffect(new EffectInstance(Effects.GLOWING, ConfigBuildHandler.SLEEP_CONFIG.glowDuration.get())));
                    }

                    evt.setResult(PlayerEntity.SleepResult.NOT_SAFE);
                    return;

                }

            }

            if (!ConfigBuildHandler.SLEEP_TIMINGS_CONFIG.instantSleeping.get() && SetSpawnHelper.isNewSpawnAllowed(world, player, at, SetSpawnPoint.CHAT)) {
                player.sendStatusMessage(SetSpawnHelper.createRespawnMessage(), false);
            }

        }

        player.startSleeping(at);
        ReflectionHelper.setSleepTimer(player, ConfigBuildHandler.SLEEP_TIMINGS_CONFIG.instantSleeping.get() ? 100 : 0);

        if (player.world instanceof ServerWorld) {
            ((ServerWorld) player.world).updateAllPlayersSleepingFlag();
        }

        // do what would otherwise have happened if a positive sleeping result would've been returned
        if (player instanceof ServerPlayerEntity) {
            player.addStat(Stats.SLEEP_IN_BED);
            CriteriaTriggers.SLEPT_IN_BED.trigger((ServerPlayerEntity) player);
        }

        // stop vanilla from executing
        evt.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);

    }

    private boolean bedInRange(PlayerEntity player, BlockPos pos, Direction direction) {

        Method bedInRange = ReflectionHelper.getBedInRange();

        if (bedInRange != null) {

            try {

                return (boolean) bedInRange.invoke(player, pos, direction);

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }

        return true;

    }

    private boolean bedObstructed(PlayerEntity player, BlockPos pos, Direction direction) {

        Method bedObstructed = ReflectionHelper.getBedObstructed();

        if (bedObstructed != null) {

            try {

                return (boolean) bedObstructed.invoke(player, pos, direction);

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }

        return false;

    }

    private void sendNotPossibleNowMessage(PlayerEntity player) {

        int min = ConfigBuildHandler.SLEEP_TIMINGS_CONFIG.bedtimeStart.get();
        int max = ConfigBuildHandler.SLEEP_TIMINGS_CONFIG.bedtimeEnd.get();
        String minTime = TimeFormatHelper.formatTime(min);
        String maxTime = TimeFormatHelper.formatTime(max);
        boolean thunder = ConfigBuildHandler.SLEEP_TIMINGS_CONFIG.bedtimeThunder.get();
        boolean rain = ConfigBuildHandler.SLEEP_TIMINGS_CONFIG.bedtimeRain.get();

        TranslationTextComponent time;
        Optional<TranslationTextComponent> weather = Optional.empty();
        TranslationTextComponent message;

        // use adapted vanilla message for default timings
        if (min == 12541 && max == 23458) {
            time = new TranslationTextComponent("block.minecraft.bed.no_sleep.night");
        } else {
            time = new TranslationTextComponent("block.minecraft.bed.no_sleep.time", minTime, maxTime);
        }

        if (thunder && rain) {
            weather = Optional.of(new TranslationTextComponent("block.minecraft.bed.no_sleep.bad_weather"));
        } else if (thunder) {
            weather = Optional.of(new TranslationTextComponent("block.minecraft.bed.no_sleep.thunder"));
        } else if (rain) {
            weather = Optional.of(new TranslationTextComponent("block.minecraft.bed.no_sleep.rain"));
        }

        message = weather.map(it -> new TranslationTextComponent("block.minecraft.bed.no_sleep.long_message", time, it))
                .orElseGet(() -> new TranslationTextComponent("block.minecraft.bed.no_sleep.short_message", time));

        player.sendStatusMessage(message, true);

    }

}
