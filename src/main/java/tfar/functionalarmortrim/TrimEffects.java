package tfar.functionalarmortrim;

import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import tfar.functionalarmortrim.config.ConfigHandler;
import tfar.functionalarmortrim.init.ModAttributes;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public class TrimEffects {

    public static final UUID[] trim_slot_uuids = new UUID[]{
            UUID.fromString("81a9ed67-25f2-40f5-9a14-5607bd76b506"),
            UUID.fromString("71dcc566-b25a-4f1a-b564-c9a8aa9e784f"),
            UUID.fromString("0a2787e9-a297-4ac9-9935-bed6f145e00c"),
            UUID.fromString("eb4dbe61-f520-44fe-81ee-7754188eb320")};

    public static void attributes(ItemAttributeModifierEvent e) {
        ItemStack stack = e.getItemStack();
        Item item = stack.getItem();
        EquipmentSlot slot = e.getSlotType();
        if (item instanceof ArmorItem armorItem && slot == armorItem.getEquipmentSlot()) {
            Item trim = getTrimItem(getWorld(), stack);
            ConfigHandler.MAP.getOrDefault(trim, Map.of()).forEach((attribute, attributeModifiers) -> {
                e.addModifier(attribute, attributeModifiers.get(slot));
            });
        }
    }

    //this is needed because the client thread calls getAttributes, but a direct call to mc.world would crash servers
    public static Level getWorld() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return Client.getClientWorld();
        } else {
            return server.getLevel(Level.OVERWORLD);
        }
    }

    public static void fireResist(LivingAttackEvent e) {
        DamageSource source = e.getSource();
        if (source.is(DamageTypeTags.IS_FIRE)) {
            double fire_resistance = e.getEntity().getAttributeValue(ModAttributes.FIRE_RESISTANCE);
            if (fire_resistance >= 1) {
                e.setCanceled(true);
            }
        }
    }

    public static void fireDamage(LivingHurtEvent e) {
        DamageSource source = e.getSource();
        if (source.is(DamageTypeTags.IS_FIRE)) {
            double fire_resistance = e.getEntity().getAttributeValue(ModAttributes.FIRE_RESISTANCE);
            e.setAmount((float) (e.getAmount() * (1 - fire_resistance)));
        }
    }



    public static void breakBlock(BlockEvent.BreakEvent e) {
        Player player = e.getPlayer();
        if (player != null) {
            double experience_boost = player.getAttributeValue(ModAttributes.EXPERIENCE_BOOST);
            e.setExpToDrop((int) (e.getExpToDrop() * experience_boost));
        }
    }

    public static void livingXp(LivingExperienceDropEvent e) {
        Player player = e.getAttackingPlayer();
        if (player != null) {
            double experience_boost = player.getAttributeValue(ModAttributes.EXPERIENCE_BOOST);
            e.setDroppedExperience((int) (e.getDroppedExperience() * experience_boost));
        }
    }

    public static void livinghurt(LivingDamageEvent e) {
        DamageSource source = e.getSource();
        LivingEntity living = e.getEntity();
        Entity attacker = source.getDirectEntity();
        if (attacker instanceof LivingEntity) {
            double quartz = living.getAttributeValue(ModAttributes.THORNS);
            if (quartz > 0)
                attacker.hurt(living.damageSources().thorns(living), (float) quartz);
        }
    }

    @Nullable
    public static ArmorTrim getTrim(@Nullable Level level, ItemStack stack) {
        return level == null ? null : ArmorTrim.getTrim(level.registryAccess(),stack).orElse(null);
    }

    @Nullable
    public static TrimMaterial getTrimMaterial(@Nullable Level level, ItemStack stack) {
        ArmorTrim armorTrim = getTrim(level, stack);
        return armorTrim == null ? null : armorTrim.material().get();
    }

    @Nullable
    public static Item getTrimItem(@Nullable Level level, ItemStack stack) {
        TrimMaterial trimMaterial = getTrimMaterial(level,stack);
        return trimMaterial == null ? null : trimMaterial.ingredient().get();
    }
}
