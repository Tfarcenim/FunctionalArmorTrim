package tfar.functionalarmortrim;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;
import tfar.functionalarmortrim.init.ModAttributes;

public class Client {

    public static Level getClientWorld() {
        return Minecraft.getInstance().level;
    }

    public static double getNightVisionScale(LocalPlayer player) {
        return player.getAttributeValue(ModAttributes.NIGHT_VISION);
    }
}
