package tfar.functionalarmortrim;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import tfar.functionalarmortrim.client.ModClient;
import tfar.functionalarmortrim.config.ConfigHandler;
import tfar.functionalarmortrim.init.ModAttributes;
import tfar.functionalarmortrim.network.PacketHandler;
import tfar.functionalarmortrim.platform.Services;

import java.util.Map;

public class FunctionalArmorTrimFabric implements ModInitializer {

    public static MinecraftServer server;
    public static final Holder<Attribute> SWIM_SPEED = ModAttributes.register("swim_speed",new RangedAttribute(FunctionalArmorTrim.MOD_ID +".swim_speed",1,0,2048).setSyncable(true));

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> FunctionalArmorTrimFabric.server = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> FunctionalArmorTrimFabric.server = null);

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new ReloadListenerFabric());

        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        PacketHandler.registerPackets();
        // Use Fabric to bootstrap the Common mod.
        FunctionalArmorTrim.init();
    }

    public static ItemAttributeModifiers computeModifiedAttributes(ItemStack stack, ItemAttributeModifiers defaultModifiers) {

        ItemAttributeModifiersBuilderFabric builder = new ItemAttributeModifiersBuilderFabric(defaultModifiers);
        attributes(stack,defaultModifiers,builder);
        return builder.build(defaultModifiers.showInTooltip());
    }

    public static void attributes(ItemStack stack, ItemAttributeModifiers defaultModifiers,ItemAttributeModifiersBuilderFabric builder) {
        Item item = stack.getItem();
        if (item instanceof ArmorItem armorItem) {
            EquipmentSlot slot = armorItem.getEquipmentSlot();
            Item trim = FunctionalArmorTrim.getTrimItem(stack);
            var map = ConfigHandler.MAP;

            if (Services.PLATFORM.getStaticServer() == null) {
                map = ModClient.MAP;
            }

            map.getOrDefault(trim, Map.of()).forEach((attribute, attributeModifiers) -> builder.addModifier(attribute, attributeModifiers.get(slot), EquipmentSlotGroup.bySlot(slot)));
        }
    }

}
