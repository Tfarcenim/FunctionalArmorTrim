package tfar.functionalarmortrim;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import tfar.functionalarmortrim.init.ModAttributes;

import javax.annotation.Nullable;
import java.util.UUID;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class FunctionalArmorTrim {

    public static final String MOD_ID = "functionalarmortrim";
    public static final UUID[] trim_slot_uuids = new UUID[]{
            UUID.fromString("81a9ed67-25f2-40f5-9a14-5607bd76b506"),
            UUID.fromString("71dcc566-b25a-4f1a-b564-c9a8aa9e784f"),
            UUID.fromString("0a2787e9-a297-4ac9-9935-bed6f145e00c"),
            UUID.fromString("eb4dbe61-f520-44fe-81ee-7754188eb320")};

    public static final ResourceLocation[] trim_modifiers = new ResourceLocation[] {
            id(EquipmentSlot.HEAD.getName()),
            id(EquipmentSlot.CHEST.getName()),
            id(EquipmentSlot.LEGS.getName()),
            id(EquipmentSlot.FEET.getName())
    };

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {
    }

    public static double blockDrops(Entity breaker) {
        return breaker instanceof Player player ? player.getAttributeValue(ModAttributes.EXPERIENCE_BOOST) : 1;
    }

    public static void livingDamagePost(LivingEntity target,DamageSource source) {
        Entity attacker = source.getDirectEntity();
        if (attacker instanceof LivingEntity) {
            double quartz = target.getAttributeValue(ModAttributes.THORNS);
            if (quartz > 0)
                attacker.hurt(target.damageSources().thorns(target), (float) quartz);
        }
    }

    public static boolean entityInvulnerabilityCheck(Entity target,DamageSource source) {
        return target instanceof LivingEntity livingTarget && source.is(DamageTypeTags.IS_FIRE) && livingTarget.getAttributeValue(ModAttributes.FIRE_RESISTANCE) >= 1;
    }

    public static double livingIncomingDamageEvent(LivingEntity target,DamageSource source) {
        return 1 - (source.is(DamageTypeTags.IS_FIRE) ? target.getAttributeValue(ModAttributes.FIRE_RESISTANCE) : 0);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID,path);
    }

    @Nullable
    public static ArmorTrim getTrim(ItemStack stack) {
        return stack.get(DataComponents.TRIM);
    }

    @Nullable
    public static TrimMaterial getTrimMaterial(ItemStack stack) {
        ArmorTrim armorTrim = getTrim(stack);
        return armorTrim == null ? null : armorTrim.material().value();
    }

    @Nullable
    public static Item getTrimItem(ItemStack stack) {
        TrimMaterial trimMaterial = getTrimMaterial(stack);
        return trimMaterial == null ? null : trimMaterial.ingredient().value();
    }
}