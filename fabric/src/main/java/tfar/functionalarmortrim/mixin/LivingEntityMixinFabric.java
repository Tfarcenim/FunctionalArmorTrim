package tfar.functionalarmortrim.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.functionalarmortrim.FunctionalArmorTrim;
import tfar.functionalarmortrim.FunctionalArmorTrimFabric;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixinFabric {
    @Shadow public abstract double getAttributeValue(Holder<Attribute> pAttribute);

    @ModifyVariable(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"), argsOnly = true)
    private float changeDamage(float value,DamageSource source){
        return (float) (value * FunctionalArmorTrim.livingIncomingDamageEvent((LivingEntity) (Object) this, source));
    }

    @Inject(method = "createLivingAttributes",at = @At("RETURN"))
    private static void addAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        cir.getReturnValue().add(FunctionalArmorTrimFabric.SWIM_SPEED);
    }


    @Inject(method = "actuallyHurt",at = @At("RETURN"))
    private void fireDamageEvent(DamageSource source, float damageAmount, CallbackInfo ci) {
        FunctionalArmorTrim.livingDamagePost((LivingEntity) (Object)this,source);
    }

    @ModifyArg(method = "jumpInLiquid",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"),index = 1)
    private double adjSwimSpeed(double y) {
        return y * getAttributeValue(FunctionalArmorTrimFabric.SWIM_SPEED);
    }
}
