package tfar.functionalarmortrim.init;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import tfar.functionalarmortrim.FunctionalArmorTrim;

public class ModAttributes {

    public static final Holder<Attribute> NIGHT_VISION = register("night_vision",new RangedAttribute("effect.minecraft.night_vision",0,0,1).setSyncable(true));
    public static final Holder<Attribute> FIRE_RESISTANCE = register("fire_resistance",new RangedAttribute("effect.minecraft.fire_resistance",0,0,1).setSyncable(true));
    public static final Holder<Attribute> EXPERIENCE_BOOST = register("experience_boost",new RangedAttribute(FunctionalArmorTrim.MOD_ID +".experience_boost",1,0,2048).setSyncable(true));
    public static final Holder<Attribute> THORNS = register("thorns",new RangedAttribute("enchantment.minecraft.thorns",0,0,2048).setSyncable(true));
    public static final Holder<Attribute> GOSSIP_BOOST = register("gossip_boost",new RangedAttribute(FunctionalArmorTrim.MOD_ID +".gossip_boost",1,0,2048).setSyncable(true));

    public static Holder<Attribute> register(String pName, Attribute pAttribute) {
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, FunctionalArmorTrim.id(pName), pAttribute);
    }

    public static void bootstrap(Registry<Attribute> pRegistry) {
    }

}
