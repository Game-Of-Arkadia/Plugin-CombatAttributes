package fr.keykatyu.combatattributes.object;

import fr.keykatyu.combatattributes.Main;
import fr.keykatyu.combatattributes.util.ItemBuilder;
import fr.keykatyu.mctranslation.Language;
import fr.keykatyu.mctranslation.MCTranslator;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.item.*;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
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

    public CombatItem(ItemStack item, Player owner) {
        this.itemStack = item;
        type = Type.retrieveItemType(itemStack);
        ib = new ItemBuilder(item);
        this.owner = owner;
        language = Language.fromPlayer(owner);
    }

    public CombatItem(ItemStack item, Player owner, Language language) {
        this.itemStack = item;
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
        } else if(itemStack.getItemMeta().hasAttributeModifiers()) {
            Item item = CraftItemStack.asNMSCopy(itemStack).d();
            switch (type) {
                case WEAPON -> {
                    List<net.minecraft.world.entity.ai.attributes.AttributeModifier> attackDamageModifiers = item.a(EnumItemSlot.a).get(GenericAttributes.f).stream().toList();
                    double attackDamageBonus = attackDamageModifiers.get(0).d();
                    List<net.minecraft.world.entity.ai.attributes.AttributeModifier> attackSpeedModifiers = item.a(EnumItemSlot.a).get(GenericAttributes.h).stream().toList();
                    double attackSpeedBonus = 4 + attackSpeedModifiers.get(0).d() - 1;
                    ib.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE,
                            new AttributeModifier(UUID.randomUUID(), "fix_attackdamage", attackDamageBonus, AttributeModifier.Operation.ADD_NUMBER, itemStack.getType().getEquipmentSlot()));
                    ib.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED,
                            new AttributeModifier(UUID.randomUUID(), "fix_attackspeed", attackSpeedBonus, AttributeModifier.Operation.ADD_NUMBER, itemStack.getType().getEquipmentSlot()));
                }
                case ARMOR_PIECE -> {
                    ItemArmor itemArmor = (ItemArmor) item;
                    List<net.minecraft.world.entity.ai.attributes.AttributeModifier> armorModifiers = itemArmor.a(itemArmor.g()).get(GenericAttributes.i).stream().toList();
                    double armorBonus = armorModifiers.get(0).d();
                    List<net.minecraft.world.entity.ai.attributes.AttributeModifier> armorToughnessModifiers = itemArmor.a(itemArmor.g()).get(GenericAttributes.j).stream().toList();
                    double armorToughnessBonus = armorToughnessModifiers.get(0).d();
                    ib.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_ARMOR,
                            new AttributeModifier(UUID.randomUUID(), "fix_armor", armorBonus, AttributeModifier.Operation.ADD_NUMBER, itemStack.getType().getEquipmentSlot()));
                    ib.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_ARMOR_TOUGHNESS,
                            new AttributeModifier(UUID.randomUUID(), "fix_armortoughness", armorToughnessBonus, AttributeModifier.Operation.ADD_NUMBER, itemStack.getType().getEquipmentSlot()));
                    if(itemArmor.d() == EnumArmorMaterial.g) {
                        List<net.minecraft.world.entity.ai.attributes.AttributeModifier> knockbackResistanceModifiers = itemArmor.a(itemArmor.g()).get(GenericAttributes.c).stream().toList();
                        double knockbackResistance = knockbackResistanceModifiers.get(0).d();
                        ib.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                                new AttributeModifier(UUID.randomUUID(), "fix_knockbackresistance", knockbackResistance, AttributeModifier.Operation.ADD_NUMBER, itemStack.getType().getEquipmentSlot()));
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
                combatAttributes.add(" §2" + df.format(CombatCalculator.getAttackDamage(nmsItem, owner)) + " " + MCTranslator.translate("attribute.name.generic.attack_damage", language));
                combatAttributes.add(" §2" + df.format(CombatCalculator.getAttackSpeed(nmsItem)) + " " + MCTranslator.translate("attribute.name.generic.attack_speed", language));
            }
            case ARMOR_PIECE -> {
                ItemArmor itemArmor = (ItemArmor) nmsItem.d();
                switch (itemArmor.b()) {
                    case d -> combatAttributes.add("§7" + MCTranslator.translate("item.modifiers.feet", language));
                    case c -> combatAttributes.add("§7" + MCTranslator.translate("item.modifiers.legs", language));
                    case b -> combatAttributes.add("§7" + MCTranslator.translate("item.modifiers.chest", language));
                    case a -> combatAttributes.add("§7" + MCTranslator.translate("item.modifiers.head", language));
                }
                combatAttributes.add("§9+" + df.format(CombatCalculator.getArmor(nmsItem)) + " " + MCTranslator.translate("attribute.name.generic.armor", language));
                combatAttributes.add("§9+" + df.format(CombatCalculator.getArmorToughness(nmsItem)) + " " + MCTranslator.translate("attribute.name.generic.armor_toughness", language));
                if(itemArmor.d() == EnumArmorMaterial.g) {
                    combatAttributes.add("§9+" + df.format(CombatCalculator.getKnockbackResistance(nmsItem)) + " " + MCTranslator.translate("attribute.name.generic.knockback_resistance", language));
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
            Item item = CraftItemStack.asNMSCopy(itemStack).d();
            if(item instanceof ItemToolMaterial || item instanceof ItemTrident) {
                return WEAPON;
            } else if(item instanceof ItemArmor) {
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
