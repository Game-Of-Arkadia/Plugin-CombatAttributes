package fr.keykatyu.weaponsattributes.object;

import fr.keykatyu.mctranslation.Language;
import fr.keykatyu.mctranslation.MCTranslator;
import fr.keykatyu.weaponsattributes.Main;
import fr.keykatyu.weaponsattributes.util.ItemBuilder;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemToolMaterial;
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

    private final ItemStack itemStack;
    private final ItemBuilder ib;
    private final Player owner;
    private final Language language;

    public Weapon(ItemStack item, Player owner) {
        this.itemStack = item;
        ib = new ItemBuilder(item);
        this.owner = owner;
        language = Language.fromPlayer(owner);
    }

    /**
     * Update item stats in the lore
     */
    public ItemStack getUpdatedItem() {
        if(!itemStack.hasItemMeta()) return itemStack;
        List<String> lore = new ArrayList<>();
        if(itemStack.getItemMeta().hasLore()) lore = itemStack.getItemMeta().getLore();

        Item item = CraftItemStack.asNMSCopy(itemStack).d();
        if(itemStack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "WeaponsAttributesLore"), PersistentDataType.INTEGER)) {
            int weaponsAttributesLines = itemStack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Main.getInstance(), "WeaponsAttributesLore"), PersistentDataType.INTEGER);
            if (weaponsAttributesLines > 0 && weaponsAttributesLines <= lore.size()) {
                lore.subList(lore.size() - weaponsAttributesLines, lore.size()).clear();
            }
        } else if(itemStack.getItemMeta().hasAttributeModifiers() && item instanceof ItemToolMaterial) {
            // Fix attack damage and attack speed
            List<net.minecraft.world.entity.ai.attributes.AttributeModifier> attackDamageModifiers = item.a(EnumItemSlot.a).get(GenericAttributes.f).stream().toList();
            double attackDamageBonus = attackDamageModifiers.get(0).d();
            List<net.minecraft.world.entity.ai.attributes.AttributeModifier> attackSpeedModifiers = item.a(EnumItemSlot.a).get(GenericAttributes.h).stream().toList();
            double attackSpeedBonus = 4 + attackSpeedModifiers.get(0).d() - 1;
            ib.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE,
                    new AttributeModifier(UUID.randomUUID(), "fix_attackdamage", attackDamageBonus, AttributeModifier.Operation.ADD_NUMBER));
            ib.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED,
                    new AttributeModifier(UUID.randomUUID(), "fix_attackspeed", attackSpeedBonus, AttributeModifier.Operation.ADD_NUMBER));
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
            String equipmentSlotKey = map.getKey().name().toLowerCase().toString();
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

    /**
     * Get item attributes
     * @return The item attributes for each {@link EquipmentSlot}
     */
    private HashMap<EquipmentSlot, List<Attribute>> getAttributes() {
        HashMap<EquipmentSlot, List<Attribute>> attributes = new HashMap<>();
        if(!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasAttributeModifiers()) return attributes;

        for(Map.Entry<org.bukkit.attribute.Attribute, AttributeModifier> attribute : itemStack.getItemMeta().getAttributeModifiers().entries()) {
            if(attribute.getValue().getName().equals("fix_attackdamage") ||
                attribute.getValue().getName().equals("fix_attackspeed")) continue;

            String name = MCTranslator.translate("attribute.name." + attribute.getKey().getKey().getKey(), Language.fromPlayer(owner));
            double value = attribute.getValue().getAmount();
            AttributeModifier.Operation operation = attribute.getValue().getOperation();

            List<Attribute> attributesList = attributes.computeIfAbsent(attribute.getValue().getSlot(), k -> new ArrayList<>());
            attributesList.add(new Attribute(name, value, operation));
            EquipmentSlot slot = attribute.getValue().getSlot();
            if(slot == null) slot = EquipmentSlot.HAND;
            attributes.put(slot, attributesList);
        }
        return attributes;
    }

    /**
     * Calculate item attack damage with NMS
     * @param item The NMS ItemStack
     * @return The item attack damage
     */
    private long getAttackDamage(net.minecraft.world.item.ItemStack item) {
        double attackDamage = 1.0;
        for(net.minecraft.world.entity.ai.attributes.AttributeModifier modifier : item.a(EnumItemSlot.a).get(GenericAttributes.f)) attackDamage += modifier.d();
        attackDamage += net.minecraft.world.item.enchantment.EnchantmentManager.a(item, null);
        attackDamage *= ((CraftPlayer) owner).getHandle().A(0.5F);
        attackDamage = Math.round(attackDamage * 10.0) / 10.0;
        return Math.round(attackDamage);
    }

    /**
     * Calculate item attack speed with NMS
     * @param item The NMS ItemStack
     * @return The item attack speed
     */
    private long getAttackSpeed(net.minecraft.world.item.ItemStack item) {
        double attackSpeed = 1.0;
        for(net.minecraft.world.entity.ai.attributes.AttributeModifier modifier : item.a(EnumItemSlot.a).get(GenericAttributes.h)) attackSpeed += modifier.d();
        attackSpeed = Math.round(attackSpeed * 10.0) / 10.0;
        return Math.round(attackSpeed);
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
                    return "§9+" + Math.round(value) + " " + name;
                }
                return "§9+" + Math.round(value * 100) + "% " + name;
            } else {
                if (operation.equals(AttributeModifier.Operation.ADD_NUMBER)) {
                    return "§c-" + Math.round(value) + " " + name;
                }
                return "§c-" + Math.round(value * 100) + "% " + name;
            }
        }
    }

}
