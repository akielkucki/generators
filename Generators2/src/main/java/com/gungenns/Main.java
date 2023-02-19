package com.gungenns;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import com.gungenns.models.GenLocation;
import com.gungenns.models.Generator;
import com.gungenns.models.GeneratorMode;
import com.gungenns.models.GeneratorModes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin {

    private JsonHandler jsonHandler;
    public static Main Instance;
    @Override
    public void onEnable() {
        Instance = this;
        jsonHandler = new JsonHandler(this);
        File bak1 = new File("locations.bak");
        File bak2 = new File("generators.bak");
        jsonHandler.loadGenerators();

        if (bak1.length() > 0) {
            String jsonString = "[\n" +
                    "  {\n" +
                    "    \"ID\": \"00000000\",\n" +
                    "    \"x\": -164.0,\n" +
                    "    \"y\": 89.0,\n" +
                    "    \"z\": 424.0,\n" +
                    "    \"world\": \"world\"\n" +
                    "  }\n" +
                    "]";

            // Create a File object for the file to write
            File file = new File("locations.json");

            try {
                // Create a FileWriter to write to the file
                FileWriter fileWriter = new FileWriter(file);

                // Create a BufferedWriter to improve performance
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                // Write the JSON string to the file
                bufferedWriter.write(jsonString);

                // Close the writer to free up resources
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        jsonHandler.loadLocations();
        getLogger().info("Generator plugin has been enabled.");
        this.getServer().getPluginManager().registerEvents(new Events(), this);
        this.getCommand("generator").setExecutor(this::onCommand);
        this.getCommand("generator").setTabCompleter(new GeneratorTabCompleter());



        try {
            File file = new File(getDataFolder(),"generators.json");
            File file2 = new File(getDataFolder(),"locations.json");
            file.getParentFile().mkdirs();
            file2.getParentFile().mkdirs();
            if (file2.length() == 0) {
                FileWriter writer = new FileWriter(file2);
                writer.write("[{}]");
            }
            if (file.createNewFile() && file2.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            jsonHandler.parseLocationsJson(jsonHandler.getLocationsFile().getAbsolutePath(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonHandler.parseGeneratorJSON(jsonHandler.readJson(jsonHandler.getGeneratorsFile()));
        new BukkitRunnable() {
            int currentGenerator = 0; // Start with the first generator in the list

            @Override
            public void run() {
                for (GenLocation l : jsonHandler.getLocations()) {
                    Location loc = new Location(Bukkit.getWorld(l.getWorld()),l.getX(),l.getY(),l.getZ());
                    loc.add(new Vector(0.5,1,0.5));
                    Generator g = jsonHandler.getGenerators().get(currentGenerator);
                    Bukkit.getWorld("world").dropItem(loc, g.getGeneratedItem()).setVelocity(new Vector(0, 0, 0));
                    currentGenerator = (currentGenerator + 1) % jsonHandler.getGenerators().size(); // Increment the counter and wrap around to the beginning of the list if necessary
                    loc.subtract(new Vector(0.5,1,0.5));
                }
            }
        }.runTaskTimer(this, 0L, getGenTime()); // 20 ticks = 1 second

    }
    int getGenTime() {
        jsonHandler.parseGeneratorJSON(jsonHandler.readJson(jsonHandler.getGeneratorsFile()));
        return JsonHandler.parsedGenTime * 20; //ticks
    }
    @Override
    public void onDisable() {
        jsonHandler.backupLocationsFile();
        jsonHandler.backupGeneratorsFile();
        getLogger().info("Generator plugin has been disabled.");
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("generator")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
                return true;
            }

            Player player = (Player) sender;

            // Generate a diamond block with a random ID
            if (args.length > 0 && args.length < 4) {
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
                color("&cUsage: &e/generator (Generator Item) (Block) (Generated Item Name)",true);
            }
        }
        if (label.equalsIgnoreCase("reloadGenerators")) {
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
    public static String color(String text, boolean withPrefix) {
        if (withPrefix) {
            return ChatColor.translateAlternateColorCodes('&', "&9&l&oGENERATORS "+text);
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
