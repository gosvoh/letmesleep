package com.fuzs.letmesleep.handler;

import com.fuzs.letmesleep.LetMeSleep;
import com.fuzs.letmesleep.util.ClearPotions;
import com.fuzs.letmesleep.util.SetSpawnPoint;
import net.minecraftforge.common.config.Config;

@SuppressWarnings("WeakerAccess")
@Config(modid = LetMeSleep.MODID)
public class ConfigBuildHandler {

    @Config.Name("general")
    public static GeneralConfig generalConfig = new GeneralConfig();
    @Config.Name("sleep_timings")
    public static SleepTimingsConfig sleepTimingsConfig = new SleepTimingsConfig();
    @Config.Name("sleeping_checks")
    public static SleepConfig sleepConfig = new SleepConfig();
    @Config.Name("wake_up_actions")
    public static WakeUpConfig wakeUpConfig = new WakeUpConfig();

    public static class GeneralConfig {

        @Config.Name("Set Respawn Point")
        @Config.Comment("How beds should be used for setting the respawn point.")
        public SetSpawnPoint setSpawn = SetSpawnPoint.INTERACT;
        @Config.Name("Always Set Spawn")
        @Config.Comment("Disable to prevent setting a new respawn point when there is already one present at another bed. The other bed will have to be removed to set a new respawn point.")
        public boolean setSpawnAlways = true;
        @Config.Name("Spawn Monster")
        @Config.Comment("Spawn a monster and wake player when sleeping in an insufficiently lit area.")
        public boolean spawnMonster = true;
        @Config.Name("Spawn Monster Chance")
        @Config.Comment("Chance to spawn a monster, higher numbers make it more likely to happen.")
        @Config.RangeInt(min = 0)
        public int spawnMonsterChance = 5;

    }

    public static class SleepTimingsConfig {

        @Config.Name("Bedtime Start")
        @Config.Comment("Time from when onwards sleeping in a bed is possible.")
        @Config.RangeInt(min = 0, max = 24000)
        public int bedtimeStart = 12541;
        @Config.Name("Bedtime End")
        @Config.Comment("Time until when sleeping is possible.")
        @Config.RangeInt(min = 0, max = 24000)
        public int bedtimeEnd = 23458;
        @Config.Name("Sleep During Thunder")
        @Config.Comment("Is going to bed during a thunderstorm permitted.")
        public boolean bedtimeThunder = true;
        @Config.Name("Sleep During Rain")
        @Config.Comment("Is going to bed when it's raining permitted.")
        public boolean bedtimeRain = false;
        @Config.Name("Time 12h Format")
        @Config.Comment("Use 12h format for status messages.")
        public boolean timeTwelve = false;
        @Config.Name("Clock Time Tooltip")
        @Config.Comment("Add current time to the clock item tooltip.")
        public boolean timeClock = true;
        @Config.Name("Instant Sleeping")
        @Config.Comment("Removes the falling asleep animation, so you wake up instantly after going to bed. Some options from \\\"Set Respawn Point\\\" will no longer be accessible then.")
        public boolean instantSleeping = false;
        @Config.Name("Wake Up Time")
        @Config.Comment("Time being set after sleeping successfully.")
        @Config.RangeInt(min = 0, max = 24000)
        public int wakeUpTime = 0;

    }

    public static class SleepConfig {

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
        @Config.Comment("Potion effects to be given to the player after waking up. Enter as \"modid:effect,duration,amplifier,hideParticles\", values are based on /effect command. Amplifier and hideParticles are optional.")
        public String[] potionEffects = new String[]{"minecraft:regeneration,30,0,false"};
        @Config.Name("Persistent Chat")
        @Config.Comment("Keep chat open after waking up if it contains any text.")
        public boolean persistentChat = true;

    }

}
