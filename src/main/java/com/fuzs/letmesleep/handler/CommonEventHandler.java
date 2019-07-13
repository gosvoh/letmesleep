package com.fuzs.letmesleep.handler;

import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class CommonEventHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void playerSleep(PlayerSleepInBedEvent evt) {

        PlayerEntity player = evt.getEntityPlayer();
        World world = player.world;
        BlockPos pos = evt.getPos();
        Direction direction = world.getBlockState(pos).get(HorizontalBlock.HORIZONTAL_FACING);

        if (!world.isRemote) {

            if (player.isSleeping() || !player.isAlive()) {
                return;
            }

            if (!world.dimension.isSurfaceWorld()) {
                return;
            }

            if (!net.minecraftforge.event.ForgeEventFactory.fireSleepingTimeCheck(player, player.getBedPosition())) {
                return;
            }

            if (!this.bedInRange(pos, direction, player)) {
                return;
            }

            if (this.func_213828_b(pos, direction, player)) {
                return;
            }

            if (!player.isCreative()) {

                List<MonsterEntity> list = world.getEntitiesWithinAABB(MonsterEntity.class, new AxisAlignedBB((double) pos.getX() - 8.0D, (double) pos.getY() - 5.0D, (double) pos.getZ() - 8.0D, (double) pos.getX() + 8.0D, (double) pos.getY() + 5.0D, (double) pos.getZ() + 8.0D), (it) -> it.isPreventingPlayerRest(player));

                if (!list.isEmpty()) {
                    list.forEach(it -> it.addPotionEffect(new EffectInstance(Effects.GLOWING, 60)));
                }

            }

        }

    }

    private boolean bedInRange(BlockPos pos, Direction direction, PlayerEntity player) {

        if (Math.abs(player.posX - (double)pos.getX()) <= 3.0D && Math.abs(player.posY - (double)pos.getY()) <= 2.0D && Math.abs(player.posZ - (double)pos.getZ()) <= 3.0D) {
            return true;
        } else if (direction == null) {
            return false;
        } else {
            BlockPos blockpos = pos.offset(direction.getOpposite());
            return Math.abs(player.posX - (double)blockpos.getX()) <= 3.0D && Math.abs(player.posY - (double)blockpos.getY()) <= 2.0D && Math.abs(player.posZ - (double)blockpos.getZ()) <= 3.0D;
        }

    }

    private boolean func_213828_b(BlockPos pos, Direction direction, PlayerEntity player) {
        BlockPos blockpos = pos.up();
        return this.isNoCube(blockpos, player.world) || this.isNoCube(blockpos.offset(direction.getOpposite()), player.world);
    }

    private boolean isNoCube(BlockPos pos, World world) {
        return world.getBlockState(pos).causesSuffocation(world, pos);
    }

}
