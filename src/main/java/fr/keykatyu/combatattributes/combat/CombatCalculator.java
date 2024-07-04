/*
 * CombatAttributes plugin to remove custom combat items attributes
 * Copyright (C) 2024 KeyKatyu (Antoine D.)
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package fr.keykatyu.combatattributes.combat;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer;

import java.util.ArrayList;
import java.util.List;

public class CombatCalculator {

    /**
     * Retrieve default value of an Attribute on a NMS Item
     * @param item The NMS item
     * @param slot The slot
     * @param attribute The attribute
     * @return The default value
     */
    public static double defaultValue(CombatItem.Type type, Item item, EquipmentSlot slot, Attribute attribute) {
        List<ItemAttributeModifiers.Entry> defaultEntries = new ArrayList<>();
        switch (type) {
            case WEAPON -> defaultEntries = item.components().get(DataComponents.ATTRIBUTE_MODIFIERS).modifiers();
            case ARMOR_PIECE -> defaultEntries = ((ArmorItem) item).getDefaultAttributeModifiers().modifiers();
        }
        for(ItemAttributeModifiers.Entry entry : defaultEntries) {
            if(entry.slot() == EquipmentSlotGroup.bySlot(slot) && entry.attribute().value() == attribute) {
                return entry.modifier().amount();
            }
        }
        return 0.0;
    }

    /**
     * Calculate item attack damage with NMS
     * @param item The NMS ItemStack
     * @return The item attack damage
     */
    public static double getAttackDamage(ItemStack item, CraftPlayer craftPlayer) {
        double attackDamage = 1.0;
        for(ItemAttributeModifiers.Entry entry : item.getItem().components().get(DataComponents.ATTRIBUTE_MODIFIERS).modifiers()) {
            if(entry.slot() == EquipmentSlotGroup.MAINHAND && entry.attribute().value() == Attributes.ATTACK_DAMAGE.value()) {
                attackDamage += entry.modifier().amount();
            }
        }
        attackDamage += EnchantmentHelper.getDamageBonus(item, craftPlayer.getHandle().getType());
        attackDamage = Math.round(attackDamage * 10.0) / 10.0;
        return attackDamage;
    }

    /**
     * Calculate item attack speed with NMS
     * @param item The NMS ItemStack
     * @return The item attack speed
     */
    public static double getAttackSpeed(ItemStack item) {
        double attackSpeed = 4.0;
        for(ItemAttributeModifiers.Entry entry : item.get(DataComponents.ATTRIBUTE_MODIFIERS).modifiers()) {
            if(entry.slot() == EquipmentSlotGroup.MAINHAND && entry.attribute().value() == Attributes.ATTACK_SPEED.value()) {
                attackSpeed += entry.modifier().amount();
            }
        }
        attackSpeed = Math.round(attackSpeed * 10.0) / 10.0;
        return attackSpeed;
    }

    /**
     * Calculate an armor attribute
     * @param item The armor piece item
     * @param attribute The attribute
     * @return The calculated value
     */
    public static double getArmorAttribute(ItemStack item, Holder<Attribute> attribute) {
        ArmorItem itemArmor = ((ArmorItem) item.getItem());
        double value = 0.0;
        for(ItemAttributeModifiers.Entry entry : item.get(DataComponents.ATTRIBUTE_MODIFIERS).modifiers()) {
            if(entry.slot() == EquipmentSlotGroup.bySlot(itemArmor.getEquipmentSlot()) && entry.attribute().value() == attribute.value()) {
                value += entry.modifier().amount();
            }
        }
        value = Math.round(value * 10.0) / 10.0;
        return value;
    }

}