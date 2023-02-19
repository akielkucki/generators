package com.gungenns.models;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GeneratorModes {
    private Map<Player, GeneratorMode> playerModes;

    public GeneratorModes() {
        this.playerModes = new HashMap<>();
    }

    public void setMode(Player player, GeneratorMode mode) {
        playerModes.put(player, mode);
    }

    public GeneratorMode getMode(Player player) {
        return playerModes.get(player);
    }

    public GeneratorMode checkMode(Player p) {
        for (Map.Entry<Player, GeneratorMode> entry : playerModes.entrySet()) {
            Player player = entry.getKey();
            GeneratorMode mode = entry.getValue();

            if (player == p) {
                System.out.println("Player Mode check true");
                return mode;
            }

            System.out.println("Player: " + player.getName() + ", Mode: " + mode);
        }
        return GeneratorMode.ACTIVE;
    }
}

