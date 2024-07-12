package tfar.functionalarmortrim.client;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import tfar.functionalarmortrim.network.client.S2CConfigPacket;

import java.util.HashMap;
import java.util.Map;

public class ModClient {

    public static Map<Item,Map<Attribute, Map<EquipmentSlot, AttributeModifier>>> MAP = new HashMap<>();


    public static void handleConfigPacket(S2CConfigPacket packet) {
        MAP = packet.map;
    }

}
