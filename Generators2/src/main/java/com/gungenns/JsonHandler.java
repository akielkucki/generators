package com.gungenns;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gungenns.models.GenLocation;
import com.gungenns.models.Generator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.json.*;

import static com.gungenns.Main.Instance;

public class JsonHandler {
    private final JavaPlugin plugin;

    public File getGeneratorsFile() {
        return generatorsFile;
    }

    public File getLocationsFile() {
        return locationsFile;
    }

    private final File generatorsFile = new File(Instance.getDataFolder().getAbsolutePath(), "generators.json");
    private final File locationsFile = new File(Instance.getDataFolder().getAbsolutePath(), "locations.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private List<Generator> generators = new ArrayList<>();
    private ArrayList<Location> locations = new ArrayList<>();
    private ArrayList<GenLocation> genLocations = new ArrayList<>();

    public JsonHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /*public void load() {
        try {
            if (!generatorsFile.exists()) {
                generatorsFile.createNewFile();
                saveGenerators();
            } else {
                FileReader reader = new FileReader(generatorsFile);
                Type type = new TypeToken<List<Generator>>(){}.getType();
                generators = gson.fromJson(reader, type);
                reader.close();
            }
            if (!locationsFile.exists()) {
                locationsFile.createNewFile();

            } else {
                FileReader reader = new FileReader(locationsFile);
                Type type = new TypeToken<List<Location>>(){}.getType();
                locations = gson.fromJson(reader, type);
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    static String parsedGenID, parsedGenItem, parsedGenItemName, parsedGenHeldItem;
    static int parsedGenTime;
    public void parseGeneratorJSON(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String generatorID = jsonObject.getString("GeneratorID");
                String generatedItem = jsonObject.getString("generated_Item");
                String generatedItemName = jsonObject.getString("generated_Item_name");
                int generatorTime = jsonObject.getInt("generator_Time");
                String generatorHeldItem = jsonObject.getString("generator_Held_Item");

                parsedGenID = generatorID;
                parsedGenItem = generatedItem;
                parsedGenItemName = generatedItemName;
                parsedGenTime = generatorTime;
                parsedGenHeldItem = generatorHeldItem;
/*
                System.out.println("GeneratorID: " + generatorID);
                System.out.println("Generated Item: " + generatedItem);
                System.out.println("Generated Item Name: " + generatedItemName);
                System.out.println("Generator Time: " + generatorTime);
                System.out.println("Generator Held Item: " + generatorHeldItem);
*/
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String readJson(File file) {
        // Create an ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        // Create a File object for the JSON file

        try {
            // Read the contents of the file into a string
            String json = objectMapper.readTree(file).toString();
            return json;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "[{}]";
    }
    List<Location> parsedLocations;
    String parsedLocationGUID;
    public void parseLocationsJson(String filename, boolean forGenerator) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNodes = mapper.readTree(new File(filename));
        parsedLocations = new ArrayList<>();
        Location location = null;
        for (JsonNode node : jsonNodes) {
            double x = node.get("x").asDouble();
            double y = node.get("y").asDouble();
            double z = node.get("z").asDouble();
            String world = node.get("world").asText();
            String locationGUID = node.get("ID").asText();
            parsedLocationGUID = locationGUID;
            location = new Location(Bukkit.getWorld(world),x,y,z);

            
            System.out.println("Location with ID " + locationGUID + ": x=" + x + ", y=" + y + ", z=" + z + ", world=" + world);
        }
        if (forGenerator) {
            location.add(new Vector(0.5,1,0.5));
        }
        parsedLocations.add(location);
    }
    public static String readJSONFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
    public void saveGenerators() {
        try (FileWriter writer = new FileWriter(generatorsFile)) {
            // Read existing generators from file, if any
            JsonArray existingGeneratorsJson = null;
            if (generatorsFile.exists() && generatorsFile.length() > 0) {
                try (BufferedReader reader = new BufferedReader(new FileReader(generatorsFile))) {
                    existingGeneratorsJson = gson.fromJson(reader, JsonArray.class);
                }
            }

            // Create a new JsonArray to hold all generators, old and new
            JsonArray allGeneratorsJson = new JsonArray();

            // Add existing generators to the new array, if any
            if (existingGeneratorsJson != null) {
                for (JsonElement generatorJson : existingGeneratorsJson) {
                    allGeneratorsJson.add(generatorJson);
                }
            }

            // Add new generators to the new array
            for (Generator generator : generators) {
                JsonObject generatorJson = new JsonObject();
                generatorJson.addProperty("GeneratorID", generator.getGUID());
                generatorJson.addProperty("generated_Item", generator.getGeneratedItem().getType().name());
                generatorJson.addProperty("generated_Item_name", generator.getName());
                generatorJson.addProperty("generator_Time", generator.getGenTime());
                generatorJson.addProperty("generator_Held_Item", generator.getGenDisplayItem().name());
                generatorJson.addProperty("generator_Item_Meta", generator.getGeneratedItemMeta().getAsString());
                generatorJson.addProperty("generator_Meta", generator.getGenMeta().getAsString());

                allGeneratorsJson.add(generatorJson);
            }

            // Write the new array to the file
            writer.write(gson.toJson(allGeneratorsJson));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveLocations(String GUID) {

        JsonArray existingLocationsJson = null;
        if (locationsFile.exists() && locationsFile.length() > 0) {
            try (BufferedReader reader = new BufferedReader(new FileReader(locationsFile))) {
                existingLocationsJson = gson.fromJson(reader, JsonArray.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Create a new JsonArray to hold all generators, old and new
        JsonArray allLocationsJson = new JsonArray();

        // Add existing generators to the new array, if any
        if (existingLocationsJson != null) {
            for (JsonElement generatorJson : existingLocationsJson) {
                allLocationsJson.add(generatorJson);
            }
        }

        try (FileWriter writer = new FileWriter(locationsFile)) {


            for (GenLocation location : genLocations) {
                JsonObject locationJsonObj = new JsonObject();

                    locationJsonObj.addProperty("ID", location.getGUID());
                    locationJsonObj.addProperty("x", location.getX());
                    locationJsonObj.addProperty("y", location.getY());
                    locationJsonObj.addProperty("z", location.getZ());
                    locationJsonObj.addProperty("world", location.getWorld());

                allLocationsJson.add(locationJsonObj);
            }
            gson.toJson(allLocationsJson, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void backupLocationsFile() {
        File sourceFile = locationsFile;
        File backupFile = new File(Instance.getDataFolder(), "locations.bak");

        try {
            Files.copy(sourceFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void backupGeneratorsFile() {
        File sourceFile = generatorsFile;
        File backupFile = new File(Instance.getDataFolder(), "generators.bak");

        try {
            Files.copy(sourceFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGenerators() {
        if (generatorsFile.exists() && generatorsFile.length() > 0) {
            try (BufferedReader reader = new BufferedReader(new FileReader(generatorsFile))) {
                JsonArray allGeneratorsJson = gson.fromJson(reader, JsonArray.class);
                for (JsonElement generatorJson : allGeneratorsJson) {
                    String id = generatorJson.getAsJsonObject().get("GeneratorID").getAsString();
                    String generatedItemType = generatorJson.getAsJsonObject().get("generated_Item").getAsString();
                    String generatedItemName = generatorJson.getAsJsonObject().get("generated_Item_name").getAsString();
                    int genTime = generatorJson.getAsJsonObject().get("generator_Time").getAsInt();
                    String genHeldItem = generatorJson.getAsJsonObject().get("generator_Held_Item").getAsString();
                    String genItemMeta = generatorJson.getAsJsonObject().get("generator_Item_Meta").getAsString();
                    String genMeta = generatorJson.getAsJsonObject().get("generator_Meta").getAsString();

                    // Create new generator object and add it to the list
                    Generator generator = new Generator(Material.valueOf(genHeldItem), new ItemStack(Material.valueOf(generatedItemType)), new ItemStack(Material.valueOf(genHeldItem)).getItemMeta(), new ItemStack(Material.valueOf(generatedItemType)).getItemMeta(), generatedItemName, genTime, id);
                    // Material genDisplayItem,ItemStack generatedItem,ItemMeta genMeta,ItemMeta generatedItemMeta,String name, int genTime, String GUID
                    //generator.setGeneratedItemMeta(MaterialUtil.getItemMetaFromString(genItemMeta));
                    //generator.setGenMeta(MaterialUtil.getItemMetaFromString(genMeta));
                    generators.add(generator);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void loadLocations() {
        try (BufferedReader reader = new BufferedReader(new FileReader(locationsFile))) {
            JsonArray locationsJson = gson.fromJson(reader, JsonArray.class);

            for (JsonElement locationElement : locationsJson) {
                JsonObject locationJson = locationElement.getAsJsonObject();
                String ID = locationJson.get("ID").getAsString();
                double x = locationJson.get("x").getAsDouble();
                double y = locationJson.get("y").getAsDouble();
                double z = locationJson.get("z").getAsDouble();
                World world = Bukkit.getWorld(locationJson.get("world").getAsString());

                //Location location = new Location(world, x, y, z);
                GenLocation genLocation = new GenLocation(x,y,z,world.getName(),ID);
                genLocations.add(genLocation);
                //locations.add(location);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getGeneratorID(GenLocation loc) {
        return loc.getGUID();
    }




    public boolean isGeneratorInInventory(String GUID) {
        parseGeneratorJSON(readJson(generatorsFile));
        return GUID.equals(parsedGenID);
    }
    public void addLocation(GenLocation location) {
        genLocations.add(location);
    }
    public boolean isGeneratorAtLocation(Block block) {
        Location blockLocation = block.getLocation();

        for (Generator generator : generators) {
            Location generatorLocation = generator.getGenLocation();

            if (blockLocation.equals(generatorLocation)) {
                return true;
            }
        }

        return false;
    }


    public void removeLocation(GenLocation location) {
        genLocations.remove(location);
        saveLocations(location.getGUID());
    }

    public List<GenLocation> getLocations() {
        return new ArrayList<>(genLocations);
    }

    public void addGenerator(Generator generator) {
        generators.add(generator);
        saveGenerators();
    }

    public void removeGenerator(Generator generator) {
        generators.remove(generator);
        saveGenerators();
    }

    public List<Generator> getGenerators() {
        return new ArrayList<>(generators);
    }
}
