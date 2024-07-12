package tfar.functionalarmortrim.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tfar.functionalarmortrim.FunctionalArmorTrim;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixinFabric {

    @Inject(method = "processMobExperience",at = @At(value = "RETURN",ordinal = 0),locals = LocalCapture.CAPTURE_FAILHARD)
    private static void adjXp(ServerLevel level, Entity killer, Entity mob, int experience, CallbackInfoReturnable<Integer> cir, LivingEntity livingEntity, MutableFloat mutableFloat) {
        if (killer instanceof Player killerPlayer) {
            mutableFloat.setValue(mutableFloat.floatValue() * FunctionalArmorTrim.livingExperienceDrops(killerPlayer));
        }
    }
}
