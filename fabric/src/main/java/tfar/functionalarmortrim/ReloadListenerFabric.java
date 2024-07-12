package tfar.functionalarmortrim;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import tfar.functionalarmortrim.config.TrimEffectReloadListener;

public class ReloadListenerFabric extends TrimEffectReloadListener implements IdentifiableResourceReloadListener {
    @Override
    public ResourceLocation getFabricId() {
        return FunctionalArmorTrim.id("config");
    }
}
