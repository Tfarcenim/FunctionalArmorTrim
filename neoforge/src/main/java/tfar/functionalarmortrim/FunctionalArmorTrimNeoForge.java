package tfar.functionalarmortrim;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import tfar.functionalarmortrim.config.ConfigHandler;
import tfar.functionalarmortrim.config.TrimEffectReloadListener;
import tfar.functionalarmortrim.datagen.ModDatagen;
import tfar.functionalarmortrim.network.PacketHandler;
import tfar.functionalarmortrim.network.PacketHandlerNeoForge;
import tfar.functionalarmortrim.network.client.S2CConfigPacket;
import tfar.functionalarmortrim.platform.Services;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FunctionalArmorTrim.MOD_ID)
public class FunctionalArmorTrimNeoForge {

    public FunctionalArmorTrimNeoForge(IEventBus bus) {
        bus.addListener(this::register);
        bus.addListener(ModDatagen::start);
        bus.addListener(this::setup);
        bus.addListener(PacketHandlerNeoForge::register);
        NeoForge.EVENT_BUS.addListener(TrimEffects::attributes);
        NeoForge.EVENT_BUS.addListener(TrimEffects::breakBlock);
        NeoForge.EVENT_BUS.addListener(TrimEffects::livingXp);
        NeoForge.EVENT_BUS.addListener(TrimEffects::livinghurt);
        NeoForge.EVENT_BUS.addListener(TrimEffects::fireResist);
        NeoForge.EVENT_BUS.addListener(TrimEffects::fireDamage);
        NeoForge.EVENT_BUS.addListener(this::reload);
        NeoForge.EVENT_BUS.addListener(this::join);
    }

    private void setup(FMLCommonSetupEvent event) {
        ConfigHandler.writeIfEmpty();
    }

    private void reload(AddReloadListenerEvent event) {
        event.addListener(new TrimEffectReloadListener());
    }

    private void join(PlayerEvent.PlayerLoggedInEvent event) {
        Services.PLATFORM.sendToClient(S2CConfigPacket.create(ConfigHandler.MAP), (ServerPlayer) event.getEntity());
    }


    private void register(RegisterEvent event) {
    }

}
