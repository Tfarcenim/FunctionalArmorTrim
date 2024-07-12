/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package tfar.functionalarmortrim;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.*;
import java.util.function.Predicate;
/**
 * Advanced version of {@link ItemAttributeModifiers.Builder} which supports removal and better sanity-checking.
 * <p>
 * The original builder only supports additions and does not guarantee that no duplicate modifiers exist for a given id.
 */
public class ItemAttributeModifiersBuilderFabric {
        private List<ItemAttributeModifiers.Entry> entries;
        private Map<Key, ItemAttributeModifiers.Entry> entriesByKey;

        ItemAttributeModifiersBuilderFabric(ItemAttributeModifiers defaultModifiers) {
            this.entries = new LinkedList<>();
            this.entriesByKey = new HashMap<>(defaultModifiers.modifiers().size());

            for (ItemAttributeModifiers.Entry entry : defaultModifiers.modifiers()) {
                entries.add(entry);
                entriesByKey.put(new Key(entry.attribute(), entry.modifier().id()), entry);
            }
        }

        /**
         * {@return an unmodifiable view of the underlying entry list}
         */
        List<ItemAttributeModifiers.Entry> getEntryView() {
            return Collections.unmodifiableList(this.entries);
        }

        /**
         * Attempts to add a new modifier, refusing if one is already present with the same id.
         *
         * @return true if the modifier was added
         */
        boolean addModifier(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slot) {
            Key key = new Key(attribute, modifier.id());
            if (entriesByKey.containsKey(key)) {
                return false;
            }

            ItemAttributeModifiers.Entry entry = new ItemAttributeModifiers.Entry(attribute, modifier, slot);
            entries.add(entry);
            entriesByKey.put(key, entry);
            return true;
        }

        /**
         * Removes a modifier for the target attribute with the given id.
         *
         * @return true if a modifier was removed
         */
        boolean removeModifier(Holder<Attribute> attribute, ResourceLocation id) {
            ItemAttributeModifiers.Entry entry = entriesByKey.remove(new Key(attribute, id));

            if (entry != null) {
                entries.remove(entry);
                return true;
            }

            return false;
        }

        /**
         * Removes modifiers based on a condition.
         *
         * @return true if any modifiers were removed
         */
        boolean removeIf(Predicate<ItemAttributeModifiers.Entry> condition) {
            this.entries.removeIf(condition);
            return this.entriesByKey.values().removeIf(condition);
        }

        void clear() {
            this.entries.clear();
            this.entriesByKey.clear();
        }

        public ItemAttributeModifiers build(boolean showInTooltip) {
            return new ItemAttributeModifiers(ImmutableList.copyOf(this.entries), showInTooltip);
        }

        /**
         * Internal key class. Attribute modifiers are unique by id for each Attribute.
         */
        private static record Key(Holder<Attribute> attr, ResourceLocation id) {

        }
    }
