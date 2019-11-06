package com.fuzs.letmesleep.handler;

import com.fuzs.letmesleep.helper.ReflectionHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InsomniaHandler {

    private static GameRules.RuleKey<GameRules.BooleanValue> doInsomnia;

    @SuppressWarnings("unchecked")
    public static void registerGamerule() {

        if (!ConfigBuildHandler.GENERAL_CONFIG.doInsomnia.get()) {
            return;
        }

        try {

            Method create = ReflectionHelper.getCreate();
            if (create != null) {
                doInsomnia = GameRules.register("doInsomnia", (GameRules.RuleType<GameRules.BooleanValue>) create.invoke(null, true));
            }

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    /**
     * Prevents phantom spawn ticks from updating when doInsomnia is false
     * Accessed by the asm transformer bundled with this mod
     */
    @SuppressWarnings("unused")
    public static int checkInsomnia(ServerWorld worldIn, boolean flag, int i) {

        boolean stop = ConfigBuildHandler.GENERAL_CONFIG.doInsomnia.get() && !worldIn.getGameRules().getBoolean(doInsomnia) && flag;
        return stop ? i + 1 : i;

    }

}
