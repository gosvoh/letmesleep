package com.fuzs.letmesleep.handler;

import com.fuzs.letmesleep.helper.ReflectionHelper;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class SleepAttemptHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerSleep(PlayerSleepInBedEvent evt) {

        EntityPlayer player = evt.getEntityPlayer();
        BlockPos bedLocation = evt.getPos();
        IBlockState state = player.world.isBlockLoaded(bedLocation) ? player.world.getBlockState(bedLocation) : null;
        boolean isBed = state != null && state.getBlock().isBed(state, player.world, bedLocation, player);
        EnumFacing enumfacing = isBed && state.getBlock() instanceof BlockHorizontal ? state.getValue(BlockHorizontal.FACING) : null;

        if (!player.world.isRemote) {

            if (ConfigBuildHandler.generalConfig.setSpawnAlways) {
                player.setSpawnPoint(bedLocation, false);
            }

            if (player.isPlayerSleeping() || !player.isEntityAlive()) {
                evt.setResult(EntityPlayer.SleepResult.OTHER_PROBLEM);
                return;
            }

            if (!player.world.provider.isSurfaceWorld()) {
                evt.setResult(EntityPlayer.SleepResult.NOT_POSSIBLE_HERE);
                return;
            }

            if (!net.minecraftforge.event.ForgeEventFactory.fireSleepingTimeCheck(player, bedLocation)) {
                evt.setResult(EntityPlayer.SleepResult.NOT_POSSIBLE_NOW);
                return;
            }

            if (ConfigBuildHandler.sleepConfig.rangeCheck && !this.bedInRange(player, bedLocation, enumfacing)) {
                evt.setResult(EntityPlayer.SleepResult.TOO_FAR_AWAY);
                return;
            }

            if (ConfigBuildHandler.sleepConfig.monsterCheck && !player.isCreative()) {

                List<EntityMob> list = player.world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB((double) bedLocation.getX() - 8.0D,
                        (double) bedLocation.getY() - 5.0D, (double) bedLocation.getZ() - 8.0D, (double) bedLocation.getX() + 8.0D,
                        (double) bedLocation.getY() + 5.0D, (double) bedLocation.getZ() + 8.0D), it -> {
                    if (it != null) {
                        boolean name = ConfigBuildHandler.sleepConfig.namedMonsters || !it.hasCustomName();
                        boolean persistent = ConfigBuildHandler.sleepConfig.persistentMonsters || !it.isNoDespawnRequired();
                        return it.isPreventingPlayerRest(player) && name && persistent && it.isEntityAlive();
                    }
                    return false;
                });

                if (!list.isEmpty()) {

                    if (ConfigBuildHandler.sleepConfig.glow) {
                        list.forEach(it -> it.addPotionEffect(new PotionEffect(MobEffects.GLOWING, ConfigBuildHandler.sleepConfig.glowDuration)));
                    }

                    evt.setResult(EntityPlayer.SleepResult.NOT_SAFE);
                    return;

                }

            }

        }

        if (player.isRiding()) {

            player.dismountRidingEntity();

        }

        this.spawnShoulderEntities(player);
        this.setSize(player, 0.2F, 0.2F);

        if (enumfacing != null) {

            float f1 = 0.5F + (float)enumfacing.getFrontOffsetX() * 0.4F;
            float f = 0.5F + (float)enumfacing.getFrontOffsetZ() * 0.4F;
            this.setRenderOffsetForSleep(player, enumfacing);
            player.setPosition(((float) bedLocation.getX() + f1), ((float) bedLocation.getY() + 0.6875F), ((float) bedLocation.getZ() + f));

        } else {

            player.setPosition(((float) bedLocation.getX() + 0.5F), ((float) bedLocation.getY() + 0.6875F), ((float) bedLocation.getZ() + 0.5F));

        }

        ReflectionHelper.setSleeping(player, true);
        ReflectionHelper.setSleepTimer(player, 0);
        player.bedLocation = bedLocation;
        player.motionX = 0.0D;
        player.motionY = 0.0D;
        player.motionZ = 0.0D;

        if (!player.world.isRemote) {

            player.world.updateAllPlayersSleepingFlag();

        }

        // stop vanilla from executing
        evt.setResult(EntityPlayer.SleepResult.OK);

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onSleepingTimeCheck(SleepingTimeCheckEvent evt) {

        EntityPlayer player = evt.getEntityPlayer();

        if (!player.world.isRemote) {

            long time = player.world.getWorldTime() % 24000L;
            long start = ConfigBuildHandler.sleepConfig.bedtimeStart;
            long end = ConfigBuildHandler.sleepConfig.bedtimeEnd;
            boolean flag = end < start ? start <= time || time <= end : start <= time && time <= end;

            evt.setResult(flag ? Event.Result.ALLOW : Event.Result.DENY);

        }

    }

    private boolean bedInRange(EntityPlayer player, BlockPos pos, EnumFacing facing) {

        Method bedInRange = ReflectionHelper.getBedInRange();

        if (bedInRange != null) {

            try {

                return (boolean) bedInRange.invoke(player, pos, facing);

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }

        return true;

    }

    private void spawnShoulderEntities(EntityPlayer player) {

        Method spawnShoulderEntities = ReflectionHelper.getSpawnShoulderEntities();

        if (spawnShoulderEntities != null) {

            try {

                spawnShoulderEntities.invoke(player);

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }

    }

    @SuppressWarnings("SameParameterValue")
    private void setSize(EntityPlayer player, float width, float height) {

        Method setSize = ReflectionHelper.getSetSize();

        if (setSize != null) {

            try {

                setSize.invoke(player, width, height);

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }

    }

    private void setRenderOffsetForSleep(EntityPlayer player, EnumFacing facing) {

        Method setRenderOffsetForSleep = ReflectionHelper.getSetRenderOffsetForSleep();

        if (setRenderOffsetForSleep != null) {

            try {

                setRenderOffsetForSleep.invoke(player, facing);

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }

    }

}
