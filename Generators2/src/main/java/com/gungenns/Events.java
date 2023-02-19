package com.gungenns;

import com.gungenns.models.GenLocation;
import com.gungenns.models.Generator;
import com.gungenns.models.GeneratorMode;
import com.gungenns.models.GeneratorModes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;

import static com.gungenns.Main.Instance;

public class Events implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        JsonHandler handler = new JsonHandler(Instance);
        String GUID = "";
        String itemDisplayName = p.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
            String[] id = itemDisplayName.split("ID: ");
            if (id.length > 0) {
                try {
                    GUID = id[1];
                } catch (IndexOutOfBoundsException ignored) {}
            }

        if (handler.isGeneratorInInventory(GUID)) {
            Location blockLoc = e.getBlock().getLocation();
            GenLocation location = new GenLocation(blockLoc.getX(),blockLoc.getY(),blockLoc.getZ(),blockLoc.getWorld().getName(),GUID);
            handler.addLocation(location);

            handler.saveLocations(GUID);

            removeHand(p);
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        JsonHandler handler = new JsonHandler(Instance);
        Location loc = e.getBlock().getLocation();

        // Remove location from locations.json
        handler.loadLocations();
        String GUID = "";
        GenLocation location = new GenLocation(loc.getX(),loc.getY(),loc.getZ(),loc.getWorld().getName(),GUID);
        String guid = handler.getGeneratorID(location);
        handler.removeLocation(location);
        handler.saveLocations(guid);

        // Remove generator from generators.json
        handler.loadGenerators();
        for (Generator gen : handler.getGenerators()) {
                if (gen.getGenLocation().equals(loc)) {
                    handler.removeGenerator(gen);
                    p.sendMessage("Removed Generator");
                }
        }
        handler.saveGenerators();
        p.sendMessage("Location and generator removed.");
    }



    public void removeHand(@NotNull Player p) {
        p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
    }
}
