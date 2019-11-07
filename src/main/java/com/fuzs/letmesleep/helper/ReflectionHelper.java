package com.fuzs.letmesleep.helper;

import com.fuzs.letmesleep.LetMeSleep;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Method;

public class ReflectionHelper {

    private static final String PLAYERENTITY_SLEEP_TIMER = "field_71076_b";
    private static final String PLAYERENTITY_BED_IN_RANGE = "func_190774_a";
    private static final String PLAYERENTITY_FUNC_213828_B = "func_213828_b";
    private static final String LIVINGENTITY_ON_FINISHED_POTION_EFFECT = "func_70688_c";
    private static final String BOOLEANVALUE_CREATE = "func_223568_b";
    private static final String SERVERWORLD_ALL_PLAYERS_SLEEPING = "field_73068_P";

    public static void setSleepTimer(PlayerEntity instance, int i) {

        try {

            ObfuscationReflectionHelper.setPrivateValue(PlayerEntity.class, instance, i, PLAYERENTITY_SLEEP_TIMER);

        } catch (Exception e) {

            LetMeSleep.LOGGER.error("setSleepTimer() failed", e);

        }

    }

    public static Method getBedInRange() {

        try {

            return ObfuscationReflectionHelper.findMethod(PlayerEntity.class, PLAYERENTITY_BED_IN_RANGE, BlockPos.class, Direction.class);

        } catch (Exception e) {

            LetMeSleep.LOGGER.error("getBedInRange() failed", e);

        }

        return null;

    }

    public static Method getBedObstructed() {

        try {

            return ObfuscationReflectionHelper.findMethod(PlayerEntity.class, PLAYERENTITY_FUNC_213828_B, BlockPos.class, Direction.class);

        } catch (Exception e) {

            LetMeSleep.LOGGER.error("getBedObstructed() failed", e);

        }

        return null;

    }

    @SuppressWarnings("WeakerAccess")
    public static Method getOnFinishedPotionEffect() {

        try {

            return ObfuscationReflectionHelper.findMethod(LivingEntity.class, LIVINGENTITY_ON_FINISHED_POTION_EFFECT, EffectInstance.class);

        } catch (Exception e) {

            LetMeSleep.LOGGER.error("getOnFinishedPotionEffect() failed", e);

        }

        return null;

    }

    public static Method getCreate() {

        try {

            return ObfuscationReflectionHelper.findMethod(GameRules.BooleanValue.class, BOOLEANVALUE_CREATE, boolean.class);

        } catch (Exception e) {

            LetMeSleep.LOGGER.error("getCreate() failed", e);

        }

        return null;

    }

    public static Boolean getAllPlayersSleeping(ServerWorld instance) {

        try {

            return ObfuscationReflectionHelper.getPrivateValue(ServerWorld.class, instance, SERVERWORLD_ALL_PLAYERS_SLEEPING);

        } catch (Exception e) {

            LetMeSleep.LOGGER.error("getAllPlayersSleeping() failed", e);

        }

        return false;

    }

}
