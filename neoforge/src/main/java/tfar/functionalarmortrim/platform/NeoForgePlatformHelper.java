package tfar.functionalarmortrim.platform;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import tfar.functionalarmortrim.network.PacketHandlerNeoForge;
import tfar.functionalarmortrim.network.client.S2CModPacket;
import tfar.functionalarmortrim.platform.services.IPlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    public static PayloadRegistrar registrar;
    @Override
    public <MSG extends S2CModPacket> void registerClientPacket(CustomPacketPayload.Type<MSG> type, StreamCodec<RegistryFriendlyByteBuf,MSG> streamCodec) {
        registrar.playToClient(type, streamCodec, (p, t) -> p.handleClient());
    }

    @Override
    public void sendToClient(CustomPacketPayload msg, ServerPlayer player) {
        PacketHandlerNeoForge.sendToClient(msg,player);
    }

    @Override
    public MinecraftServer getStaticServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public Holder<Attribute> getSwimSpeed() {
        return NeoForgeMod.SWIM_SPEED;
    }
}