package com.fuzs.letmesleep.handler;

import com.fuzs.letmesleep.helper.ReflectionHelper;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class BadDreamHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent evt) {

        if (evt.phase != TickEvent.Phase.START || evt.world.isRemote) {
            return;
        }

        ServerWorld world = (ServerWorld) evt.world;

        if (ReflectionHelper.getAllPlayersSleeping(world) && world.getPlayers().stream().noneMatch(player -> !player.isSpectator() && !player.isPlayerFullyAsleep())) {

            if (!ConfigBuildHandler.GENERAL_CONFIG.spawnMonster.get() || !this.performSleepSpawning(world, world.getPlayers(), world.getDifficulty().getId())) {

                if (world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
                    long dayTime = world.getDayTime();
                    long wakeUpTime = ConfigBuildHandler.SLEEP_TIMINGS_CONFIG.wakeUpTime.get();
                    long sleepLimit = ConfigBuildHandler.SLEEP_TIMINGS_CONFIG.sleepLimit.get();
                    long sleepDuration = Math.min((wakeUpTime + 24000L - dayTime) % 24000L, sleepLimit);
                    world.setDayTime((dayTime + sleepDuration) % 24000L);
                }

                ReflectionHelper.setAllPlayersSleeping(world, false);
                world.getPlayers().stream().filter(LivingEntity::isSleeping).forEach(player ->
                        player.wakeUpPlayer(false, false, true));

                if (world.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
                    world.dimension.resetRainAndThunder();
                }

            }

        }

    }

    private boolean performSleepSpawning(World world, List<? extends PlayerEntity> playerlist, int difficulty) {

        boolean flag = false;

        for (PlayerEntity player : playerlist) {

            if (!EntityPredicates.CAN_AI_TARGET.test(player) || !player.getBedPosition().isPresent()) {
                continue;
            }

            int i = 0;
            boolean flag1 = false;
            BlockPos bedPos = player.getBedPosition().get();

            while (i < difficulty * ConfigBuildHandler.GENERAL_CONFIG.spawnMonsterChance.get() && !flag1) {

                Direction direction = world.getBlockState(bedPos).get(HorizontalBlock.HORIZONTAL_FACING).getOpposite();
                double d1 = world.getRandom().nextDouble() - world.getRandom().nextDouble();
                double d2 = world.getRandom().nextDouble() - world.getRandom().nextDouble();

                double xCoord = (double) bedPos.getX() + d1 * (2.0 + (Math.signum(d1) == Math.signum(direction.getXOffset()) ? Math.abs(direction.getXOffset()) : 0)) + 0.5;
                double yCoord = bedPos.getY() + world.getRandom().nextInt(3) - 1;
                double zCoord = (double) bedPos.getZ() + d2 * (2.0 + (Math.signum(d2) == Math.signum(direction.getZOffset()) ? Math.abs(direction.getZOffset()) : 0)) + 0.5;

                EntityType<?> entitytype = DungeonHooks.getRandomDungeonMob(world.getRandom());
                boolean collisionCheck = world.areCollisionShapesEmpty(entitytype.func_220328_a(xCoord, yCoord, zCoord));
                boolean requirementsCheck = EntitySpawnPlacementRegistry.func_223515_a(entitytype, world, SpawnReason.EVENT, new BlockPos(xCoord, yCoord, zCoord), world.getRandom());

                if (collisionCheck && requirementsCheck) {

                    Entity entity = entitytype.create(world);

                    if (entity instanceof MobEntity) {

                        MobEntity mob = (MobEntity) entity;
                        mob.setLocationAndAngles(xCoord, yCoord, zCoord, world.getRandom().nextFloat() * 360.0F, 0.0F);

                        if (!world.containsAnyLiquid(mob.getBoundingBox())) {

                            mob.onGround = true; // required for navigator to be able to find a path
                            Path path = mob.getNavigator().getPathToEntityLiving(player, 0);

                            if (path != null && path.getCurrentPathLength() > 1) {

                                PathPoint pathpoint = path.getFinalPathPoint();

                                if (pathpoint != null && world.isPlayerWithin(pathpoint.x, pathpoint.y, pathpoint.z, 1.5)) {

                                    mob.onInitialSpawn(world, world.getDifficultyForLocation(new BlockPos(entity)), SpawnReason.EVENT, null, null);
                                    mob.setAttackTarget(player);
                                    mob.playAmbientSound();
                                    this.addEntityPassengers(world, entity);

                                    player.wakeUpPlayer(true, false, false);
                                    flag = flag1 = true;

                                }

                            }

                        }

                    }

                }

                i++;

            }

        }

        return flag;

    }

    private void addEntityPassengers(World world, Entity entity) {

        if (world.addEntity(entity)) {

            for(Entity e : entity.getPassengers()) {
                this.addEntityPassengers(world, e);
            }

        }

    }

}
