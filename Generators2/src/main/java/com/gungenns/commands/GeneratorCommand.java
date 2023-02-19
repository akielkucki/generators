package com.gungenns.commands;

import com.gungenns.JsonHandler;
import com.gungenns.models.Generator;
import com.gungenns.models.GeneratorMode;
import com.gungenns.models.GeneratorModes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.UUID;

import static com.gungenns.Main.Instance;
import static com.gungenns.Main.color;

public class GeneratorCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("generator")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
                return true;
            }
            JsonHandler jsonHandler = new JsonHandler(Instance);
            Player player = (Player) sender;

            // Generate a diamond block with a random ID
            if (args.length == 3) {
                ItemStack generatorItem = new ItemStack(Material.valueOf(args[0]));
                ItemMeta generatedItemMeta = generatorItem.getItemMeta();
                String id = UUID.randomUUID().toString().substring(0, 8);
                generatedItemMeta.setDisplayName(color(args[2],false));
                generatorItem.setItemMeta(generatedItemMeta);
                Generator generator = new Generator(Material.valueOf(args[1]), generatorItem, generatorItem.getItemMeta(), generatedItemMeta, args[2], 1, id); //Material genDisplayItem, ItemStack generatedItem, ItemMeta genMeta,ItemMeta generatedItemMeta, String name, int genTime, String GUID)

                // Add the generated item to the player's inventory
                ItemStack displayItem = new ItemStack(generator.getGenDisplayItem());
                ItemMeta displayItemMeta = displayItem.getItemMeta();
                displayItemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.AQUA + "ID: " + id);
                displayItem.setItemMeta(displayItemMeta);
                player.getInventory().addItem(displayItem);

                // Create a new generator object and add it to the list

                GeneratorModes modes = new GeneratorModes();
                modes.setMode(player, GeneratorMode.ACTIVE);
                Bukkit.broadcastMessage(modes.getMode(player).name());
                // Send a message to the player confirming the generator was added
                jsonHandler.addGenerator(generator);
                jsonHandler.saveGenerators();
                player.sendMessage(ChatColor.GREEN + "Generator added with ID " + id);
                return true;
            } else {
                player.sendMessage(color("&cUsage: &e/generator (Generator Item) (Block) (Generated Item Name)",true));
            }
        }
        if (label.equalsIgnoreCase("reloadGenerators")) {
            JsonHandler jsonHandler = new JsonHandler(Instance);
            Player player = (Player) sender;
            jsonHandler.parsedLocations.clear();
            try {
                jsonHandler.parseLocationsJson(jsonHandler.getLocationsFile().getAbsolutePath(),true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            player.sendMessage(color("&cGenerators reloaded",true));
        }

        return false;
    }
}
