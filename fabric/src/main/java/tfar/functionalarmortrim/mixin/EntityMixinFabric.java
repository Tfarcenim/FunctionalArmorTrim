package tfar.functionalarmortrim.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.functionalarmortrim.FunctionalArmorTrim;

@Mixin(Entity.class)
public class EntityMixinFabric {

    @Inject(method = "isInvulnerableTo",at = @At("RETURN"),cancellable = true)
    private void invulnerableEvent(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        boolean alreadyInvulnerable = cir.getReturnValue();
        if (!alreadyInvulnerable) {
            boolean b = FunctionalArmorTrim.entityInvulnerabilityCheck((Entity) (Object) this, source);
            if (b) cir.setReturnValue(b);
        }
    }

}
