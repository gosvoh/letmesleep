package com.fuzs.letmesleep;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;

public class ModEventHandler extends BlockPumpkin {

    @SubscribeEvent
    public void playerSleep(PlayerSleepInBedEvent evt) {

        EntityPlayer player = evt.getEntityPlayer();
        World world = player.world;
        BlockPos bedLocation = evt.getPos();
        final IBlockState state = world.isBlockLoaded(bedLocation) ? world.getBlockState(bedLocation) : null;
        final boolean isBed = state != null && state.getBlock().isBed(state, world, bedLocation, player);
        final EnumFacing enumfacing = isBed && state.getBlock() instanceof BlockHorizontal ? (EnumFacing)state.getValue(BlockHorizontal.FACING) : null;

        if (!world.isRemote)
        {
            if (player.isPlayerSleeping() || !player.isEntityAlive())
            {
                return;
            }

            if (!world.provider.isSurfaceWorld())
            {
                return;
            }

            if (!net.minecraftforge.event.ForgeEventFactory.fireSleepingTimeCheck(player, bedLocation))
            {
                return;
            }

            if (!this.bedInRange(bedLocation, enumfacing, player))
            {
                return;
            }

            List<EntityMob> list = world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB((double)bedLocation.getX() - 8.0D, (double)bedLocation.getY() - 5.0D, (double)bedLocation.getZ() - 8.0D, (double)bedLocation.getX() + 8.0D, (double)bedLocation.getY() + 5.0D, (double)bedLocation.getZ() + 8.0D), new SleepEnemyPredicate(player));

            if (!list.isEmpty())
            {
                list.forEach(it -> it.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 60, 0, false, true)));
            }
        }

    }

    static class SleepEnemyPredicate implements Predicate<EntityMob>
    {
        private final EntityPlayer player;

        private SleepEnemyPredicate(EntityPlayer playerIn)
        {
            this.player = playerIn;
        }

        public boolean apply(@Nullable EntityMob p_apply_1_)
        {
            return p_apply_1_.isPreventingPlayerRest(this.player);
        }
    }



    private boolean bedInRange(BlockPos pos, EnumFacing facing, EntityPlayer player)
    {
        if (Math.abs(player.posX - (double)pos.getX()) <= 3.0D && Math.abs(player.posY - (double)pos.getY()) <= 2.0D && Math.abs(player.posZ - (double)pos.getZ()) <= 3.0D)
        {
            return true;
        }
        else if (facing == null) return false;
        else
        {
            BlockPos blockpos = pos.offset(facing.getOpposite());
            return Math.abs(player.posX - (double)blockpos.getX()) <= 3.0D && Math.abs(player.posY - (double)blockpos.getY()) <= 2.0D && Math.abs(player.posZ - (double)blockpos.getZ()) <= 3.0D;
        }
    }

}
