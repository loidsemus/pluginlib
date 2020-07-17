package me.loidsemus.pluginlib.messages;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Messages {

    private static final Properties properties = new Properties();

    private static boolean useDefaults = false;
    private static Translatable prefixTranslatable;
    private static List<String> missingKeys = new ArrayList<>();
    //private String prefixOffset;

    private Messages() {}

    /**
     * Loads messages from file with specified language code and saves defaults into lang_default.properties.
     * Keys and values to save and use as defaults are specified with a list of {@link Translatable}.
     * Messages are also retrieved using those keys.
     *
     * @param langDirectory Directory to load and save language files to
     * @param languageCode  Which code to load language files from, ignored if set to use defaults. If set to "default", default values from
     *                      provided {@link Translatable}s will be used.
     * @param values        Keys and default values to use. Used to save to lang_defaults.properties and to load from file.
     * @param prefix        Prefix to use for all messages.
     * @return true if everything went correctly and a file has been loaded, else false.
     */
    public static boolean load(File langDirectory, String languageCode, Translatable[] values, Translatable prefix) {
        Logger logger = Logger.getLogger(langDirectory + File.separator + "lang_" + languageCode + ".properties");

        missingKeys.clear();
        saveDefaults(new File(langDirectory, "lang_default.properties"), values);
        Messages.prefixTranslatable = prefix;

        useDefaults = false;

        if (languageCode.equals("default")) {
            useDefaults = true;
            return true;
        }

        if (!langDirectory.exists()) {
            if (!langDirectory.mkdirs()) {
                logger.log(Level.WARNING, "Couldn't create language directory, falling back to default language values");
                useDefaults();
                return false;
            }
        }
        properties.clear();

        File langFile = new File(langDirectory, "lang_" + languageCode + ".properties");
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(langFile);
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "No language file matches the code \"" + languageCode + "\"! Falling back to default values" +
                    " Please make one by copying the contents of lang_default.properties, or changing the config value to \"default\"." +
                    " DO NOT change the values in lang_default.properties!");
            useDefaults = true;
            return false;
        }

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        //setPrefixOffset(properties.getProperty("prefix"));

        for (Translatable key : values) {
            if (!properties.containsKey(key.getKey())) {
                missingKeys.add(key.getKey());
            }
        }

        if (missingKeys.isEmpty()) {
            return true;
        }

        logger.log(Level.WARNING,
                "lang_" + languageCode + ".properties is missing " + missingKeys.size() + " values: " +
                        String.join(", ", missingKeys) + "\n" +
                        "To fix this, copy the missing values from lang_default.properties to lang_" + languageCode + ".properties");
        return true;
    }

    public static void useDefaults() {
        useDefaults = true;
    }

    /**
     * Saves the keys and values of all provided {@link Translatable} to lang_default.properties, so a user can copy to their own file.
     *
     * @param file   File to save to
     * @param values All values to copy to file
     */
    private static void saveDefaults(File file, Translatable[] values) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(file, false))) {

            pw.println("# DO NOT CHANGE THE CONTENTS OF THIS FILE\n" +
                    "# To change messages, copy the contents of this file and rename it to \"lang_<code>.properties\",\n" +
                    "# and change the \"language\" config value to the code you\n" +
                    "# used in the file name. Example: lang_es.properties or lang_custom.properties\n");

            for (Translatable key : values) {
                if (key.getArgs().length > 0) {
                    pw.println("# Placeholders: " + String.join(", ", key.getArgs()));
                }
                else {
                    pw.println("# No placeholders");
                }
                pw.println(key.getKey() + "=" + key.getDefaultValue() + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*private void setPrefixOffset(String prefix) {
        if (isNotNullOrEmpty(prefix)) {
            StringBuilder prefixOffset = new StringBuilder(" ");
            String strippedPrefix = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', prefix));
            for (int i = 0; i < strippedPrefix.length(); i++) {
                prefixOffset.append(" ");
            }
            this.prefixOffset = prefixOffset.toString();
            return;
        }
        prefixOffset = "";
    }

    public String getPrefixOffset() {
        return prefixOffset;
    }*/

    /**
     * Retrieves message from either default values or loaded file.
     *
     * @param key    Key to use for loaded messages and value for default message.
     * @param prefix If returned message should include a prefix in the beginning
     * @param args   Strings to replace arguments of the {@link Translatable} with.
     *               Must be given in the same order as they're defined in the {@link Translatable}
     * @return Retrieved message, with colors already parsed and space before prefix
     */
    public static String get(Translatable key, boolean prefix, String... args) {
        StringBuilder builder = new StringBuilder();

        if (useDefaults) {
            if (prefix) {
                builder.append(ChatColor.translateAlternateColorCodes('&', prefixTranslatable.getDefaultValue())).append(" ");
            }
            builder.append(ChatColor.translateAlternateColorCodes('&', key.getDefaultValue()));

        }
        else {

            if (prefix && isNotNullOrEmpty(properties.getProperty("prefix"))) {
                builder.append(ChatColor.translateAlternateColorCodes('&', properties.getProperty("prefix"))).append(" ");
            }

            if (isNotNullOrEmpty(properties.getProperty(key.getKey()))) {
                builder.append(ChatColor.translateAlternateColorCodes('&', properties.getProperty(key.getKey())));
            }
            else {
                builder.append(ChatColor.translateAlternateColorCodes('&', key.getDefaultValue()));
            }
        }

        String message = builder.toString();
        // Replace placeholders
        if (key.getArgs().length > 0) {
            int index = 0;
            for (String arg : args) {
                message = StringUtils.replace(message, "{" + key.getArgs()[index] + "}", arg);
                index++;
            }
        }
        return message;
    }

    public static List<String> getMissingKeys() {
        return missingKeys;
    }

    private static boolean isNotNullOrEmpty(String string) {
        return string != null && !string.trim().isEmpty();
    }
}
