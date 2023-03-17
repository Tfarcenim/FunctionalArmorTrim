package tfar.functionalarmortrim.mixin;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.functionalarmortrim.TrimEffects;

@Mixin(Villager.class)
public class VillagerMixin {
    @Inject(method = "getPlayerReputation",at = @At("RETURN"),cancellable = true)
    private void addRep(Player pPlayer, CallbackInfoReturnable<Integer> cir) {
        int emerald = TrimEffects.countTrim(pPlayer, Items.EMERALD);
        if (emerald > 0) {
            int rep = cir.getReturnValue();
            if (rep > 0) {
                cir.setReturnValue((int) (rep * (1 + .1 * emerald)));
            }
        }
    }
}
