package com.fuzs.letmesleep.handler;

import com.fuzs.letmesleep.LetMeSleep;
import com.fuzs.letmesleep.util.ClearPotions;
import net.minecraftforge.common.config.Config;

@SuppressWarnings("WeakerAccess")
@Config(modid = LetMeSleep.MODID)
public class ConfigBuildHandler {

    @Config.Name("general")
    public static GeneralConfig generalConfig = new GeneralConfig();
    @Config.Name("sleep")
    public static SleepConfig sleepConfig = new SleepConfig();
    @Config.Name("wake_up")
    public static WakeUpConfig wakeUpConfig = new WakeUpConfig();

    public static class GeneralConfig {

        @Config.Name("Always Set Spawn")
        @Config.Comment("Set player spawn point when attempting to sleep in a bed, even when not successful, e. g. before bedtime.")
        public boolean setSpawnAlways = true;
        @Config.Name("Set Spawn On Wake Up")
        @Config.Comment("Should the player spawn point be set after sleeping in a bed. Disabling this and \"Always Set Spawn\" will prevent beds from setting the player spawn point.")
        public boolean setSpawnOnWakeUp = true;
        @Config.Name("Spawn Monster")
        @Config.Comment("Spawn monster and wake player when sleeping in an insufficiently lit area.")
        public boolean spawnMonster = true;
        @Config.Name("Spawn Monster Chance")
        @Config.Comment("Chance to spawn a monster, higher numbers make it more likely to happen.")
        @Config.RangeInt(min = 0)
        public int spawnMonsterChance = 5;

    }

    public static class SleepConfig {

        @Config.Name("Bedtime Start")
        @Config.Comment("Time from when onwards sleeping in a bed is possible.")
        @Config.RangeInt(min = 0, max = 24000)
        public int bedtimeStart = 12541;
        @Config.Name("Bedtime End")
        @Config.Comment("Time until when sleeping is permitted.")
        @Config.RangeInt(min = 0, max = 24000)
        public int bedtimeEnd = 23458;
        @Config.Name("Range Check")
        @Config.Comment("Check if the player is close enough to the bed.")
        public boolean rangeCheck = false;
        @Config.Name("Obstruction Check")
        @Config.Comment("Check if the bed has enough open space above it.")
        public boolean obstructionCheck = true;
        @Config.Name("Monster Check")
        @Config.Comment("Check if monsters are nearby.")
        public boolean monsterCheck = true;
        @Config.Name("Glowing Monsters")
        @Config.Comment("Should monsters preventing the player from sleeping glow.")
        public boolean glow = true;
        @Config.Name("Glow Duration")
        @Config.Comment("Duration in ticks for which the monsters nearby will glow.")
        @Config.RangeInt(min = 0)
        public int glowDuration = 60;
        @Config.Name("Named Monsters")
        @Config.Comment("Should named monsters prevent the player from sleeping.")
        public boolean namedMonsters = false;
        @Config.Name("Persistent Monsters")
        @Config.Comment("Should persistent monsters (unable to despawn via a vanilla tag) prevent the player from sleeping.")
        public boolean persistentMonsters = false;

    }

    public static class WakeUpConfig {

        @Config.Name("Wake Up Time")
        @Config.Comment("Time being set after sleeping successfully.")
        @Config.RangeInt(min = 0, max = 24000)
        public int wakeUpTime = 0;
        @Config.Name("Heal Player")
        @Config.Comment("Should the player be healed when waking up.")
        public boolean heal = true;
        @Config.Name("Heal Amount")
        @Config.RangeInt(min = 0)
        @Config.Comment("Amount of health the player should be healed by. Set to 0 to fully heal.")
        public int healAmount = 0;
        @Config.Name("Loose Food")
        @Config.Comment("Should the player loose some food after waking up.")
        public boolean starve = false;
        @Config.Name("Food Amount")
        @Config.Comment("Amount of food to loose when waking up. Set to 0 to completely starve the player. Negative values will feed the player instead.")
        public int starveAmount = 3;
        @Config.Name("Clear Potions")
        @Config.Comment("Clear potion effects after the player wakes up.")
        public ClearPotions clearPotions = ClearPotions.BOTH;
        @Config.Name("Apply Effects")
        @Config.Comment("Should custom potion effects be applied to the player after waking up.")
        public boolean effects = false;
        @Config.Name("Effects To Apply")
        @Config.Comment("Potion effects to be given to the player after waking up. Enter as \"modid:effect,duration,amplifier,hideParticles\", values are like the /effect command. Amplifier and hideParticles are optional.")
        public String[] potionEffects = new String[]{"minecraft:regeneration,30,0,false"};

    }

}
