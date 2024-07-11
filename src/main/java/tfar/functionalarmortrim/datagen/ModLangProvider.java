package tfar.functionalarmortrim.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.common.data.LanguageProvider;
import tfar.functionalarmortrim.FunctionalArmorTrim;
import tfar.functionalarmortrim.init.ModAttributes;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(PackOutput output) {
        super(output, FunctionalArmorTrim.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addAttribute(ModAttributes.EXPERIENCE_BOOST,"Experience Boost");
        addAttribute(ModAttributes.GOSSIP_BOOST,"Gossip Boost");
    }

    protected void addAttribute(Attribute attribute,String value) {
        add(attribute.getDescriptionId(),value);
    }
}
