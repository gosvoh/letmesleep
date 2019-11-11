package com.fuzs.letmesleep.proxy;

import com.fuzs.letmesleep.handler.*;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override
    public void onClientSetup() {

        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new SetSpawnHandler());

    }

    @Override
    public void onServerSetup() {

    }

}
