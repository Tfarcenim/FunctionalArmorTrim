package tfar.functionalarmortrim.network.server;

import net.minecraft.server.level.ServerPlayer;
import tfar.functionalarmortrim.network.ModPacket;

public interface C2SModPacket extends ModPacket {

    void handleServer(ServerPlayer player);

}
