package tfar.functionalarmortrim.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.functionalarmortrim.FunctionalArmorTrim;

@Mixin(Player.class)
public class PlayerMixinFabric {

    @Inject(method = "actuallyHurt",at = @At("RETURN"))
    private void fireDamageEvent(DamageSource source, float damageAmount, CallbackInfo ci) {
        FunctionalArmorTrim.livingDamagePost((LivingEntity) (Object)this,source);
    }

}
