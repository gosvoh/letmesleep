package com.fuzs.letmesleep.handler;

import com.fuzs.letmesleep.helper.ReflectionHelper;
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
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class SleepAttemptHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerSleep(PlayerSleepInBedEvent evt) {

        PlayerEntity player = evt.getPlayer();
        World world = player.world;
        BlockPos at = evt.getPos();
        Direction direction = world.getBlockState(at).get(HorizontalBlock.HORIZONTAL_FACING);

        if (!world.isRemote) {

            if (ConfigBuildHandler.GENERAL_CONFIG.setSpawnAlways.get()) {
                player.setSpawnPoint(at, false, player.dimension);
            }

            if (player.isSleeping() || !player.isAlive()) {
                evt.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);
                return;
            }

            if (!world.dimension.isSurfaceWorld()) {
                evt.setResult(PlayerEntity.SleepResult.NOT_POSSIBLE_HERE);
                return;
            }

            if (!net.minecraftforge.event.ForgeEventFactory.fireSleepingTimeCheck(player, player.getBedPosition())) {
                evt.setResult(PlayerEntity.SleepResult.NOT_POSSIBLE_NOW);
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

        }

        player.startSleeping(at);
        ReflectionHelper.setSleepTimer(player, 0);

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

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onSleepingTimeCheck(SleepingTimeCheckEvent evt) {

        PlayerEntity player = evt.getPlayer();

        if (!player.world.isRemote) {

            long time = player.world.getDayTime() % 24000L;
            long start = ConfigBuildHandler.SLEEP_CONFIG.bedtimeStart.get();
            long end = ConfigBuildHandler.SLEEP_CONFIG.bedtimeEnd.get();
            boolean flag = end < start ? start <= time || time <= end : start <= time && time <= end;

            evt.setResult(flag ? Event.Result.ALLOW : Event.Result.DENY);

        }

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

}
