package me.loidsemus.pluginlib.config;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.net.URL;

public class ConfigFile {

    private final HoconConfigurationLoader loader;
    private CommentedConfigurationNode root;

    public ConfigFile(HoconConfigurationLoader loader) {
        this.loader = loader;
        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load config (see stacktrace), will load empty node instead");
        }
    }

    /**
     * Reloads this config from the file. Will load an empty root node if no file exists.
     *
     * @throws IOException If the file could not be loaded
     */
    public void load() throws IOException {
        root = loader.load();
    }

    public void save() throws IOException {
        loader.save(root);
    }

    /**
     * Gets a node, same method as Configurate's, but always from root.
     * Refer to <a href="https://github.com/SpongePowered/Configurate/wiki/Node">Configurate's wiki on nodes</a>
     *
     * @param path Path of the wanted node
     * @return The node, never null. If it doesn't exist it will be an empty "virtual" node.
     */
    public CommentedConfigurationNode getNode(Object... path) {
        return root.getNode(path);
    }

    /**
     * Copies the values from a file (in resources...) to this file. Does not override values that already exist.
     * Also saves the config.
     *
     * @param source The source to copy from
     * @throws IOException If loading or saving fails
     */
    public void copyDefaults(URL source) throws IOException {
        root.mergeValuesFrom(HoconConfigurationLoader.builder()
                .setURL(source)
                .build()
                .load(ConfigurationOptions.defaults()));
        save();
    }

}
