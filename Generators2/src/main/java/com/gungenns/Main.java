package com.gungenns;

import com.gungenns.commands.GeneratorCommand;
import com.gungenns.models.GenLocation;
import com.gungenns.models.Generator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin {

    private JsonHandler jsonHandler;
    public static Main Instance;
    private HashMap<Location,ItemStack> itemMap = new HashMap<>();
    @Override
    public void onEnable() {
        Instance = this;
        jsonHandler = new JsonHandler(this);
        File bak1 = new File("locations.bak");
        File bak2 = new File("generators.bak");
        try {
            jsonHandler.loadGenerators();
            jsonHandler.loadLocations();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        getLogger().info("Generator plugin has been enabled.");
        this.getServer().getPluginManager().registerEvents(new Events(), this);
        this.getCommand("generator").setExecutor(new GeneratorCommand());
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
                    l.setDropped(false);
                    String locationGUID = l.getGUID();
                    for (Generator generator : jsonHandler.getGenerators()) {
                        String generatorGUID = generator.getGUID();
                        if (locationGUID.equals(generatorGUID)) {
                            Location loc = new Location(Bukkit.getWorld(l.getWorld()),l.getX(),l.getY(),l.getZ());
                            loc.add(new Vector(0.5,1,0.5));
                                Bukkit.getWorld("world").dropItem(loc, generator.getGeneratedItem()).setVelocity(new Vector(0, 0, 0));
                                currentGenerator = (currentGenerator + 1) % jsonHandler.getGenerators().size(); // Increment the counter and wrap around to the beginning of the list if necessary

                            loc.subtract(new Vector(0.5,1,0.5));
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0L, getGenTime()); // 20 ticks = 1 second

    }
    int getGenTime() {
        jsonHandler.parseGeneratorJSON(jsonHandler.readJson(jsonHandler.getGeneratorsFile()));
        for (Generator g : jsonHandler.getGenerators()) {
            return g.getGenTime() * 20; //ticks
        }
        return 20;
    }
    @Override
    public void onDisable() {
        jsonHandler.backupLocationsFile();
        jsonHandler.backupGeneratorsFile();
        getLogger().info("Generator plugin has been disabled.");
    }


    public static String color(String text, boolean withPrefix) {
        if (withPrefix) {
            return ChatColor.translateAlternateColorCodes('&', "&9&l&oGENERATORS "+text);
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
