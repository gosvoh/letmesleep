package com.fuzs.letmesleep.helper;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

public class PotionHelper {

    public static void clearActivePotions(LivingEntity entity, boolean beneficial) {

        if (!entity.world.isRemote) {

            Iterator<EffectInstance> iterator = entity.getActivePotionEffects().iterator();

            while (iterator.hasNext()) {

                EffectInstance effect = iterator.next();

                boolean flag1 = net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.living.PotionEvent.PotionRemoveEvent(entity, effect));
                boolean flag2 = effect.getPotion().isBeneficial();

                if (flag1 || flag2 && !beneficial || !flag2 && beneficial) {
                    continue;
                }

                onFinishedPotionEffect(entity, effect);
                iterator.remove();

            }

        }

    }

    private static void onFinishedPotionEffect(LivingEntity entity, EffectInstance effect) {

        Method onFinishedPotionEffect = ReflectionHelper.getOnFinishedPotionEffect();
        if (onFinishedPotionEffect != null) {

            try {

                onFinishedPotionEffect.invoke(entity, effect);

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }

    }

}
