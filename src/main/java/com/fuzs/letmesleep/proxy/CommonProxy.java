package com.fuzs.letmesleep.proxy;

import com.fuzs.letmesleep.handler.BadDreamHandler;
import com.fuzs.letmesleep.handler.CommonEventHandler;
import com.fuzs.letmesleep.handler.SleepAttemptHandler;
import com.fuzs.letmesleep.handler.WakeUpHandler;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("unused")
public abstract class CommonProxy{

    public void onCommonSetup() {

        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
        MinecraftForge.EVENT_BUS.register(new SleepAttemptHandler());
        MinecraftForge.EVENT_BUS.register(new WakeUpHandler());
        MinecraftForge.EVENT_BUS.register(new BadDreamHandler());

    }

    public abstract void onClientSetup();

    public abstract void onServerSetup();

}