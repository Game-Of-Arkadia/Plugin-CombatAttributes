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

import fr.keykatyu.combatattributes.Main;
import fr.keykatyu.combatattributes.util.ItemBuilder;
import fr.keykatyu.combatattributes.util.Util;
import fr.keykatyu.mctranslation.api.Language;
import fr.keykatyu.mctranslation.api.MCTranslator;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class CombatItem {

    private final ItemStack itemStack;
    private final Type type;
    private final ItemBuilder ib;
    private final Player owner;
    private final Language language;
    private static final DecimalFormat df = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.US));
    private final boolean refreshFixers;

    public CombatItem(ItemStack item, Player owner, boolean refreshFixers) {
        this.itemStack = item;
        this.refreshFixers = refreshFixers;
        type = Type.retrieveItemType(itemStack);
        ib = new ItemBuilder(item);
        this.owner = owner;
        language = Language.fromPlayer(owner);
    }

    public CombatItem(ItemStack item, Player owner, Language language, boolean refreshFixers) {
        this.itemStack = item;
        this.refreshFixers = refreshFixers;
        type = Type.retrieveItemType(itemStack);
        ib = new ItemBuilder(item);
        this.owner = owner;
        this.language = language;
    }

    /**
     * Update item stats in the lore
     */
    public ItemStack getUpdatedItem() {
        if(type == null) return itemStack;
        if(!itemStack.hasItemMeta()) return itemStack;
        List<String> lore = new ArrayList<>();
        if(itemStack.getItemMeta().hasLore()) lore = itemStack.getItemMeta().getLore();

        if(itemStack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "CombatAttributesLore"), PersistentDataType.INTEGER)) {
            int combatAttributesLines = itemStack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Main.getInstance(), "CombatAttributesLore"), PersistentDataType.INTEGER);
            if (combatAttributesLines > 0 && combatAttributesLines <= lore.size()) {
                lore.subList(lore.size() - combatAttributesLines, lore.size()).clear();
            }
            if(refreshFixers) {
                Item item = CraftItemStack.asNMSCopy(itemStack).getItem();
                switch (type) {
                    case WEAPON -> {
                        double attackDamageBonus = CombatCalculator.defaultValue(type, item, net.minecraft.world.entity.EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE.value());
                        double attackSpeedBonus = CombatCalculator.defaultValue(type, item, net.minecraft.world.entity.EquipmentSlot.MAINHAND, Attributes.ATTACK_SPEED.value());
                        ib.updateAttributeModifierValue(org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE, "fix_attackdamage", attackDamageBonus);
                        ib.updateAttributeModifierValue(org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED, "fix_attackspeed", attackSpeedBonus);
                    }
                    case ARMOR_PIECE -> {
                        ArmorItem itemArmor = (ArmorItem) item;
                        double armorBonus = CombatCalculator.defaultValue(type, itemArmor, itemArmor.getEquipmentSlot(), Attributes.ARMOR.value());
                        ib.updateAttributeModifierValue(org.bukkit.attribute.Attribute.GENERIC_ARMOR, "fix_armor", armorBonus);

                        Holder<ArmorMaterial> armorMaterial = itemArmor.getMaterial();
                        if(armorMaterial == ArmorMaterials.DIAMOND || armorMaterial == ArmorMaterials.NETHERITE) {
                            double armorToughnessBonus = CombatCalculator.defaultValue(type, itemArmor, itemArmor.getEquipmentSlot(), Attributes.ARMOR_TOUGHNESS.value());
                            ib.updateAttributeModifierValue(org.bukkit.attribute.Attribute.GENERIC_ARMOR_TOUGHNESS, "fix_armortoughness", armorToughnessBonus);
                            if(armorMaterial == ArmorMaterials.NETHERITE) {
                                double knockbackResistance = CombatCalculator.defaultValue(type, itemArmor, itemArmor.getEquipmentSlot(), Attributes.KNOCKBACK_RESISTANCE.value());
                                ib.updateAttributeModifierValue(org.bukkit.attribute.Attribute.GENERIC_KNOCKBACK_RESISTANCE, "fix_knockbackresistance", knockbackResistance);
                            }
                        }
                    }
                }
            }
        } else if(itemStack.getItemMeta().hasAttributeModifiers()) {
            Item item = CraftItemStack.asNMSCopy(itemStack).getItem();
            switch (type) {
                case WEAPON -> {
                    double attackDamageBonus = CombatCalculator.defaultValue(type, item, net.minecraft.world.entity.EquipmentSlot.MAINHAND, Attributes.ATTACK_DAMAGE.value());
                    double attackSpeedBonus = CombatCalculator.defaultValue(type, item, net.minecraft.world.entity.EquipmentSlot.MAINHAND, Attributes.ATTACK_SPEED.value());
                    ib.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE,
                            new AttributeModifier(UUID.randomUUID(), "fix_attackdamage", attackDamageBonus, AttributeModifier.Operation.ADD_NUMBER, itemStack.getType().getEquipmentSlot()));
                    ib.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED,
                            new AttributeModifier(UUID.randomUUID(), "fix_attackspeed", attackSpeedBonus, AttributeModifier.Operation.ADD_NUMBER, itemStack.getType().getEquipmentSlot()));
                }
                case ARMOR_PIECE -> {
                    ArmorItem itemArmor = (ArmorItem) item;
                    double armorBonus = CombatCalculator.defaultValue(type, itemArmor, itemArmor.getEquipmentSlot(), Attributes.ARMOR.value());
                    ib.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_ARMOR,
                            new AttributeModifier(UUID.randomUUID(), "fix_armor", armorBonus, AttributeModifier.Operation.ADD_NUMBER, itemStack.getType().getEquipmentSlot()));

                    Holder<ArmorMaterial> armorMaterial = itemArmor.getMaterial();
                    if(armorMaterial == ArmorMaterials.DIAMOND || armorMaterial == ArmorMaterials.NETHERITE) {
                        double armorToughnessBonus = CombatCalculator.defaultValue(type, itemArmor, itemArmor.getEquipmentSlot(), Attributes.ARMOR_TOUGHNESS.value());
                        ib.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_ARMOR_TOUGHNESS,
                                new AttributeModifier(UUID.randomUUID(), "fix_armortoughness", armorToughnessBonus, AttributeModifier.Operation.ADD_NUMBER, itemStack.getType().getEquipmentSlot()));
                        if(armorMaterial == ArmorMaterials.NETHERITE) {
                            double knockbackResistance = CombatCalculator.defaultValue(type, itemArmor, itemArmor.getEquipmentSlot(), Attributes.KNOCKBACK_RESISTANCE.value());
                            ib.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                                    new AttributeModifier(UUID.randomUUID(), "fix_knockbackresistance", knockbackResistance, AttributeModifier.Operation.ADD_NUMBER, itemStack.getType().getEquipmentSlot()));
                        }
                    }
                }
            }
        }

        List<String> combatAttributes = new ArrayList<>();
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(ib.toItemStack());
        combatAttributes.add("");
        switch (type) {
            case WEAPON -> {
                combatAttributes.add("§7" + MCTranslator.translate("item.modifiers.mainhand", language));
                combatAttributes.add(" §2" + df.format(CombatCalculator.getAttackDamage(nmsItem, (CraftPlayer) owner)) + " " + MCTranslator.translate("attribute.name.generic.attack_damage", language));
                combatAttributes.add(" §2" + df.format(CombatCalculator.getAttackSpeed(nmsItem)) + " " + MCTranslator.translate("attribute.name.generic.attack_speed", language));
            }
            case ARMOR_PIECE -> {
                ArmorItem itemArmor = (ArmorItem) nmsItem.getItem();
                switch (itemArmor.getEquipmentSlot()) {
                    case FEET -> combatAttributes.add("§7" + MCTranslator.translate("item.modifiers.feet", language));
                    case LEGS -> combatAttributes.add("§7" + MCTranslator.translate("item.modifiers.legs", language));
                    case CHEST -> combatAttributes.add("§7" + MCTranslator.translate("item.modifiers.chest", language));
                    case HEAD -> combatAttributes.add("§7" + MCTranslator.translate("item.modifiers.head", language));
                }
                combatAttributes.add("§9+" + df.format(CombatCalculator.getArmorAttribute(nmsItem, Attributes.ARMOR)) + " " + MCTranslator.translate("attribute.name.generic.armor", language));
                double armorToughness = CombatCalculator.getArmorAttribute(nmsItem, Attributes.ARMOR_TOUGHNESS);
                if(armorToughness != 0) combatAttributes.add("§9+" + df.format(armorToughness) + " " + MCTranslator.translate("attribute.name.generic.armor_toughness", language));
                if(!Util.hasNetheriteKBResistanceToBeRemoved()) {
                    double knockbackResistance = CombatCalculator.getArmorAttribute(nmsItem, Attributes.KNOCKBACK_RESISTANCE);
                    if(knockbackResistance != 0) combatAttributes.add("§9+" + df.format(knockbackResistance) + " " + MCTranslator.translate("attribute.name.generic.knockback_resistance", language));
                }
            }
        }

        HashMap<EquipmentSlot, List<Attribute>> attributes = getAttributes();
        if(attributes.containsKey(itemStack.getType().getEquipmentSlot())) attributes.get(itemStack.getType().getEquipmentSlot()).forEach(attribute -> combatAttributes.add(attribute.renderDisplay()));
        attributes.remove(itemStack.getType().getEquipmentSlot());

        for(Map.Entry<EquipmentSlot, List<Attribute>> map : attributes.entrySet()) {
            String equipmentSlotKey = map.getKey().name().toLowerCase().toString();
            if(map.getKey().equals(EquipmentSlot.OFF_HAND)) equipmentSlotKey = "offhand";
            combatAttributes.add("");
            combatAttributes.add("§7" + MCTranslator.translate("item.modifiers." + equipmentSlotKey, language));
            map.getValue().forEach(attribute -> combatAttributes.add(attribute.renderDisplay()));
        }

        lore.addAll(combatAttributes);
        return ib.setItemsFlags(ItemFlag.HIDE_ATTRIBUTES).setLore(lore)
                .addNBTKey(new NamespacedKey(Main.getInstance(), "CombatAttributesLore"), PersistentDataType.INTEGER, combatAttributes.size())
                .toItemStack();
    }

    /**
     * Get item attributes
     * @return The item attributes for each {@link EquipmentSlot}
     */
    private HashMap<EquipmentSlot, List<Attribute>> getAttributes() {
        HashMap<EquipmentSlot, List<Attribute>> attributes = new HashMap<>();
        if(!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasAttributeModifiers()) return attributes;

        for(Map.Entry<org.bukkit.attribute.Attribute, AttributeModifier> attribute : itemStack.getItemMeta().getAttributeModifiers().entries()) {
            if(attribute.getValue().getName().startsWith("fix_")) continue;
            if(attribute.getValue().getName().equals("remove_netheritekbresistance")) continue;
            if(type.equals(Type.ARMOR_PIECE)) {
                List<String> invisibleAttributes = List.of("generic.armor", "generic.armor_toughness", "generic.knockback_resistance");
                if (invisibleAttributes.contains(attribute.getKey().getKey().getKey())) continue;
            }

            String name = MCTranslator.translate("attribute.name." + attribute.getKey().getKey().getKey(), language);
            double value = attribute.getValue().getAmount();
            AttributeModifier.Operation operation = attribute.getValue().getOperation();

            List<Attribute> attributesList = attributes.computeIfAbsent(attribute.getValue().getSlot(), k -> new ArrayList<>());
            attributesList.add(new Attribute(name, value, operation));
            EquipmentSlot slot = attribute.getValue().getSlot();
            if(slot == null) slot = itemStack.getType().getEquipmentSlot();
            attributes.put(slot, attributesList);
        }
        return attributes;
    }

    public enum Type {
        WEAPON,
        ARMOR_PIECE;

        /**
         * Get the type from the ItemStack
         * @param itemStack The ItemStack
         * @return The type or null if not found
         */
        public static Type retrieveItemType(ItemStack itemStack) {
            Item item = CraftItemStack.asNMSCopy(itemStack).getItem();
            if(item instanceof TridentItem || item instanceof TieredItem) {
                return WEAPON;
            } else if(item instanceof ArmorItem) {
                return ARMOR_PIECE;
            } else {
                return null;
            }
        }
    }

    public static class Attribute {

        private final String name;
        private final double value;
        private final AttributeModifier.Operation operation;

        public Attribute(String name, double value, AttributeModifier.Operation operation) {
            this.name = name;
            this.value = value;
            this.operation = operation;
        }

        public String renderDisplay() {
            if(value > 0) {
                if (operation.equals(AttributeModifier.Operation.ADD_NUMBER)) {
                    return "§9+" + df.format(value) + " " + name;
                }
                return "§9+" + df.format(value * 100) + "% " + name;
            } else {
                if (operation.equals(AttributeModifier.Operation.ADD_NUMBER)) {
                    return "§c-" + df.format(value) + " " + name;
                }
                return "§c-" + df.format(value * 100) + "% " + name;
            }
        }
    }

}
