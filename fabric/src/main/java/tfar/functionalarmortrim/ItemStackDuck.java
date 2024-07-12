package tfar.functionalarmortrim;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public interface ItemStackDuck {

    default ItemAttributeModifiers getAttributeModifiers() {
        ItemAttributeModifiers defaultModifiers = self().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);

        if (defaultModifiers.modifiers().isEmpty()) {
            defaultModifiers = self().getItem().getDefaultAttributeModifiers();
        }

        return FunctionalArmorTrimFabric.computeModifiedAttributes(self(), defaultModifiers);
    }

    default ItemStack self() {
        return (ItemStack) (Object)this;
    }

}
