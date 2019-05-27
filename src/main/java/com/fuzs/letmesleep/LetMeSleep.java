package com.fuzs.letmesleep;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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
public class LetMeSleep
{
    public static final String MODID = "letmesleep";
    public static final String NAME = "Let Me Sleep";
    public static final String VERSION = "@VERSION@";
    public static final String RANGE = "[1.12.2]";
    public static final String REMOTE = "*";
    public static final String DEPENDENCIES = "required-after:forge@[14.23.5.2768,)";
    public static final String FINGERPRINT = "@FINGERPRINT@";

    public static final Logger LOGGER = LogManager.getLogger(LetMeSleep.NAME);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ModEventHandler());
    }

    @EventHandler
    public void fingerprintViolation(FMLFingerprintViolationEvent event) {
        LOGGER.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }
}
