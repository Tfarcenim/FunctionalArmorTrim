package tfar.functionalarmortrim.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.functionalarmortrim.TrimEffects;
import tfar.functionalarmortrim.init.ModAttributes;
import tfar.functionalarmortrim.network.PacketHandler;
import tfar.functionalarmortrim.network.client.S2CConfigPacket;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.*;

public class ConfigHandler {

    static final File file = new File("config/functionalarmortrim.json");


    static JsonObject createDefault() {
        JsonObject root = new JsonObject();
        {
            JsonArray ironModifiers = new JsonArray();
            addMod(ironModifiers, Attributes.ARMOR, AttributeModifier.Operation.ADDITION, 2);
            addTrim(root, Items.IRON_INGOT, ironModifiers);
        }
        {
            JsonArray diamondModifiers = new JsonArray();
            addMod(diamondModifiers, Attributes.ARMOR, AttributeModifier.Operation.ADDITION, 2);
            addMod(diamondModifiers, Attributes.ARMOR_TOUGHNESS, AttributeModifier.Operation.ADDITION, 2);
            addTrim(root, Items.DIAMOND, diamondModifiers);
        }
        {
            JsonArray netheriteModifiers = new JsonArray();
            addMod(netheriteModifiers, Attributes.ARMOR, AttributeModifier.Operation.ADDITION, 2);
            addMod(netheriteModifiers, Attributes.ARMOR_TOUGHNESS, AttributeModifier.Operation.ADDITION, 2);
            addMod(netheriteModifiers, ModAttributes.FIRE_RESISTANCE, AttributeModifier.Operation.ADDITION, .25);
            addTrim(root, Items.NETHERITE_INGOT, netheriteModifiers);
        }
        {
            JsonArray redstoneModifiers = new JsonArray();
            addMod(redstoneModifiers, Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL, .1);
            addMod(redstoneModifiers, ForgeMod.STEP_HEIGHT_ADDITION.get(), AttributeModifier.Operation.ADDITION, 1);
            addTrim(root, Items.REDSTONE, redstoneModifiers);
        }
        {
            JsonArray copperModifiers = new JsonArray();
            addMod(copperModifiers, ForgeMod.SWIM_SPEED.get(), AttributeModifier.Operation.MULTIPLY_TOTAL, .1);
            addTrim(root, Items.COPPER_INGOT, copperModifiers);
        }
        {
            JsonArray amethystModifiers = new JsonArray();
            addMod(amethystModifiers, ModAttributes.NIGHT_VISION, AttributeModifier.Operation.ADDITION, .25);
            addTrim(root, Items.AMETHYST_SHARD, amethystModifiers);
        }
        {
            JsonArray lapisModifiers = new JsonArray();
            addMod(lapisModifiers, ModAttributes.EXPERIENCE_BOOST, AttributeModifier.Operation.MULTIPLY_TOTAL, .1);
            addTrim(root, Items.LAPIS_LAZULI, lapisModifiers);
        }
        {
            JsonArray quartzModifiers = new JsonArray();
            addMod(quartzModifiers, ModAttributes.THORNS, AttributeModifier.Operation.ADDITION, 1);
            addTrim(root, Items.QUARTZ, quartzModifiers);
        }
        {
            JsonArray emeraldModifiers = new JsonArray();
            addMod(emeraldModifiers, ModAttributes.GOSSIP_BOOST, AttributeModifier.Operation.MULTIPLY_TOTAL, .1);
            addTrim(root, Items.EMERALD, emeraldModifiers);
        }

        return root;
    }

    static void addTrim(JsonObject root,Item item,JsonArray mods) {
        root.add(getName(item),mods);
    }

    static void addMod(JsonArray array,Attribute attribute, AttributeModifier.Operation operation,double amount) {
        JsonObject object = new JsonObject();
        object.addProperty("attribute",getName(attribute));
        object.addProperty("operation", operation.name().toLowerCase(Locale.ROOT));
        object.addProperty("amount",amount);
        array.add(object);
    }

    private static final Logger LOGGER = LogManager.getLogger();

    public static JsonObject read(Gson gson) {
        writeIfEmpty();

        Reader reader = null;
        try {
            reader = new FileReader(file);
            JsonReader jsonReader = new JsonReader(reader);
            // Type listType = new TypeToken<ArrayList<BTSIslandConfig>>(){}.getType();
            LOGGER.info("Loading existing config");
            return gson.fromJson(jsonReader, JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    public static final Map<Item,Map<Attribute,Map<EquipmentSlot,AttributeModifier>>> MAP = new HashMap<>();

    public static void load(JsonObject jsonObject) {
        MAP.clear();
        for (Map.Entry<String,JsonElement> entry : jsonObject.asMap().entrySet()) {
            String key = entry.getKey();
            ResourceLocation location = new ResourceLocation(key);
           if (!BuiltInRegistries.ITEM.containsKey(location)) {
               LOGGER.info("Skipping unregistered item {}",key);
               continue;
            }
           Item item = BuiltInRegistries.ITEM.get(location);
           JsonArray mods = entry.getValue().getAsJsonArray();
           Map<Attribute,Map<EquipmentSlot,AttributeModifier>> attributeMapMap = new HashMap<>();
           for (JsonElement element : mods) {
               JsonObject obj = element.getAsJsonObject();
               Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation(obj.get("attribute").getAsString()));
               AttributeModifier.Operation operation = AttributeModifier.Operation.valueOf(obj.get("operation").getAsString().toUpperCase(Locale.ROOT));
               double amount = obj.get("amount").getAsDouble();
               Map<EquipmentSlot,AttributeModifier> attributeModifierMap = new HashMap<>();
               for (EquipmentSlot slot : EquipmentSlot.values()) {
                   if (slot.isArmor()) {
                       UUID uuid = TrimEffects.trim_slot_uuids[slot.getIndex()];
                       AttributeModifier modifier = new AttributeModifier(uuid,"Armor Trim Boost",amount,operation);
                       attributeModifierMap.put(slot,modifier);
                   }
               }
               attributeMapMap.put(attribute,attributeModifierMap);
           }
           MAP.put(item,attributeMapMap);
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) server.getPlayerList().getPlayers().forEach(player -> PacketHandler.sendToClient(new S2CConfigPacket(MAP),player));
    }

    public static void writeIfEmpty() {
        if (!file.exists()) {
            write(file);
        }
    }

    public static void write(File file) {
        Gson gson = new Gson();
        JsonWriter writer = null;
        try {
            writer = gson.newJsonWriter(new FileWriter(file));
            writer.setIndent("    ");
            gson.toJson(createDefault(), writer);
        } catch (Exception e) {
            LOGGER.error("Couldn't save config");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public static String getName(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }

    public static Item getItem(String s) {
        return BuiltInRegistries.ITEM.get(new ResourceLocation(s));
    }

    public static String getName(Attribute attribute) {
        return BuiltInRegistries.ATTRIBUTE.getKey(attribute).toString();
    }

    public static Attribute getAttribute(String s) {
        return BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation(s));
    }

}
