package tfar.functionalarmortrim.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import tfar.functionalarmortrim.init.ModAttributes;
import tfar.functionalarmortrim.network.client.S2CConfigPacket;

import java.util.HashMap;
import java.util.Map;

public class ModClient {

    public static Map<Item,Map<Holder<Attribute>, Map<EquipmentSlot, AttributeModifier>>> MAP = new HashMap<>();


    public static void handleConfigPacket(S2CConfigPacket packet) {
        MAP = S2CConfigPacket.fromTag(packet.tag());
    }


    public static Level getClientWorld() {
        return Minecraft.getInstance().level;
    }

    public static double getNightVisionScale(LocalPlayer player) {
        return player.getAttributeValue(ModAttributes.NIGHT_VISION);
    }

}
