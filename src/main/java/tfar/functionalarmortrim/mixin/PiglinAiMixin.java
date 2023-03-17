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
import tfar.functionalarmortrim.TrimEffects;

import java.util.Iterator;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
	@Inject(at = @At(value = "INVOKE",target = "Lnet/minecraft/world/item/ItemStack;makesPiglinsNeutral(Lnet/minecraft/world/entity/LivingEntity;)Z",remap = false),
			method = "isWearingGold",cancellable = true,locals = LocalCapture.CAPTURE_FAILHARD)
	private static void init(LivingEntity pLivingEntity, CallbackInfoReturnable<Boolean> cir, Iterator var1, ItemStack itemstack, Item item) {
		if (TrimEffects.getTrimItem(pLivingEntity.level, itemstack) == Items.GOLD_INGOT) {
			cir.setReturnValue(true);
		}
	}
}
