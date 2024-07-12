package tfar.functionalarmortrim.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;


public class TrimEffectReloadListener extends SimplePreparableReloadListener<JsonObject> {
    final Gson gson = new GsonBuilder().create();

    @Override
    protected JsonObject prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return ConfigHandler.read(gson);
    }

    @Override
    protected void apply(JsonObject pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        ConfigHandler.load(pObject);
    }
}
