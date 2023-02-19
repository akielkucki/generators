package com.gungenns;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneratorTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            // Tab complete the generator command argument
            List<String> completions = new ArrayList<>();
            String input = args[0];
            for (Material material : Material.values()) {
                if (material.name().startsWith(input)) {
                    completions.add(material.name().toUpperCase());
                }
            }
            return completions;
        } else if (args.length == 2) {
            // Tab complete the material argument
            List<String> completions = new ArrayList<>();
            String input = args[1];
            for (Material material : Material.values()) {
                if (material.isBlock() && material.name().startsWith(input)) {
                    completions.add(material.name().toUpperCase());
                }
            }
            return completions;
        } else if (args.length == 3) {
            List<String> completions = new ArrayList<>();
            String input = args[1].toLowerCase();
            completions.add("|Colored|-Item-Name");
            return completions;
        } else {
            return Arrays.asList("");
        }
    }
}

