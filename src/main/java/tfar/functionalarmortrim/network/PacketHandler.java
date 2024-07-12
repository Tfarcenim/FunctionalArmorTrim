package tfar.functionalarmortrim.network;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import tfar.functionalarmortrim.FunctionalArmorTrim;
import tfar.functionalarmortrim.network.client.S2CConfigPacket;
import tfar.functionalarmortrim.network.client.S2CModPacket;
import tfar.functionalarmortrim.network.server.C2SModPacket;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketHandler {

    public static void registerPackets() {
        if (true) {
            registerClientPackets();
        }
    }

    public static void registerClientPackets() {
        registerClientPacket(S2CConfigPacket.class, S2CConfigPacket::new);
    }

    static int i;

    static  <MSG extends S2CModPacket> void registerClientPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {
        PacketHandler.INSTANCE.registerMessage(i++, packetLocation, MSG::write, reader, wrapS2C());
    }

    public static SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(FunctionalArmorTrim.id("packets"), () -> "1.0", s -> true, s -> true);


    public static <MSG extends S2CModPacket> BiConsumer<MSG, Supplier<NetworkEvent.Context>> wrapS2C() {
        return ((msg, contextSupplier) -> {
            contextSupplier.get().enqueueWork(msg::handleClient);
            contextSupplier.get().setPacketHandled(true);
        });
    }

    public static <MSG extends C2SModPacket> BiConsumer<MSG, Supplier<NetworkEvent.Context>> wrapC2S() {
        return ((msg, contextSupplier) -> {
            ServerPlayer player = contextSupplier.get().getSender();
            contextSupplier.get().enqueueWork(() -> msg.handleServer(player));
            contextSupplier.get().setPacketHandled(true);
        });
    }

    public static <MSG> void sendToClient(MSG packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <MSG> void sendToServer(MSG packet) {
        INSTANCE.sendToServer(packet);
    }


}
