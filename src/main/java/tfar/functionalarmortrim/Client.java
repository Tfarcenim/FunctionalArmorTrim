package tfar.functionalarmortrim;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

public class Client {

    public static Level getClientWorld() {
        return Minecraft.getInstance().level;
    }

    public static float getNightVisionScale(int amy) {
        return amy/4f;
    }
}
