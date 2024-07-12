package tfar.functionalarmortrim;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import tfar.functionalarmortrim.client.ModClient;
import tfar.functionalarmortrim.config.ConfigHandler;
import tfar.functionalarmortrim.config.TrimEffectReloadListener;
import tfar.functionalarmortrim.datagen.ModDatagen;
import tfar.functionalarmortrim.network.PacketHandlerNeoForge;
import tfar.functionalarmortrim.network.client.S2CConfigPacket;
import tfar.functionalarmortrim.platform.Services;

import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FunctionalArmorTrim.MOD_ID)
public class FunctionalArmorTrimNeoForge {

    public FunctionalArmorTrimNeoForge(IEventBus bus) {
        bus.addListener(this::register);
        bus.addListener(ModDatagen::start);
        bus.addListener(this::setup);
        bus.addListener(PacketHandlerNeoForge::register);
        NeoForge.EVENT_BUS.addListener(this::attributes);
        NeoForge.EVENT_BUS.addListener(this::breakBlock);
        NeoForge.EVENT_BUS.addListener(this::livingXp);
        NeoForge.EVENT_BUS.addListener(this::livinghurt);
        NeoForge.EVENT_BUS.addListener(this::fireResist);
        NeoForge.EVENT_BUS.addListener(this::fireDamage);
        NeoForge.EVENT_BUS.addListener(this::reload);
        NeoForge.EVENT_BUS.addListener(this::join);
    }

    private void setup(FMLCommonSetupEvent event) {
        ConfigHandler.writeIfEmpty();
    }

    private void reload(AddReloadListenerEvent event) {
        event.addListener(new TrimEffectReloadListener());
    }

    private void join(PlayerEvent.PlayerLoggedInEvent event) {
        Services.PLATFORM.sendToClient(S2CConfigPacket.create(ConfigHandler.MAP), (ServerPlayer) event.getEntity());
    }


    private void register(RegisterEvent event) {
    }

    public void attributes(ItemAttributeModifierEvent e) {
        ItemStack stack = e.getItemStack();
        Item item = stack.getItem();
        if (item instanceof ArmorItem armorItem) {
            EquipmentSlot slot = armorItem.getEquipmentSlot();
            Item trim = FunctionalArmorTrim.getTrimItem(stack);
            var map = ConfigHandler.MAP;

            if (Services.PLATFORM.getStaticServer() == null) {
                map = ModClient.MAP;
            }

            map.getOrDefault(trim, Map.of()).forEach((attribute, attributeModifiers) -> e.addModifier(attribute, attributeModifiers.get(slot), EquipmentSlotGroup.bySlot(slot)));
        }
    }

    public void fireResist(EntityInvulnerabilityCheckEvent e) {
        DamageSource source = e.getSource();
        Entity entity = e.getEntity();
        if (FunctionalArmorTrim.entityInvulnerabilityCheck(entity,source)) {
            e.setInvulnerable(true);
        }
    }

    public void fireDamage(LivingIncomingDamageEvent e) {
        DamageSource source = e.getSource();
        LivingEntity entity = e.getEntity();
        e.setAmount((float) (e.getAmount() * FunctionalArmorTrim.livingIncomingDamageEvent(entity,source)));
    }

    public void breakBlock(BlockDropsEvent e) {
        Entity entity = e.getBreaker();
        e.setDroppedExperience((int) (e.getDroppedExperience() * FunctionalArmorTrim.blockDrops(entity)));
    }

    public void livingXp(LivingExperienceDropEvent e) {
        Player player = e.getAttackingPlayer();
        e.setDroppedExperience((int) (e.getDroppedExperience() * FunctionalArmorTrim.livingExperienceDrops(player)));
    }

    public void livinghurt(LivingDamageEvent.Post e) {
        DamageSource source = e.getSource();
        LivingEntity living = e.getEntity();
        FunctionalArmorTrim.livingDamagePost(living,source);
    }
}
