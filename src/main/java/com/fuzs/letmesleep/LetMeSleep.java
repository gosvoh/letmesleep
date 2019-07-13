package com.fuzs.letmesleep;

import com.fuzs.letmesleep.handler.CommonEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(LetMeSleep.MODID)
public class LetMeSleep
{
    public static final String MODID = "letmesleep";
    public static final String NAME = "Let Me Sleep";
    public static final Logger LOGGER = LogManager.getLogger(LetMeSleep.NAME);

    public LetMeSleep() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

    }

    private void commonSetup(final FMLCommonSetupEvent evt) {

        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());

    }

}
