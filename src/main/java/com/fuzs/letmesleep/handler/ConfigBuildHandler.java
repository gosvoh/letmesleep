package com.fuzs.letmesleep.handler;

import com.fuzs.letmesleep.util.ClearPotions;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class ConfigBuildHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final GeneralConfig GENERAL_CONFIG = new GeneralConfig("general");
    public static final SleepConfig SLEEP_CONFIG = new SleepConfig("sleep");
    public static final WakeUpConfig WAKE_UP_CONFIG = new WakeUpConfig("wake_up");

    public static class GeneralConfig {

        public final ForgeConfigSpec.BooleanValue setSpawnAlways;
        public final ForgeConfigSpec.BooleanValue setSpawnOnWakeUp;
        public final ForgeConfigSpec.BooleanValue spawnMonster;
        public final ForgeConfigSpec.IntValue spawnMonsterChance;
        public final ForgeConfigSpec.BooleanValue doInsomnia;

        private GeneralConfig(String name) {

            BUILDER.push(name);

            this.setSpawnAlways = ConfigBuildHandler.BUILDER.comment("Set player spawn point when attempting to sleep in a bed, even when not successful, e. g. before bedtime.").define("Always Set Spawn", true);
            this.setSpawnOnWakeUp = ConfigBuildHandler.BUILDER.comment("Should the player spawn point be set after sleeping in a bed. Disabling this and \"Always Set Spawn\" will prevent beds from setting the player spawn point.").define("Set Spawn On Wake Up", true);
            this.spawnMonster = ConfigBuildHandler.BUILDER.comment("Spawn monster and wake player when sleeping in an insufficiently lit area.").define("Spawn Monster", true);
            this.spawnMonsterChance = ConfigBuildHandler.BUILDER.comment("Chance to spawn a monster, higher numbers make it more likely to happen.").defineInRange("Spawn Monster Chance", 10, 0, Integer.MAX_VALUE);
            this.doInsomnia = ConfigBuildHandler.BUILDER.comment("Add a game rule \"doInsomnia\" to disable phantom spawning during the night.").define("doInsomnia Game Rule", true);

            BUILDER.pop();

        }

    }

    public static class SleepConfig {

        public final ForgeConfigSpec.IntValue bedtimeStart;
        public final ForgeConfigSpec.IntValue bedtimeEnd;
        public final ForgeConfigSpec.BooleanValue rangeCheck;
        public final ForgeConfigSpec.BooleanValue obstructionCheck;
        public final ForgeConfigSpec.BooleanValue monsterCheck;
        public final ForgeConfigSpec.BooleanValue glow;
        public final ForgeConfigSpec.IntValue glowDuration;
        public final ForgeConfigSpec.BooleanValue namedMonsters;
        public final ForgeConfigSpec.BooleanValue persistentMonsters;

        private SleepConfig(String name) {

            BUILDER.push(name);

            this.bedtimeStart = ConfigBuildHandler.BUILDER.comment("Time from when onwards sleeping in a bed is possible.").defineInRange("Bedtime Start", 12541, 0, 24000);
            this.bedtimeEnd = ConfigBuildHandler.BUILDER.comment("Time until when sleeping is permitted.").defineInRange("Bedtime End", 23458, 0, 24000);
            this.rangeCheck = ConfigBuildHandler.BUILDER.comment("Check if the player is close enough to the bed.").define("Range Check", false);
            this.obstructionCheck = ConfigBuildHandler.BUILDER.comment("Check if the bed has enough open space above it.").define("Obstruction Check", true);
            this.monsterCheck = ConfigBuildHandler.BUILDER.comment("Check if monsters are nearby.").define("Monster Check", true);
            this.glow = ConfigBuildHandler.BUILDER.comment("Should monsters preventing the player from sleeping glow.").define("Glowing Monsters", true);
            this.glowDuration = ConfigBuildHandler.BUILDER.comment("Duration in ticks for which the monsters nearby will glow.").defineInRange("Glow Duration", 60, 0, Integer.MAX_VALUE);
            this.namedMonsters = ConfigBuildHandler.BUILDER.comment("Should named monsters prevent the player from sleeping.").define("Named Monsters", false);
            this.persistentMonsters = ConfigBuildHandler.BUILDER.comment("Should persistent monsters (unable to despawn via a vanilla tag) prevent the player from sleeping.").define("Persistent Monsters", false);

            BUILDER.pop();

        }

    }

    public static class WakeUpConfig {

        public final ForgeConfigSpec.IntValue wakeUpTime;
        public final ForgeConfigSpec.BooleanValue heal;
        public final ForgeConfigSpec.IntValue healAmount;
        public final ForgeConfigSpec.BooleanValue starve;
        public final ForgeConfigSpec.IntValue starveAmount;
        public final ForgeConfigSpec.EnumValue<ClearPotions> clearPotions;
        public final ForgeConfigSpec.BooleanValue effects;
        public final ForgeConfigSpec.ConfigValue<List<String>> potionEffects;

        private WakeUpConfig(String name) {

            BUILDER.push(name);

            this.wakeUpTime = ConfigBuildHandler.BUILDER.comment("Time being set after sleeping successfully.").defineInRange("Wake Up Time", 0, 0, 24000);
            this.heal = ConfigBuildHandler.BUILDER.comment("Should the player be healed when waking up.").define("Heal Player", true);
            this.healAmount = ConfigBuildHandler.BUILDER.comment("Amount of health the player should be healed by. Set to 0 to fully heal.").defineInRange("Heal Amount", 0, 0, Integer.MAX_VALUE);
            this.starve = ConfigBuildHandler.BUILDER.comment("Should the player loose some food after waking up.").define("Loose Food", false);
            this.starveAmount = ConfigBuildHandler.BUILDER.comment("Amount of food to loose when waking up. Set to 0 to completely starve the player. Negative values will feed the player instead.").defineInRange("Food Amount", 3, Integer.MIN_VALUE, Integer.MAX_VALUE);
            this.clearPotions = ConfigBuildHandler.BUILDER.comment("Clear potion effects after the player wakes up.").defineEnum("Clear Potions", ClearPotions.BOTH);
            this.effects = ConfigBuildHandler.BUILDER.comment("Should custom potion effects be applied to the player after waking up.").define("Apply Effects", false);
            this.potionEffects = ConfigBuildHandler.BUILDER.comment("Potion effects to be given to the player after waking up. Enter as \"modid:effect,duration,amplifier,hideParticles\", values are like the /effect command. Amplifier and hideParticles are optional.").define("Effects To Apply", Collections.singletonList("minecraft:regeneration,30,0,false"));

            BUILDER.pop();

        }

    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

}
