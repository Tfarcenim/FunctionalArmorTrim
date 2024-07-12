package tfar.functionalarmortrim.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import tfar.functionalarmortrim.client.ModClient;

@Mixin(LightTexture.class)
public class GameRendererMixin {

    @Shadow @Final private Minecraft minecraft;

    @ModifyVariable(method = "updateLightTexture",
            at = @At(value = "FIELD",target = "Lnet/minecraft/client/renderer/LightTexture;blockLightRedFlicker:F"),ordinal = 7)
    private float adjustVision(float old) {
        if (!minecraft.player.hasEffect(MobEffects.NIGHT_VISION)) {
            return (float) ModClient.getNightVisionScale(minecraft.player);
        }
        return old;
    }

    /*@Inject(method = "updateLightTexture",
            at = @At(value = "INVOKE",target = "Lorg/joml/Vector3f;lerp(Lorg/joml/Vector3fc;F)Lorg/joml/Vector3f;",ordinal = 0,remap = false),locals = LocalCapture.PRINT)
    private void checkLocals(float pPartialTicks, CallbackInfo ci, ClientLevel clientlevel, float f, float f1, float f2, float f3, float f4, float f6, float f5) {

    }*/

}
