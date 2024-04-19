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

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;

import java.util.List;

public class CombatCalculator {

    /**
     * Retrieve default value of an Attribute on a NMS Item
     * @param item The NMS item
     * @param slot The slot
     * @param attribute The attribute
     * @return The default value
     */
    public static double defaultValue(Item item, EquipmentSlot slot, Attribute attribute) {
        List<AttributeModifier> modifiers = item.getDefaultAttributeModifiers(slot).get(attribute).stream().toList();
        return modifiers.get(0).getAmount();
    }

    /**
     * Calculate item attack damage with NMS
     * @param item The NMS ItemStack
     * @return The item attack damage
     */
    public static double getAttackDamage(net.minecraft.world.item.ItemStack item, CraftPlayer craftPlayer) {
        double attackDamage = 1.0;
        for(net.minecraft.world.entity.ai.attributes.AttributeModifier modifier : item.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE)) {
            attackDamage += modifier.getAmount();
        }
        attackDamage += EnchantmentHelper.getDamageBonus(item, craftPlayer.getHandle().getMobType());
        attackDamage = Math.round(attackDamage * 10.0) / 10.0;
        return attackDamage;
    }

    /**
     * Calculate item attack speed with NMS
     * @param item The NMS ItemStack
     * @return The item attack speed
     */
    public static double getAttackSpeed(net.minecraft.world.item.ItemStack item) {
        double attackSpeed = 4.0;
        for(net.minecraft.world.entity.ai.attributes.AttributeModifier modifier : item.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_SPEED)) {
            attackSpeed += modifier.getAmount();
        }
        attackSpeed = Math.round(attackSpeed * 10.0) / 10.0;
        return attackSpeed;
    }

    /**
     * Calculate item armor with NMS
     * @param item The NMS ItemStack
     * @return The item armor points
     */
    public static double getArmor(net.minecraft.world.item.ItemStack item) {
        double armor = 0.0;
        ArmorItem itemArmor = ((ArmorItem) item.getItem());
        for(net.minecraft.world.entity.ai.attributes.AttributeModifier modifier : item.getAttributeModifiers(itemArmor.getEquipmentSlot()).get(Attributes.ARMOR)) {
            armor += modifier.getAmount();
        }
        armor = Math.round(armor * 10.0) / 10.0;
        return armor;
    }

    /**
     * Calculate item armor toughness with NMS
     * @param item The NMS ItemStack
     * @return The item armor toughness
     */
    public static double getArmorToughness(net.minecraft.world.item.ItemStack item) {
        double armorToughness = 0.0;
        ArmorItem itemArmor = ((ArmorItem) item.getItem());
        for(net.minecraft.world.entity.ai.attributes.AttributeModifier modifier : item.getAttributeModifiers(itemArmor.getEquipmentSlot()).get(Attributes.ARMOR_TOUGHNESS)) {
            armorToughness += modifier.getAmount();
        }
        armorToughness = Math.round(armorToughness * 10.0) / 10.0;
        return armorToughness;
    }

    /**
     * Calculate item armor knockback resistance with NMS
     * @param item The NMS ItemStack
     * @return The item armor knocback resistance
     */
    public static double getKnockbackResistance(net.minecraft.world.item.ItemStack item) {
        double knockbackResistance = 0.0;
        ArmorItem itemArmor = ((ArmorItem) item.getItem());
        for(net.minecraft.world.entity.ai.attributes.AttributeModifier modifier : item.getAttributeModifiers(itemArmor.getEquipmentSlot()).get(Attributes.KNOCKBACK_RESISTANCE)) {
            knockbackResistance += modifier.getAmount();
        }
        knockbackResistance = Math.round(knockbackResistance * 10.0);
        return knockbackResistance;
    }

}