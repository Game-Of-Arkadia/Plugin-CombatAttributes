package fr.keykatyu.weaponsattributes.command;

import fr.keykatyu.weaponsattributes.object.Weapon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FixAttributesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("§7[§f§lWeaponsAttributes§7] §cVous devez être un joueur pour exécuter cette commande.");
            return false;
        }

        player.getInventory().setItemInMainHand(new Weapon(player.getInventory().getItemInMainHand(), player).getUpdatedItem());
        return false;
    }

}