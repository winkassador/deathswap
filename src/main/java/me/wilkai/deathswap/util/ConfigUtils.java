package me.wilkai.deathswap.util;

import me.wilkai.deathswap.DeathswapPlugin;
import me.wilkai.deathswap.config.Config;
import me.wilkai.deathswap.config.ConfigElement;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Various Utilities relating to the Plugin's Configuration.
 */
public class ConfigUtils {

    /**
     * The Instance of the Deathswap Plugin.
     */
    private static final DeathswapPlugin plugin;

    static {
        plugin = DeathswapPlugin.getInstance();
    }

    /**
     * Reads the Config from the Plugin's config.yml file.
     */
    public static Config readConfig() {
        Config config = new Config();

        FileConfiguration messages = new YamlConfiguration();

        try {
            messages.load(new File("plugins/Deathswap/messages.yml"));
        }
        catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load messages.yml! Defaulting to default Messages.\nCause: " + e.getMessage());
        }

        for(Field field : Config.class.getFields()) {
            try {
                Object value;

                if(field.getAnnotation(ConfigElement.class) != null) {
                    value = plugin.getConfig().get(field.getName());
                }
                else {
                    value = messages.get(field.getName());
                }

                if(value == null) { // If a value is set to null.
                    continue; // Don't set the field, it will resort to its default value.
                }

                field.set(config, value); // If all is well set the value.

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return config;
    }

    /**
     * Saves the Config to the Plugin's config.yml file.
     */
    public static void saveConfig(Config config) {
        Config defaultConfig = new Config();

        for(Field field : Config.class.getFields()) {
            try {
                Object value = field.get(config);

                if(value == null) { // If the value has somehow been set to null.
                    value = field.get(defaultConfig); // Reset it to the default value.
                }

                plugin.getConfig().set(field.getName(), value);
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        try {
            plugin.getConfig().save("plugins/Deathswap/config.yml");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
