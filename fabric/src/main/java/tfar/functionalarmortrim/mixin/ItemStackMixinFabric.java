package tfar.functionalarmortrim.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Inject;
import tfar.functionalarmortrim.ItemStackDuck;

import java.util.function.BiConsumer;

@Mixin(ItemStack.class)
public class ItemStackMixinFabric implements ItemStackDuck {



    @Overwrite
    public void forEachModifier(EquipmentSlotGroup slotGroup, BiConsumer<Holder<Attribute>, AttributeModifier> action) {
        ItemAttributeModifiers itemAttributeModifiers = getAttributeModifiers();
        itemAttributeModifiers.forEach(slotGroup, action);


        EnchantmentHelper.forEachModifier((ItemStack) (Object)this, slotGroup, action);
    }


    @Overwrite
    public void forEachModifier(EquipmentSlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> action) {
        ItemAttributeModifiers itemAttributeModifiers = getAttributeModifiers();
        itemAttributeModifiers.forEach(slot, action);
        EnchantmentHelper.forEachModifier((ItemStack) (Object)this, slot, action);
    }
}
