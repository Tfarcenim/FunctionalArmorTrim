package tfar.functionalarmortrim.init;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import tfar.functionalarmortrim.FunctionalArmorTrim;

public class ModAttributes {

    public static final Attribute NIGHT_VISION = new RangedAttribute("effect.minecraft.night_vision",0,0,1).setSyncable(true);
    public static final Attribute FIRE_RESISTANCE = new RangedAttribute("effect.minecraft.fire_resistance",0,0,1).setSyncable(true);
    public static final Attribute EXPERIENCE_BOOST = new RangedAttribute(FunctionalArmorTrim.MODID+".experience_boost",1,0,2048).setSyncable(true);
    public static final Attribute THORNS = new RangedAttribute("enchantment.minecraft.thorns",0,0,2048).setSyncable(true);
    public static final Attribute GOSSIP_BOOST = new RangedAttribute(FunctionalArmorTrim.MODID +".gossip_boost",1,0,2048).setSyncable(true);

}
