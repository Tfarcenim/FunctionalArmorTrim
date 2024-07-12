package tfar.functionalarmortrim.network.client;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import tfar.functionalarmortrim.client.ModClient;
import tfar.functionalarmortrim.config.ConfigHandler;
import tfar.functionalarmortrim.network.PacketHandler;

import java.util.HashMap;
import java.util.Map;


public record S2CConfigPacket(CompoundTag tag) implements S2CModPacket {

    public static final CustomPacketPayload.Type<S2CConfigPacket> TYPE = new CustomPacketPayload.Type<>(PacketHandler.packet(S2CConfigPacket.class));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CConfigPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            S2CConfigPacket::tag,
            S2CConfigPacket::new);

    public static CompoundTag toTag(Map<Item,Map<Holder<Attribute>, Map<EquipmentSlot, AttributeModifier>>> map) {
        CompoundTag tag1 = new CompoundTag();
        for (Map.Entry<Item,Map<Holder<Attribute>,Map<EquipmentSlot,AttributeModifier>>> mapEntry : map.entrySet()) {
            Item itemKey = mapEntry.getKey();
            CompoundTag attributeToAttributeModifierMapTag = new CompoundTag();
            Map<Holder<Attribute>,Map<EquipmentSlot,AttributeModifier>> value = mapEntry.getValue();
            for (Map.Entry<Holder<Attribute>,Map<EquipmentSlot,AttributeModifier>> entry : value.entrySet()) {
                Holder<Attribute> attributeKey = entry.getKey();
                Map<EquipmentSlot,AttributeModifier> mapValue = entry.getValue();
                CompoundTag equipmentToAttributeModifierTag = new CompoundTag();
                for (Map.Entry<EquipmentSlot,AttributeModifier> entry1 : mapValue.entrySet()) {
                    equipmentToAttributeModifierTag.put(entry1.getKey().getName(),entry1.getValue().save());
                }
                attributeToAttributeModifierMapTag.put(ConfigHandler.getName(attributeKey),equipmentToAttributeModifierTag);
            }
            tag1.put(ConfigHandler.getName(itemKey),attributeToAttributeModifierMapTag);
        }
        return tag1;
    }

    public static S2CConfigPacket create(Map<Item,Map<Holder<Attribute>, Map<EquipmentSlot, AttributeModifier>>> map) {
        return new S2CConfigPacket(toTag(map));
    }

    public static Map<Item,Map<Holder<Attribute>, Map<EquipmentSlot, AttributeModifier>>> fromTag(CompoundTag tag) {
        Map<Item,Map<Holder<Attribute>, Map<EquipmentSlot, AttributeModifier>>> map = new HashMap<>();
        for (String tagKey : tag.getAllKeys()) {
            CompoundTag attributeMapMapTag = tag.getCompound(tagKey);
            Item item = ConfigHandler.getItem(tagKey);
            Map<Holder<Attribute>, Map<EquipmentSlot, AttributeModifier>> attributeMapMap = new HashMap<>();
            for (String attributeTagKey : attributeMapMapTag.getAllKeys()) {
                Holder<Attribute> attribute = ConfigHandler.getAttribute(attributeTagKey);
                Map<EquipmentSlot, AttributeModifier> equipmentSlotAttributeModifierMap = new HashMap<>();
                CompoundTag attributeModifierMapTag = attributeMapMapTag.getCompound(attributeTagKey);
                for (String equipmentSlotKey : attributeModifierMapTag.getAllKeys()) {
                    EquipmentSlot slot = EquipmentSlot.byName(equipmentSlotKey);
                    AttributeModifier attributeModifier = AttributeModifier.load(attributeModifierMapTag.getCompound(equipmentSlotKey));
                    equipmentSlotAttributeModifierMap.put(slot, attributeModifier);
                }
                attributeMapMap.put(attribute, equipmentSlotAttributeModifierMap);
            }
            map.put(item, attributeMapMap);
        }
        return map;
    }

    @Override
    public void handleClient() {
        ModClient.handleConfigPacket(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
