package tfar.functionalarmortrim;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FunctionalArmorTrim.MODID)
public class FunctionalArmorTrim {
    public static final String MODID = "functionalarmortrim";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public FunctionalArmorTrim() {
        MinecraftForge.EVENT_BUS.addListener(TrimEffects::attributes);
        MinecraftForge.EVENT_BUS.addListener(TrimEffects::breakBlock);
        MinecraftForge.EVENT_BUS.addListener(TrimEffects::livingXp);
        MinecraftForge.EVENT_BUS.addListener(TrimEffects::livinghurt);
        MinecraftForge.EVENT_BUS.addListener(TrimEffects::fireResist);
        MinecraftForge.EVENT_BUS.addListener(TrimEffects::fireDamage);
    }
}
