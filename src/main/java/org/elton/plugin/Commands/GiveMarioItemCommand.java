package org.elton.plugin.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.elton.plugin.Items.DoubleCherryItem;
import org.elton.plugin.Items.FireFlowerItem;
import org.elton.plugin.TheMarioPlugin;

public class GiveMarioItemCommand implements CommandExecutor {
    //main plugin ref
    private final TheMarioPlugin plugin;
    public GiveMarioItemCommand(TheMarioPlugin plugin) {this.plugin = plugin;}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        //if not player or console return false
        if(!(sender instanceof Player || sender instanceof ConsoleCommandSender))
            return false;

        //if no permission return false
        if(!sender.hasPermission("themarioplugin.give")) {
            sender.sendMessage(ChatColor.RED + "You do not have the required permission to use this command");
            return false;
        }

        //If incorrect number of arguments
        if(!(args.length == 3)) {
            sender.sendMessage(ChatColor.RED + "Incorrect number of arguments. Expected 3." +
                    "\n Usage: /givemario <player> <item> <amount>");
            return false;
        }

        //gets the target
        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        //if player not found, returns
        if(target == null) {
            sender.sendMessage(ChatColor.RED + "Could not find player " + targetName);
            return false;
        }

        //gets the target's inventory
        Inventory targetInv = target.getInventory();

        //if target inventory full ,returns
        if(targetInv.firstEmpty() == -1) {
            sender.sendMessage(ChatColor.RED + targetName + "'s Inventory is full. Unable to give item");
            return false;
        }

        //gets the item arg
        String itemName = args[1];
        //gets the amount arg
        int itemAmt = Integer.parseInt(args[2]);

        if(itemAmt > 64) {
            sender.sendMessage(ChatColor.GRAY + "Item amount over 64, setting item amount to 64");
            itemAmt = 64;
        }

        //item variables
        ItemStack itemStack = null;

        //TODO - consider making this a hash tree if the item number increases
        //checks the item against names, if match gives that item
        if(FireFlowerItem.isNameMatch(itemName)) {
            itemStack = FireFlowerItem.getItemStack();
        }
        if(DoubleCherryItem.isNameMatch(itemName)) {
            itemStack = DoubleCherryItem.getItemStack();
        }

        //if item stack != null, then a match was found
        if(!(itemStack == null)) {
            itemStack.setAmount(itemAmt);
            targetInv.addItem(itemStack);
            sender.sendMessage(ChatColor.GRAY + "Gave " + itemAmt + " " + itemName + " to " + targetName);
            return true;
        }

        //if code is here, then the provided item argument is not value
        sender.sendMessage(ChatColor.RED + "The specified item does not exist");
        return true;
    }
}
