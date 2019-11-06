package com.fuzs.letmesleep.helper;

import com.fuzs.letmesleep.LetMeSleep;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Method;

public class ReflectionHelper {

    private static final String PLAYERENTITY_SLEEPTIMER = "field_71076_b";
    private static final String PLAYERENTITY_BEDINRANGE = "func_190774_a";
    private static final String PLAYERENTITY_FUNC_213828_B = "func_213828_b";
    private static final String LIVINGENTITY_ONFINISHEDPOTIONEFFECT = "func_70688_c";
    private static final String BOOLEANVALUE_CREATE = "func_223568_b";

    public static void resetSleepTimer(PlayerEntity instance) {

        try {

            ObfuscationReflectionHelper.setPrivateValue(PlayerEntity.class, instance, 0, PLAYERENTITY_SLEEPTIMER);

        } catch (Exception e) {

            LetMeSleep.LOGGER.error("resetSleepTimer() failed", e);

        }

    }

    public static Method getBedInRange() {

        try {

            return ObfuscationReflectionHelper.findMethod(PlayerEntity.class, PLAYERENTITY_BEDINRANGE, BlockPos.class, Direction.class);

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

            return ObfuscationReflectionHelper.findMethod(LivingEntity.class, LIVINGENTITY_ONFINISHEDPOTIONEFFECT, EffectInstance.class);

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

}
