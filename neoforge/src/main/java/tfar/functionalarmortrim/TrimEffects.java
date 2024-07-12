package tfar.functionalarmortrim;

import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import tfar.functionalarmortrim.client.ModClient;
import tfar.functionalarmortrim.config.ConfigHandler;
import tfar.functionalarmortrim.init.ModAttributes;

import java.util.Map;

public class TrimEffects {

    public static void attributes(ItemAttributeModifierEvent e) {
        ItemStack stack = e.getItemStack();
        Item item = stack.getItem();
        if (item instanceof ArmorItem armorItem) {
            EquipmentSlot slot = armorItem.getEquipmentSlot();
            Level level = getWorld();
            Item trim = FunctionalArmorTrim.getTrimItem(stack);
            var map = ConfigHandler.MAP;

            if (level.isClientSide) {
                map = ModClient.MAP;
            }

            map.getOrDefault(trim, Map.of()).forEach((attribute, attributeModifiers) -> e.addModifier(attribute, attributeModifiers.get(slot),EquipmentSlotGroup.bySlot(slot)));
        }
    }

    //this is needed because the client thread calls getAttributes, but a direct call to mc.world would crash servers
    public static Level getWorld() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return ModClient.getClientWorld();
        } else {
            return server.getLevel(Level.OVERWORLD);
        }
    }

    public static void fireResist(EntityInvulnerabilityCheckEvent e) {
        DamageSource source = e.getSource();
        Entity entity = e.getEntity();
        if (FunctionalArmorTrim.entityInvulnerabilityCheck(entity,source)) {
            e.setInvulnerable(true);
        }
    }

    public static void fireDamage(LivingIncomingDamageEvent e) {
        DamageSource source = e.getSource();
        LivingEntity entity = e.getEntity();
        e.setAmount((float) (e.getAmount() * FunctionalArmorTrim.livingIncomingDamageEvent(entity,source)));
    }



    public static void breakBlock(BlockDropsEvent e) {
       Entity entity = e.getBreaker();
       e.setDroppedExperience((int) (e.getDroppedExperience() * FunctionalArmorTrim.blockDrops(entity)));
    }

    public static void livingXp(LivingExperienceDropEvent e) {
        Player player = e.getAttackingPlayer();
        if (player != null) {
            double experience_boost = player.getAttributeValue(ModAttributes.EXPERIENCE_BOOST);
            e.setDroppedExperience((int) (e.getDroppedExperience() * experience_boost));
        }
    }

    public static void livinghurt(LivingDamageEvent.Post e) {
        DamageSource source = e.getSource();
        LivingEntity living = e.getEntity();
        FunctionalArmorTrim.livingDamagePost(living,source);
    }
}
