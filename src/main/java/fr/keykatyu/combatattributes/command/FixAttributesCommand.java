package fr.keykatyu.combatattributes.command;

import fr.keykatyu.combatattributes.object.Weapon;
import fr.keykatyu.combatattributes.util.Util;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FixAttributesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Util.prefix() + "§cVous devez être un joueur pour exécuter cette commande.");
            return false;
        }

        ItemStack previousItem = player.getInventory().getItemInMainHand();
        if(!Util.isCustomItem(previousItem)) {
            sender.sendMessage(Util.prefix() + "§cCet item n'est pas un item custom.");
            return false;
        }

        ItemStack newItem = new Weapon(player.getInventory().getItemInMainHand().clone(), player).getUpdatedItem();
        player.getInventory().setItemInMainHand(newItem);
        TextComponent textComponent = new TextComponent();
        textComponent.setText(Util.prefix() + " §aVotre item a bien été mis à jour. Vous pouvez passer votre souris sur ce message " +
                "pour visualiser l'ancien item.");
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
                new Item(previousItem.getType().getKey().toString(), previousItem.getAmount(),
                        ItemTag.ofNbt(previousItem.getItemMeta().getAsString()))));
        player.spigot().sendMessage(textComponent);
        return true;
    }

}