package tfar.functionalarmortrim.datagen;

import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.common.data.LanguageProvider;
import tfar.functionalarmortrim.FunctionalArmorTrim;
import tfar.functionalarmortrim.init.ModAttributes;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(PackOutput output) {
        super(output, FunctionalArmorTrim.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addAttribute(ModAttributes.EXPERIENCE_BOOST,"Experience Boost");
        addAttribute(ModAttributes.GOSSIP_BOOST,"Gossip Boost");
    }

    protected void addAttribute(Holder<Attribute> attribute, String value) {
        add(attribute.value().getDescriptionId(),value);
    }
}
