package com.fuzs.letmesleep;

import com.fuzs.letmesleep.handler.BadDreamHandler;
import com.fuzs.letmesleep.handler.SleepAttemptHandler;
import com.fuzs.letmesleep.handler.WakeUpHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = LetMeSleep.MODID,
        name = LetMeSleep.NAME,
        version = LetMeSleep.VERSION,
        acceptedMinecraftVersions = LetMeSleep.RANGE,
        acceptableRemoteVersions = LetMeSleep.REMOTE,
        dependencies = LetMeSleep.DEPENDENCIES,
        certificateFingerprint = LetMeSleep.FINGERPRINT
)
@Mod.EventBusSubscriber(modid = LetMeSleep.MODID)
@SuppressWarnings({"WeakerAccess", "unused"})
public class LetMeSleep {

    public static final String MODID = "letmesleep";
    public static final String NAME = "Let Me Sleep";
    public static final String VERSION = "@VERSION@";
    public static final String RANGE = "[1.12.2]";
    public static final String REMOTE = "*";
    public static final String DEPENDENCIES = "required-after:forge@[14.23.5.2768,)";
    public static final String FINGERPRINT = "@FINGERPRINT@";

    public static final Logger LOGGER = LogManager.getLogger(LetMeSleep.NAME);

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(new SleepAttemptHandler());
        MinecraftForge.EVENT_BUS.register(new WakeUpHandler());
        MinecraftForge.EVENT_BUS.register(new BadDreamHandler());

    }

    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {

        LOGGER.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");

    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent evt) {

        if (evt.getModID().equals(LetMeSleep.MODID)) {
            ConfigManager.sync(LetMeSleep.MODID, Config.Type.INSTANCE);
        }

    }

}
