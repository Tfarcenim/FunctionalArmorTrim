package tfar.functionalarmortrim.network;


import net.minecraft.resources.ResourceLocation;
import tfar.functionalarmortrim.FunctionalArmorTrim;
import tfar.functionalarmortrim.network.client.S2CConfigPacket;
import tfar.functionalarmortrim.platform.Services;

import java.util.Locale;

public class PacketHandler {

    public static void registerPackets() {
        registerClientPackets();
    }

    public static void registerClientPackets() {
        Services.PLATFORM.registerClientPacket(S2CConfigPacket.TYPE,S2CConfigPacket.STREAM_CODEC);
    }

    public static ResourceLocation packet(Class<?> clazz) {
        return FunctionalArmorTrim.id(clazz.getName().toLowerCase(Locale.ROOT));
    }

}
