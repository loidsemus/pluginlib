package me.loidsemus.pluginlib.config;

import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.util.HashMap;

public class Config {

    private static HashMap<String, ConfigFile> files = new HashMap<>();

    public static void loadFile(File dir, String filename) {
        File file = new File(dir, filename);
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setPath(file.toPath()).build();
        files.put(filename, new ConfigFile(loader));
    }

    /**
     * Removes the configuration, does not save to a file.
     *
     * @param filename The loaded config to unload
     */
    public static void unloadFile(String filename) {
        if (!filename.endsWith(".conf")) {
            filename += ".conf";
        }
        files.remove(filename);
    }

    public static ConfigFile file(String filename) {
        if (!filename.endsWith(".conf")) {
            filename += ".conf";
        }
        return files.get(filename);
    }

}
