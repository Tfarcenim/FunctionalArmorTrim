package tfar.functionalarmortrim.mixin;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.functionalarmortrim.TrimEffects;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow public abstract ItemStack getItem();

    @Inject(method = "hurt",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/item/Item;canBeHurtBy(Lnet/minecraft/world/damagesource/DamageSource;)Z"),cancellable = true)
    private void fireProtect(DamageSource pSource, float pAmount, CallbackInfoReturnable<Boolean> cir) {
        if (pSource.is(DamageTypeTags.IS_FIRE) && TrimEffects.getTrimItem(level(), getItem()) == Items.NETHERITE_INGOT) {
            cir.setReturnValue(false);
        }
    }
}
