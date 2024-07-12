package tfar.functionalarmortrim.network.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import tfar.functionalarmortrim.client.ModClient;
import tfar.functionalarmortrim.config.ConfigHandler;

import java.util.HashMap;
import java.util.Map;


public class S2CConfigPacket implements S2CModPacket {

    public final Map<Item,Map<Attribute, Map<EquipmentSlot, AttributeModifier>>> map;
    CompoundTag tag = new CompoundTag();

    public S2CConfigPacket( Map<Item,Map<Attribute, Map<EquipmentSlot, AttributeModifier>>> map) {
        this.map = map;
    }


    public S2CConfigPacket(FriendlyByteBuf buf) {
        map = new HashMap<>();
        tag = buf.readNbt();
        for (String tagKey : tag.getAllKeys()) {
            CompoundTag attributeMapMapTag = tag.getCompound(tagKey);
            Item item = ConfigHandler.getItem(tagKey);
            Map<Attribute, Map<EquipmentSlot, AttributeModifier>> attributeMapMap = new HashMap<>();
            for (String attributeTagKey : attributeMapMapTag.getAllKeys()) {
                Attribute attribute = ConfigHandler.getAttribute(attributeTagKey);
                Map<EquipmentSlot, AttributeModifier> equipmentSlotAttributeModifierMap = new HashMap<>();
                CompoundTag attributeModifierMapTag = attributeMapMapTag.getCompound(attributeTagKey);
                for (String equipmentSlotKey : attributeModifierMapTag.getAllKeys()) {
                    EquipmentSlot slot = EquipmentSlot.byName(equipmentSlotKey);
                    AttributeModifier attributeModifier = AttributeModifier.load(attributeModifierMapTag.getCompound(equipmentSlotKey));
                    equipmentSlotAttributeModifierMap.put(slot,attributeModifier);
                }
                attributeMapMap.put(attribute,equipmentSlotAttributeModifierMap);
            }
            map.put(item,attributeMapMap);
        }
    }

    @Override
    public void handleClient() {
        ModClient.handleConfigPacket(this);
    }

    @Override
    public void write(FriendlyByteBuf to) {
        for (Map.Entry<Item,Map<Attribute,Map<EquipmentSlot,AttributeModifier>>> mapEntry : map.entrySet()) {
            Item itemKey = mapEntry.getKey();
            CompoundTag attributeToAttributeModifierMapTag = new CompoundTag();
            Map<Attribute,Map<EquipmentSlot,AttributeModifier>> value = mapEntry.getValue();
            for (Map.Entry<Attribute,Map<EquipmentSlot,AttributeModifier>> entry : value.entrySet()) {
                Attribute attributeKey = entry.getKey();
                Map<EquipmentSlot,AttributeModifier> mapValue = entry.getValue();
                CompoundTag equipmentToAttributeModifierTag = new CompoundTag();
                for (Map.Entry<EquipmentSlot,AttributeModifier> entry1 : mapValue.entrySet()) {
                    equipmentToAttributeModifierTag.put(entry1.getKey().getName(),entry1.getValue().save());
                }
                attributeToAttributeModifierMapTag.put(ConfigHandler.getName(attributeKey),equipmentToAttributeModifierTag);
            }
            tag.put(ConfigHandler.getName(itemKey),attributeToAttributeModifierMapTag);
        }
        to.writeNbt(tag);
    }
}
