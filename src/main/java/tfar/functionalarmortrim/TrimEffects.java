package tfar.functionalarmortrim;

import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class TrimEffects {

    public static final UUID[] trim_slot_uuids = new UUID[]{
            UUID.fromString("81a9ed67-25f2-40f5-9a14-5607bd76b506"),
            UUID.fromString("71dcc566-b25a-4f1a-b564-c9a8aa9e784f"),
            UUID.fromString("0a2787e9-a297-4ac9-9935-bed6f145e00c"),
            UUID.fromString("eb4dbe61-f520-44fe-81ee-7754188eb320")};

    public static final AttributeModifier[] trim_modifiers_add_2 = createAttributeModGroup(2, AttributeModifier.Operation.ADDITION);
    public static final AttributeModifier[] trim_modifiers_add_1 = createAttributeModGroup(1, AttributeModifier.Operation.ADDITION);

    public static final AttributeModifier[] trim_modifiers_add_5_percent = createAttributeModGroup(0.05, AttributeModifier.Operation.MULTIPLY_TOTAL);

    public static final AttributeModifier[] trim_modifiers_add_10_percent = createAttributeModGroup(0.10, AttributeModifier.Operation.MULTIPLY_TOTAL);

    public static AttributeModifier[] createAttributeModGroup(double amount, AttributeModifier.Operation op) {
        return new AttributeModifier[]{
                new AttributeModifier(trim_slot_uuids[EquipmentSlot.HEAD.getIndex()], "Armor Trim Boost", amount, op),
                new AttributeModifier(trim_slot_uuids[EquipmentSlot.CHEST.getIndex()], "Armor Trim Boost", amount, op),
                new AttributeModifier(trim_slot_uuids[EquipmentSlot.LEGS.getIndex()], "Armor Trim Boost", amount, op),
                new AttributeModifier(trim_slot_uuids[EquipmentSlot.FEET.getIndex()], "Armor Trim Boost", amount, op),
        };
    }

    static final Map<Item,Map<Attribute,Function<EquipmentSlot,AttributeModifier>>> TRIM_ATTRIBUTE_MAP = new HashMap<>();

    static {
        Function<EquipmentSlot,AttributeModifier> plus2modifier = slot -> trim_modifiers_add_2[slot.getIndex()];
        Function<EquipmentSlot,AttributeModifier> plus1modifier = slot -> trim_modifiers_add_1[slot.getIndex()];
        Function<EquipmentSlot,AttributeModifier> plus10percentModifier = slot -> trim_modifiers_add_10_percent[slot.getIndex()];
        TRIM_ATTRIBUTE_MAP.put(Items.IRON_INGOT, Map.of(Attributes.ARMOR,plus2modifier));
        TRIM_ATTRIBUTE_MAP.put(Items.DIAMOND,Map.of(Attributes.ARMOR,plus2modifier,Attributes.ARMOR_TOUGHNESS,plus2modifier));
        TRIM_ATTRIBUTE_MAP.put(Items.REDSTONE,Map.of(Attributes.MOVEMENT_SPEED,plus10percentModifier,ForgeMod.STEP_HEIGHT_ADDITION.get(), plus1modifier));
        TRIM_ATTRIBUTE_MAP.put(Items.COPPER_INGOT,Map.of(ForgeMod.SWIM_SPEED.get(), plus10percentModifier));
    }

    public static void attributes(ItemAttributeModifierEvent e) {
        ItemStack stack = e.getItemStack();
        Item item = stack.getItem();
        EquipmentSlot slot = e.getSlotType();
        if (item instanceof ArmorItem armorItem && slot == armorItem.getEquipmentSlot()) {
            Item trim = getTrimItem(getWorld(), stack);
            TRIM_ATTRIBUTE_MAP.getOrDefault(trim,Map.of()).forEach((attribute, equipmentSlotAttributeModifierFunction) ->
                    e.addModifier(attribute,equipmentSlotAttributeModifierFunction.apply(slot)));
        }
    }

    public static void fireResist(LivingAttackEvent e) {
        DamageSource source = e.getSource();
        if (source.is(DamageTypeTags.IS_FIRE)) {
            int netherite = countTrim(e.getEntity(), Items.NETHERITE_INGOT);
            if (netherite > 3) {
                e.setCanceled(true);
            }
        }
    }

    public static void fireDamage(LivingHurtEvent e) {
        DamageSource source = e.getSource();
        if (source.is(DamageTypeTags.IS_FIRE)) {
            int netherite = countTrim(e.getEntity(), Items.NETHERITE_INGOT);
            e.setAmount(e.getAmount() * (1 - netherite / 4f));
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

    public static void breakBlock(BlockEvent.BreakEvent e) {
        Player player = e.getPlayer();
        int lapis = countTrim(player,Items.LAPIS_LAZULI);
        e.setExpToDrop((int) (e.getExpToDrop() * (1 + .1 * lapis)));
    }

    public static void livingXp(LivingExperienceDropEvent e) {
        Player player = e.getAttackingPlayer();
        if (player != null) {
            int lapis = countTrim(player, Items.LAPIS_LAZULI);
            e.setDroppedExperience((int) (e.getDroppedExperience() * (1 + .1 * lapis)));
        }
    }

    public static void livinghurt(LivingDamageEvent e) {
        DamageSource source = e.getSource();
        LivingEntity living = e.getEntity();
        Entity attacker = source.getDirectEntity();
        if (attacker instanceof LivingEntity) {
            int quartz = countTrim(living, Items.QUARTZ);
            if (quartz > 0)
                attacker.hurt(living.damageSources().thorns(living), quartz);
        }
    }

    public static int countTrim(LivingEntity pLivingEntity,Item trim) {
        int i = 0;
        for(ItemStack itemstack : pLivingEntity.getArmorSlots())
            if (getTrimItem(pLivingEntity.level(), itemstack) == trim) i++;

        return i;
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
