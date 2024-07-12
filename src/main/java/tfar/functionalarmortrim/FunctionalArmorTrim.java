package tfar.functionalarmortrim;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import tfar.functionalarmortrim.config.ConfigHandler;
import tfar.functionalarmortrim.datagen.ModDatagen;
import tfar.functionalarmortrim.init.ModAttributes;
import tfar.functionalarmortrim.network.PacketHandler;
import tfar.functionalarmortrim.network.client.S2CConfigPacket;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FunctionalArmorTrim.MODID)
public class FunctionalArmorTrim {
    public static final String MODID = "functionalarmortrim";

    public FunctionalArmorTrim() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::register);
        bus.addListener(ModDatagen::start);
        bus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.addListener(TrimEffects::attributes);
        MinecraftForge.EVENT_BUS.addListener(TrimEffects::breakBlock);
        MinecraftForge.EVENT_BUS.addListener(TrimEffects::livingXp);
        MinecraftForge.EVENT_BUS.addListener(TrimEffects::livinghurt);
        MinecraftForge.EVENT_BUS.addListener(TrimEffects::fireResist);
        MinecraftForge.EVENT_BUS.addListener(TrimEffects::fireDamage);
        MinecraftForge.EVENT_BUS.addListener(this::reload);
        MinecraftForge.EVENT_BUS.addListener(this::join);
    }

    private void setup(FMLCommonSetupEvent event) {
        ConfigHandler.writeIfEmpty();
        PacketHandler.registerPackets();
    }

    private void reload(AddReloadListenerEvent event) {
        event.addListener(new TrimEffectReloadListener());
    }

    private void join(PlayerEvent.PlayerLoggedInEvent event) {
        PacketHandler.sendToClient(new S2CConfigPacket(ConfigHandler.MAP), (ServerPlayer) event.getEntity());
    }


    private void register(RegisterEvent event) {
        event.register(Registries.ATTRIBUTE,id("night_vision"),() -> ModAttributes.NIGHT_VISION);
        event.register(Registries.ATTRIBUTE,id("fire_resistance"),() -> ModAttributes.FIRE_RESISTANCE);
        event.register(Registries.ATTRIBUTE,id("experience_boost"),() -> ModAttributes.EXPERIENCE_BOOST);
        event.register(Registries.ATTRIBUTE,id("thorns"),() -> ModAttributes.THORNS);
        event.register(Registries.ATTRIBUTE,id("gossip_boost"),() -> ModAttributes.GOSSIP_BOOST);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID,path);
    }
}
