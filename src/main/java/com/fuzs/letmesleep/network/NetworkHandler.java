package com.fuzs.letmesleep.network;

import com.fuzs.letmesleep.LetMeSleep;
import com.fuzs.letmesleep.network.message.MessageRequestSpawn;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("unused")
public class NetworkHandler {

    private static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(LetMeSleep.MODID);

    public static void init() {
        int discriminator = 0;
        INSTANCE.registerMessage(MessageRequestSpawn.class, MessageRequestSpawn.class, discriminator, Side.SERVER);
    }

    public static void sendToServer(IMessage message) {
        INSTANCE.sendToServer(message);
    }

    public static void sendTo(IMessage message, EntityPlayerMP player) {
        INSTANCE.sendTo(message, player);
    }

    public static void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
        INSTANCE.sendToAllAround(message, point);
    }

    public static void sendToAll(IMessage message) {
        INSTANCE.sendToAll(message);
    }

    public static void sendToAllTeamMembers(IMessage message, EntityPlayerMP player) {

        Team team = player.getTeam();

        if (team != null) {
            for (String s : team.getMembershipCollection()) {
                EntityPlayerMP entityplayermp = player.mcServer.getPlayerList().getPlayerByUsername(s);

                if (entityplayermp != null && entityplayermp != player) {
                    INSTANCE.sendTo(message, entityplayermp);
                }
            }
        }

    }

    public static void sendToTeamOrAllPlayers(IMessage message, EntityPlayerMP player) {

        Team team = player.getTeam();

        if (team == null) {
            INSTANCE.sendToAll(message);
        } else {
            for (int i = 0; i < player.mcServer.getPlayerList().getPlayers().size(); ++i) {
                EntityPlayerMP entityplayermp = player.mcServer.getPlayerList().getPlayers().get(i);

                if (entityplayermp.getTeam() != team) {
                    INSTANCE.sendTo(message, entityplayermp);
                }
            }
        }

    }

}