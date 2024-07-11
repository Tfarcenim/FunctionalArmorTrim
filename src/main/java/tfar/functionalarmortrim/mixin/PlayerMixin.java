package tfar.functionalarmortrim.mixin;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.functionalarmortrim.init.ModAttributes;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "createAttributes",at = @At("RETURN"))
    private static void addModifiers(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        cir.getReturnValue().add(ModAttributes.NIGHT_VISION);
    }
}
