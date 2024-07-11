package tfar.functionalarmortrim.mixin;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.functionalarmortrim.TrimEffects;
import tfar.functionalarmortrim.init.ModAttributes;

@Mixin(Villager.class)
public class VillagerMixin {
    @Inject(method = "getPlayerReputation", at = @At("RETURN"), cancellable = true)
    private void addRep(Player player, CallbackInfoReturnable<Integer> cir) {
        int rep = cir.getReturnValue();
        if (rep > 0) {
            double gossip_boost = player.getAttributeValue(ModAttributes.GOSSIP_BOOST);
            if (gossip_boost != 1) {
                cir.setReturnValue((int) (rep * gossip_boost));
            }
        }
    }
}
