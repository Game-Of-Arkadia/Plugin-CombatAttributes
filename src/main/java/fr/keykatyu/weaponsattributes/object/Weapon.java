package fr.keykatyu.weaponsattributes.object;

import fr.keykatyu.mctranslation.Language;
import fr.keykatyu.mctranslation.MCTranslator;
import fr.keykatyu.weaponsattributes.Main;
import fr.keykatyu.weaponsattributes.util.ItemBuilder;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.item.*;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class Weapon {

    private final ItemStack item;
    private final ItemBuilder ib;
    private final Player owner;
    private final Language language;

    public Weapon(ItemStack item, Player owner) {
        this.item = item;
        ib = new ItemBuilder(item);
        this.owner = owner;
        language = Language.fromPlayer(owner);
    }

    /**
     * Update item attributes (stats in the lore)
     */
    public ItemStack getUpdatedItem() {
        if(!item.hasItemMeta()) return item;
        List<String> lore = new ArrayList<>();
        if(item.getItemMeta().hasLore()) lore = item.getItemMeta().getLore();

        Item itemTool = CraftItemStack.asNMSCopy(item).d();
        if(item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "WeaponsAttributesLore"), PersistentDataType.INTEGER)) {
            int weaponsAttributesLines = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Main.getInstance(), "WeaponsAttributesLore"), PersistentDataType.INTEGER);
            if (weaponsAttributesLines > 0 && weaponsAttributesLines <= lore.size()) {
                lore.subList(lore.size() - weaponsAttributesLines, lore.size()).clear();
            }
        } else if(item.getItemMeta().hasAttributeModifiers() && itemTool instanceof ItemToolMaterial) {
            double attackDamage = 0.0;
            if(itemTool instanceof ItemSword sword) {
                attackDamage = sword.h();
            } else if (itemTool instanceof ItemTool tool) {
                attackDamage = tool.d();
            }
            ib.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE,
                    new AttributeModifier(UUID.randomUUID(), "fix_attackdamage", attackDamage, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        }
        List<String> weaponsAttributes = new ArrayList<>();
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(ib.toItemStack());
        weaponsAttributes.add("");
        weaponsAttributes.add("§7" + MCTranslator.translate("item.modifiers.mainhand", language));
        weaponsAttributes.add(" §2" + getAttackDamage(nmsItem) + " " + MCTranslator.translate("attribute.name.generic.attack_damage", language));
        weaponsAttributes.add(" §2" + getAttackSpeed(nmsItem) + " " + MCTranslator.translate("attribute.name.generic.attack_speed", language));

        HashMap<EquipmentSlot, List<Attribute>> attributes = getAttributes();
        if(attributes.containsKey(EquipmentSlot.HAND)) attributes.get(EquipmentSlot.HAND).forEach(attribute -> weaponsAttributes.add(attribute.renderDisplay()));
        attributes.remove(EquipmentSlot.HAND);

        for(Map.Entry<EquipmentSlot, List<Attribute>> map : attributes.entrySet()) {
            String equipmentSlotKey = map.getKey().name().toString();
            if(map.getKey().equals(EquipmentSlot.OFF_HAND)) equipmentSlotKey = "offhand";
            weaponsAttributes.add("");
            weaponsAttributes.add("§7" + MCTranslator.translate("item.modifiers." + equipmentSlotKey, language));
            map.getValue().forEach(attribute -> weaponsAttributes.add(attribute.renderDisplay()));
        }

        lore.addAll(weaponsAttributes);
        return ib.setItemsFlags(ItemFlag.HIDE_ATTRIBUTES).setLore(lore)
                .addNBTKey(new NamespacedKey(Main.getInstance(), "WeaponsAttributesLore"), PersistentDataType.INTEGER, weaponsAttributes.size())
                .toItemStack();
    }

    private HashMap<EquipmentSlot, List<Attribute>> getAttributes() {
        HashMap<EquipmentSlot, List<Attribute>> attributes = new HashMap<>();
        if(!item.hasItemMeta() || !item.getItemMeta().hasAttributeModifiers()) return attributes;

        for(Map.Entry<org.bukkit.attribute.Attribute, AttributeModifier> attribute : item.getItemMeta().getAttributeModifiers().entries()) {
            if(attribute.getValue().getName().equals("fix_attackdamage")) continue;

            String name = MCTranslator.translate("attribute.name." + attribute.getKey().getKey().getKey(), Language.fromPlayer(owner));
            double value = attribute.getValue().getAmount();
            AttributeModifier.Operation operation = attribute.getValue().getOperation();

            List<Attribute> attributesList = attributes.computeIfAbsent(attribute.getValue().getSlot(), k -> new ArrayList<>());
            attributesList.add(new Attribute(name, value, operation));
            attributes.put(attribute.getValue().getSlot(), attributesList);
        }
        return attributes;
    }

    private double getAttackDamage(net.minecraft.world.item.ItemStack item) {
        double attackDamage = 1.0;
        for(net.minecraft.world.entity.ai.attributes.AttributeModifier modifier : item.a(EnumItemSlot.a).get(GenericAttributes.f)) attackDamage += modifier.d();
        attackDamage += net.minecraft.world.item.enchantment.EnchantmentManager.a(item, null);
        attackDamage *= ((CraftPlayer) owner).getHandle().A(0.5F);
        return Math.round(attackDamage * 10.0) / 10.0;
    }

    private double getAttackSpeed(net.minecraft.world.item.ItemStack item) {
        double attackSpeed = 1.0;
        for(net.minecraft.world.entity.ai.attributes.AttributeModifier modifier : item.a(EnumItemSlot.a).get(GenericAttributes.h)) attackSpeed += modifier.d();
        return Math.round(attackSpeed * 10.0) / 10.0;
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
                    return "§9+" + value + " " + name;
                }
                return "§9+" + Math.round(value * 100) + "% " + name;
            } else {
                if (operation.equals(AttributeModifier.Operation.ADD_NUMBER)) {
                    return "§c-" + value + " " + name;
                }
                return "§c-" + Math.round(value * 100) + "% " + name;
            }
        }
    }

}
