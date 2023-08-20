package fr.keykatyu.combatattributes.object;

import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.item.ItemArmor;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CombatCalculator {

    /**
     * Calculate item attack damage with NMS
     * @param item The NMS ItemStack
     * @param owner The item owner
     * @return The item attack damage
     */
    public static double getAttackDamage(net.minecraft.world.item.ItemStack item, Player owner) {
        double attackDamage = 1.0;
        for(net.minecraft.world.entity.ai.attributes.AttributeModifier modifier : item.a(EnumItemSlot.a).get(GenericAttributes.f)) attackDamage += modifier.d();
        attackDamage += net.minecraft.world.item.enchantment.EnchantmentManager.a(item, null);
        attackDamage *= ((CraftPlayer) owner).getHandle().A(0.5F);
        attackDamage = Math.round(attackDamage * 10.0) / 10.0;
        return attackDamage;
    }

    /**
     * Calculate item attack speed with NMS
     * @param item The NMS ItemStack
     * @return The item attack speed
     */
    public static double getAttackSpeed(net.minecraft.world.item.ItemStack item) {
        double attackSpeed = 1.0;
        for(net.minecraft.world.entity.ai.attributes.AttributeModifier modifier : item.a(EnumItemSlot.a).get(GenericAttributes.h)) attackSpeed += modifier.d();
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
        ItemArmor itemArmor = ((ItemArmor) item.d());
        for(net.minecraft.world.entity.ai.attributes.AttributeModifier modifier : item.a(itemArmor.g()).get(GenericAttributes.i)) armor += modifier.d();
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
        ItemArmor itemArmor = ((ItemArmor) item.d());
        for(net.minecraft.world.entity.ai.attributes.AttributeModifier modifier : item.a(itemArmor.g()).get(GenericAttributes.j)) armorToughness += modifier.d();
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
        ItemArmor itemArmor = ((ItemArmor) item.d());
        for(net.minecraft.world.entity.ai.attributes.AttributeModifier modifier : item.a(itemArmor.g()).get(GenericAttributes.c)) knockbackResistance += modifier.d();
        knockbackResistance = Math.round(knockbackResistance * 10.0) / 10.0;
        return knockbackResistance * 10.0;
    }

}
