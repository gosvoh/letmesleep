package com.fuzs.letmesleep.network.message;

import com.fuzs.letmesleep.helper.SetSpawnHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
//import net.minecraft.network.play.server.SSpawnPositionPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestSpawnMessage {

    private final RequestSpawnMessageData[] data;

    public RequestSpawnMessage(RequestSpawnMessageData... data) {

        this.data = data;

    }

    public static void writePacketData(RequestSpawnMessage message, PacketBuffer buf) {

        buf.writeVarInt(message.data.length);

        for (RequestSpawnMessageData data : message.data) {

            buf.writeBlockPos(data.getPosition());

        }

    }

    public static RequestSpawnMessage readPacketData(PacketBuffer buf) {

        int size = buf.readVarInt();
        RequestSpawnMessageData[] data = new RequestSpawnMessageData[size];

        for (int i = 0; i < size; i++) {

            BlockPos pos = buf.readBlockPos();

            data[i] = new RequestSpawnMessageData(pos);

        }

        return new RequestSpawnMessage(data);

    }

    @SuppressWarnings("ConstantConditions")
    public static void processPacket(final RequestSpawnMessage message, Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            for (RequestSpawnMessageData data : message.data) {

                ServerPlayerEntity player = ctx.get().getSender();
                BlockPos pos = data.getPosition();

                // can actually be null, although the IDE says it won't be, therefor a null check later on
                BlockPos spawn = player.getBedPosition().isPresent() ? player.getBedPosition().get() : null;

                //if (pos.equals(player.world.getSpawnPoint()) && spawn != null) {
                if (pos.equals(player.func_241140_K_()) && spawn != null) {

                    player.connection.sendPacket(new SSpawnPositionPacket(spawn));

                } else if (pos.equals(player.getBedPosition().orElse(null)) && SetSpawnHelper.isNewSpawnAllowed(player.world, player, pos, null)) {

                    player.func_241153_a_(player.func_241141_L_(), pos, false, false);
                    player.connection.sendPacket(new SSpawnPositionPacket(pos));

                }

            }

        });

        ctx.get().setPacketHandled(true);

    }

    public static class RequestSpawnMessageData {

        private final BlockPos pos;

        public RequestSpawnMessageData(BlockPos pos) {

            this.pos = pos;

        }

        private BlockPos getPosition() {
            return this.pos;
        }

    }

}
