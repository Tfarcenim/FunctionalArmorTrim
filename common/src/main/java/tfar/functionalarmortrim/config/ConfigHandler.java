package tfar.functionalarmortrim.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.functionalarmortrim.FunctionalArmorTrim;
import tfar.functionalarmortrim.init.ModAttributes;
import tfar.functionalarmortrim.network.client.S2CConfigPacket;
import tfar.functionalarmortrim.platform.Services;

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
            addMod(ironModifiers, Attributes.ARMOR, AttributeModifier.Operation.ADD_VALUE, 2);
            addTrim(root, Items.IRON_INGOT, ironModifiers);
        }
        {
            JsonArray diamondModifiers = new JsonArray();
            addMod(diamondModifiers, Attributes.ARMOR, AttributeModifier.Operation.ADD_VALUE, 2);
            addMod(diamondModifiers, Attributes.ARMOR_TOUGHNESS, AttributeModifier.Operation.ADD_VALUE, 2);
            addTrim(root, Items.DIAMOND, diamondModifiers);
        }
        {
            JsonArray netheriteModifiers = new JsonArray();
            addMod(netheriteModifiers, Attributes.ARMOR, AttributeModifier.Operation.ADD_VALUE, 2);
            addMod(netheriteModifiers, Attributes.ARMOR_TOUGHNESS, AttributeModifier.Operation.ADD_VALUE, 2);
            addMod(netheriteModifiers, ModAttributes.FIRE_RESISTANCE, AttributeModifier.Operation.ADD_VALUE, .25);
            addTrim(root, Items.NETHERITE_INGOT, netheriteModifiers);
        }
        {
            JsonArray redstoneModifiers = new JsonArray();
            addMod(redstoneModifiers, Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, .1);
            addMod(redstoneModifiers, Attributes.STEP_HEIGHT, AttributeModifier.Operation.ADD_VALUE, 1);
            addTrim(root, Items.REDSTONE, redstoneModifiers);
        }
        {
            JsonArray copperModifiers = new JsonArray();
            addMod(copperModifiers, Services.PLATFORM.getSwimSpeed(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, .1);
            addTrim(root, Items.COPPER_INGOT, copperModifiers);
        }
        {
            JsonArray amethystModifiers = new JsonArray();
            addMod(amethystModifiers, ModAttributes.NIGHT_VISION, AttributeModifier.Operation.ADD_VALUE, .25);
            addTrim(root, Items.AMETHYST_SHARD, amethystModifiers);
        }
        {
            JsonArray lapisModifiers = new JsonArray();
            addMod(lapisModifiers, ModAttributes.EXPERIENCE_BOOST, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, .1);
            addTrim(root, Items.LAPIS_LAZULI, lapisModifiers);
        }
        {
            JsonArray quartzModifiers = new JsonArray();
            addMod(quartzModifiers, ModAttributes.THORNS, AttributeModifier.Operation.ADD_VALUE, 1);
            addTrim(root, Items.QUARTZ, quartzModifiers);
        }
        {
            JsonArray emeraldModifiers = new JsonArray();
            addMod(emeraldModifiers, ModAttributes.GOSSIP_BOOST, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, .1);
            addTrim(root, Items.EMERALD, emeraldModifiers);
        }

        return root;
    }

    static void addTrim(JsonObject root,Item item,JsonArray mods) {
        root.add(getName(item),mods);
    }

    static void addMod(JsonArray array, Holder<Attribute> attribute, AttributeModifier.Operation operation, double amount) {
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

    public static final Map<Item,Map<Holder<Attribute>,Map<EquipmentSlot,AttributeModifier>>> MAP = new HashMap<>();

    public static void load(JsonObject jsonObject) {
        MAP.clear();
        for (Map.Entry<String,JsonElement> entry : jsonObject.asMap().entrySet()) {
            String key = entry.getKey();
            ResourceLocation location = ResourceLocation.parse(key);
           if (!BuiltInRegistries.ITEM.containsKey(location)) {
               LOGGER.info("Skipping unregistered item {}",key);
               continue;
            }
           Item item = BuiltInRegistries.ITEM.get(location);
           JsonArray mods = entry.getValue().getAsJsonArray();
           Map<Holder<Attribute>,Map<EquipmentSlot,AttributeModifier>> attributeMapMap = new HashMap<>();
           for (JsonElement element : mods) {
               JsonObject obj = element.getAsJsonObject();
               ResourceLocation attrLocation = ResourceLocation.parse(obj.get("attribute").getAsString());
               Optional<Holder.Reference<Attribute>> attribute = BuiltInRegistries.ATTRIBUTE.getHolder(attrLocation);
               if (attribute.isEmpty()) {
                   LOGGER.info("Skipping unregistered attribute {}",attrLocation);
                   continue;
               }
               AttributeModifier.Operation operation = AttributeModifier.Operation.valueOf(obj.get("operation").getAsString().toUpperCase(Locale.ROOT));
               double amount = obj.get("amount").getAsDouble();
               Map<EquipmentSlot,AttributeModifier> attributeModifierMap = new HashMap<>();
               for (EquipmentSlot slot : EquipmentSlot.values()) {
                   if (slot.isArmor()) {
                       ResourceLocation id = FunctionalArmorTrim.trim_modifiers[slot.getIndex()];
                       AttributeModifier modifier = new AttributeModifier(id,amount,operation);
                       attributeModifierMap.put(slot,modifier);
                   }
               }
               attributeMapMap.put(attribute.get(),attributeModifierMap);
           }
           MAP.put(item,attributeMapMap);
        }
        MinecraftServer server = Services.PLATFORM.getStaticServer();
        if (server != null) server.getPlayerList().getPlayers().forEach(player -> Services.PLATFORM.sendToClient(S2CConfigPacket.create(MAP),player));
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
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(s));
    }

    public static String getName(Holder<Attribute> attribute) {
        return attribute.getRegisteredName();
    }

    public static Holder<Attribute> getAttribute(String s) {
        return BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse(s)).orElseThrow();
    }

}
