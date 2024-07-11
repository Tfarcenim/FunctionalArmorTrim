package tfar.functionalarmortrim.init;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class ModAttributes {

    public static final Attribute NIGHT_VISION = new RangedAttribute("effect.minecraft.night_vision",0,0,1).setSyncable(true);
    public static final Attribute FIRE_RESISTANCE = new RangedAttribute("effect.minecraft.fire_resistance",0,0,1).setSyncable(true);

}
