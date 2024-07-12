package tfar.functionalarmortrim.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tfar.functionalarmortrim.FunctionalArmorTrim;

import java.util.Iterator;

@Mixin(PiglinAi.class)
public class PiglinAiMixinFabric {
    @Inject(method = "isWearingGold",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ArmorItem;getMaterial()Lnet/minecraft/core/Holder;"),locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void checkTrim(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir, Iterable iterable, Iterator var2, ItemStack itemStack, Item item) {
        if (FunctionalArmorTrim.getTrimItem(itemStack) == Items.GOLD_INGOT) {
            cir.setReturnValue(true);
        }
    }
}
