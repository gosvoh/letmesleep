package com.fuzs.letmesleep.handler;

import com.fuzs.letmesleep.LetMeSleep;
import com.fuzs.letmesleep.helper.PotionHelper;
import com.fuzs.letmesleep.util.ClearPotions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Optional;

public class WakeUpHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerWake(PlayerWakeUpEvent evt) {

        if (!evt.getEntityPlayer().world.isRemote) {

            EntityPlayerMP player = (EntityPlayerMP) evt.getEntityPlayer();

            if (!ConfigBuildHandler.generalConfig.setSpawnOnWakeUp) {

                IBlockState iblockstate = player.bedLocation == null ? null : player.world.getBlockState(player.bedLocation);

                if (player.bedLocation != null && iblockstate.getBlock().isBed(iblockstate, player.world, player.bedLocation, player)) {

                    iblockstate.getBlock().setBedOccupied(player.world, player.bedLocation, player, false);
                    BlockPos blockpos = iblockstate.getBlock().getBedSpawnPosition(iblockstate, player.world, player.bedLocation, player);

                    if (blockpos == null) {
                        blockpos = player.bedLocation.up();
                    }

                    player.setPosition((float) blockpos.getX() + 0.5F, (float) blockpos.getY() + 0.1F, (float) blockpos.getZ() + 0.5F);

                }

                player.bedLocation = null;

            }

            if (evt.shouldSetSpawn() && !evt.wakeImmediately() && !evt.updateWorld()) {

                if (ConfigBuildHandler.wakeUpConfig.heal) {

                    int i = ConfigBuildHandler.wakeUpConfig.healAmount;
                    player.heal(i == 0 ? player.getMaxHealth() : i);

                }

                if (ConfigBuildHandler.wakeUpConfig.starve) {

                    int j = ConfigBuildHandler.wakeUpConfig.starveAmount;
                    int k = player.getFoodStats().getFoodLevel();
                    player.getFoodStats().setFoodLevel(MathHelper.clamp(k - (j == 0 ? k : j), 0, 20));

                }

                ClearPotions clearPotions = ConfigBuildHandler.wakeUpConfig.clearPotions;
                if (clearPotions == ClearPotions.BOTH) {

                    player.clearActivePotions();

                } else if (clearPotions == ClearPotions.POSITIVE) {

                    PotionHelper.clearActivePotions(player, true);

                } else if (clearPotions == ClearPotions.NEGATIVE) {

                    PotionHelper.clearActivePotions(player, false);

                }

                if (ConfigBuildHandler.wakeUpConfig.effects) {

                    this.applyPotions(player);

                }

            }

        }

    }

    private void applyPotions(EntityPlayerMP player) {

        String[] effects = ConfigBuildHandler.wakeUpConfig.potionEffects;

        for (String s : effects) {

            String error = "Potion effect to be applied on waking up has been specified incorrectly!";
            String[] values = s.split(",");
            Optional<Potion> potionOptional = Optional.empty();
            int duration = 0;
            int amplifier = 0;
            boolean showParticles = false;

            if (values.length > 0) {

                String[] name = values[0].split(":");

                if (name.length > 1) {
                    ResourceLocation location = new ResourceLocation(name[0], name[1]);
                    potionOptional = Optional.ofNullable(ForgeRegistries.POTIONS.getValue(location));
                } else {
                    LetMeSleep.LOGGER.error(error);
                }

            } else {
                LetMeSleep.LOGGER.error(error);
            }

            try {

                if (values.length > 1) {
                    duration = Integer.parseInt(values[1]) * 20;
                } else {
                    LetMeSleep.LOGGER.error(error);
                }

                if (values.length > 2) {
                    amplifier = Integer.parseInt(values[2]);
                }

            } catch (NumberFormatException e) {
                LetMeSleep.LOGGER.error(error, e);
            }

            if (values.length > 3) {
                showParticles = !Boolean.parseBoolean(values[3]);
            }

            if (potionOptional.isPresent()) {

                PotionEffect potion = new PotionEffect(potionOptional.get(), duration, amplifier, false, showParticles);
                player.addPotionEffect(potion);

            }

        }

    }

}
