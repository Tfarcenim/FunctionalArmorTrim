package tfar.functionalarmortrim;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import tfar.functionalarmortrim.init.ModAttributes;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FunctionalArmorTrim.MODID)
public class FunctionalArmorTrim {
    public static final String MODID = "functionalarmortrim";

    public FunctionalArmorTrim() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::register);
        MinecraftForge.EVENT_BUS.addListener(TrimEffects::attributes);
        MinecraftForge.EVENT_BUS.addListener(TrimEffects::breakBlock);
        MinecraftForge.EVENT_BUS.addListener(TrimEffects::livingXp);
        MinecraftForge.EVENT_BUS.addListener(TrimEffects::livinghurt);
        MinecraftForge.EVENT_BUS.addListener(TrimEffects::fireResist);
        MinecraftForge.EVENT_BUS.addListener(TrimEffects::fireDamage);
    }


    private void register(RegisterEvent event) {
        event.register(Registries.ATTRIBUTE,id("night_vision"),() -> ModAttributes.NIGHT_VISION);
        event.register(Registries.ATTRIBUTE,id("fire_resistance"),() -> ModAttributes.FIRE_RESISTANCE);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID,path);
    }
}
