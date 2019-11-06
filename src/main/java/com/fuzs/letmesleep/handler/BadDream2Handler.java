package com.fuzs.letmesleep.handler;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class BadDream2Handler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent evt) {

        if (!ConfigBuildHandler.GENERAL_CONFIG.spawnMonsters.get() || evt.phase != TickEvent.Phase.START || evt.world.isRemote) {
            return;
        }

        ServerWorld world = (ServerWorld) evt.world;

        if (world.getPlayers().stream().noneMatch(player -> !player.isSpectator() && !player.isPlayerFullyAsleep())) {

            if (world.getDifficulty() == Difficulty.PEACEFUL || !this.performSleepSpawning(world, world.getPlayers())) {

                if (world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
                    long dayTime = world.getDayTime() + 24000L;
                    long wakeUpTime = ConfigBuildHandler.WAKE_UP_CONFIG.wakeUpTime.get();
                    world.setDayTime(dayTime - (24000L - wakeUpTime + dayTime) % 24000L);
                }

                world.getPlayers().stream().filter(LivingEntity::isSleeping).forEach(player -> player.wakeUpPlayer(false, false, true));

                if (world.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
                    world.dimension.resetRainAndThunder();
                }

            }

        }

    }

    private boolean performSleepSpawning(World world, List<? extends PlayerEntity> playerList) {

        boolean flag = false;

        for (PlayerEntity player : playerList) {

            boolean flag1 = false;
            int i = 0;

            while (i < 20 && !flag1) {

                int j = (MathHelper.floor(player.posX) + world.rand.nextInt(8)) - world.rand.nextInt(8);
                int k = (MathHelper.floor(player.posZ) + world.rand.nextInt(8)) - world.rand.nextInt(8);
                int l = (MathHelper.floor(player.posY) + world.rand.nextInt(4)) - world.rand.nextInt(4);

                EntityType<?> entitytype = DungeonHooks.getRandomDungeonMob(player.getRNG());
                l = MathHelper.clamp(l, 1, 256);
                int l1 = l;

                BlockPos pos = new BlockPos(j, l1 - 1, k);
                while (l1 > 2 && !world.getBlockState(pos).isNormalCube(world, pos)) {
                    pos = pos.down();
                    l1--;
                }

                BlockPos pos1 = new BlockPos(j, l1, k);
                while (!world.isAirBlock(pos1) && l1 < l + 16 && l1 < 256) {
                    pos1 = pos1.up();
                    l1++;
                }

                if (l1 < l + 16 && l1 < 256 && world.areCollisionShapesEmpty(entitytype.func_220328_a(j, l1, k))) {

                    Entity entity = entitytype.create(world);

                    if (entity instanceof MobEntity) {

                        MobEntity monster = (MobEntity) entity;
                        Path path = monster.getNavigator().getPathToEntityLiving(player, 0);

                        if (path != null && path.getCurrentPathLength() > 1) {

                            PathPoint pathpoint = path.getFinalPathPoint();

                            if (pathpoint != null && world.isPlayerWithin(pathpoint.x, pathpoint.y, pathpoint.z, 1.5)) {

                                BlockPos blockpos = this.getNearestEmptyChunkCoordinates(world, player.getBedPosition().orElse(new BlockPos(j, l1 + 1, k)));

                                monster.moveToBlockPosAndAngles(blockpos, world.rand.nextFloat() * 360.0F, 0.0F);
                                monster.onInitialSpawn(world, world.getDifficultyForLocation(blockpos), SpawnReason.EVENT, null, null);
                                this.addEntityPassengers(world, monster);
                                monster.playAmbientSound();

                                player.wakeUpPlayer(true, false, false);
                                flag = flag1 = true;

                            }

                        }

                    }

                }

                i++;

            }

        }

        return flag;

    }

    private BlockPos getNearestEmptyChunkCoordinates(World world, BlockPos pos) {

        BlockState state = world.getBlockState(pos);

        if (state.isBed(world, pos, null)) {

            Direction facing1 = state.get(HorizontalBlock.HORIZONTAL_FACING);
            Direction facing2 = state.get(BedBlock.PART) == BedPart.FOOT ? facing1 : facing1.getOpposite();

            BlockPos part1 = pos.offset(facing2.getOpposite(), -2).offset(facing2.getOpposite().rotateY(), -2);
            BlockPos part2 = pos.offset(facing2, 3).offset(facing2.rotateY(), 2);

            Stream<BlockPos> stream = BlockPos.getAllInBox(part1, part2);
            Optional<BlockPos> first = stream.filter(it -> world.getBlockState(it).isNormalCube(world, it.down())
                    && world.isAirBlock(it) && world.isAirBlock(it)).findFirst();

            return first.orElse(pos);

        }

        return pos;

    }

    private void addEntityPassengers(World world, Entity entity) {

        if (world.addEntity(entity)) {

            for(Entity e : entity.getPassengers()) {
                this.addEntityPassengers(world, e);
            }

        }

    }

}
