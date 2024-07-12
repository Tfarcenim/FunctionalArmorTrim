package tfar.functionalarmortrim.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.functionalarmortrim.init.ModAttributes;

@Mixin(Attributes.class)
public class AttributesMixin {

    @Inject(method = "bootstrap",at = @At("RETURN"))
    private static void piggyback(Registry<Attribute> pRegistry, CallbackInfoReturnable<Holder<Attribute>> cir) {
        ModAttributes.bootstrap(pRegistry);
    }
}
