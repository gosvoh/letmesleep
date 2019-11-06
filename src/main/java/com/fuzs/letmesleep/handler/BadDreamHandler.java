package com.fuzs.letmesleep.handler;

import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BadDreamHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent evt) {

        if (ConfigBuildHandler.GENERAL_CONFIG.spawnMonsters.get() && !evt.player.world.isRemote) {

            ServerPlayerEntity player = (ServerPlayerEntity) evt.player;
            boolean flag = player.world.getDifficulty() != Difficulty.PEACEFUL;

            if (flag && !player.isCreative() && player.getBedPosition().isPresent() && !player.isPlayerFullyAsleep()) {

                int i = player.world.getDifficulty().getId();

                if (player.getRNG().nextInt(60 / i + 100 - player.getSleepTimer()) == 0) {

                    if (this.summonMob(player, player.world, player.getBedPosition().get())) {
                        player.wakeUpPlayer(true, false, false);
                    }

                }

            }

        }

    }

    private boolean summonMob(PlayerEntity player, World world, BlockPos bedPos) {

        Direction direction = world.getBlockState(bedPos).get(HorizontalBlock.HORIZONTAL_FACING).getOpposite();
        double d1 = world.getRandom().nextDouble() - world.getRandom().nextDouble();
        double d2 = world.getRandom().nextDouble() - world.getRandom().nextDouble();

        double xCoord = (double) bedPos.getX() + d1 * (2.0 + (Math.signum(d1) == Math.signum(direction.getXOffset()) ? Math.abs(direction.getXOffset()) : 0)) + 0.5;
        double yCoord = bedPos.getY() + world.getRandom().nextInt(3) - 1;
        double zCoord = (double) bedPos.getZ() + d2 * (2.0 + (Math.signum(d2) == Math.signum(direction.getZOffset()) ? Math.abs(direction.getZOffset()) : 0)) + 0.5;

        BlockPos spawnPos = new BlockPos(xCoord, yCoord, zCoord);
        EntityType<?> entitytype = DungeonHooks.getRandomDungeonMob(world.getRandom());
        boolean collisionCheck = world.areCollisionShapesEmpty(entitytype.func_220328_a(xCoord, yCoord, zCoord));
        boolean requirementsCheck = EntitySpawnPlacementRegistry.func_223515_a(entitytype, world.getWorld(), SpawnReason.EVENT, spawnPos, world.getRandom());

        if (collisionCheck && requirementsCheck && world.getBlockState(spawnPos.down()).isNormalCube(world, spawnPos.down())) {

            Entity entity = entitytype.create(world);

            if (entity instanceof MobEntity) {

                MobEntity monster = (MobEntity) entity;
                monster.onGround = true;
                monster.setLocationAndAngles(xCoord, yCoord, zCoord, world.getRandom().nextFloat() * 360.0F, 0.0F);
                Path path = monster.getNavigator().getPathToEntityLiving(player, 0);

                if (path != null && path.getCurrentPathLength() > 1) {

                    PathPoint pathpoint = path.getFinalPathPoint();

                    if (pathpoint != null && world.isPlayerWithin(pathpoint.x, pathpoint.y, pathpoint.z, 1.5)) {

                        monster.onInitialSpawn(world, world.getDifficultyForLocation(new BlockPos(entity)), SpawnReason.EVENT, null, null);
                        monster.setAttackTarget(player);
                        this.addEntityPassengers(world, entity);

                        return true;

                    }

                }

            }

        }

        return false;

    }

    private void addEntityPassengers(World world, Entity entity) {

        if (world.addEntity(entity)) {

            for(Entity e : entity.getPassengers()) {
                this.addEntityPassengers(world, e);
            }

        }

    }

}
