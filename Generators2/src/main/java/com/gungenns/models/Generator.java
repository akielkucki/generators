package com.gungenns.models;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Generator {

    private Material genDisplayItem;
    private ItemStack generatedItem;
    private ItemMeta genMeta;
    private ItemMeta generatedItemMeta;
    private String name;
    private int genTime;
    private Location genLocation;
    private transient String GUID;

    public Generator(Material genDisplayItem, ItemStack generatedItem, ItemMeta genMeta,
                     ItemMeta generatedItemMeta, String name, int genTime, String GUID) {
        this.genDisplayItem = genDisplayItem;
        this.generatedItem = generatedItem;
        this.genMeta = genMeta;
        this.generatedItemMeta = generatedItemMeta;
        this.name = name;
        this.genTime = genTime;
        this.GUID = GUID;
    }


    public Material getGenDisplayItem() {
        return genDisplayItem;
    }

    public void setGenDisplayItem(Material genDisplayItem) {
        this.genDisplayItem = genDisplayItem;
    }

    public ItemStack getGeneratedItem() {
        return generatedItem;
    }

    public void setGeneratedItem(ItemStack generatedItem) {
        this.generatedItem = generatedItem;
    }

    public ItemMeta getGenMeta() {
        return genMeta;
    }

    public void setGenMeta(ItemMeta genMeta) {
        this.genMeta = genMeta;
    }

    public ItemMeta getGeneratedItemMeta() {
        return generatedItemMeta;
    }

    public void setGeneratedItemMeta(ItemMeta generatedItemMeta) {
        this.generatedItemMeta = generatedItemMeta;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGenTime() {
        return genTime;
    }

    public void setGenTime(int genTime) {
        this.genTime = genTime;
    }

    public Location getGenLocation() {
        return genLocation;
    }

    public void setGenLocation(Location genLocation) {
        this.genLocation = genLocation;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

}
