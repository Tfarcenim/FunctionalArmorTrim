package tfar.functionalarmortrim.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.functionalarmortrim.FunctionalArmorTrim;

@Mixin(IItemStackExtension.class)
public interface IItemStackExtensionMixin {

    @Inject(method = "makesPiglinsNeutral",at = @At("RETURN"),cancellable = true)
    private void handle(LivingEntity wearer, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (FunctionalArmorTrim.getTrimItem((ItemStack)(Object)this) == Items.GOLD_INGOT) {
            cir.setReturnValue(true);
        }
    }
}
