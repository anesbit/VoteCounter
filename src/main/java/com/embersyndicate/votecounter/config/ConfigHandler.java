package com.embersyndicate.votecounter.config;

import com.embersyndicate.votecounter.VoteCounter;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigHandler {
    private static File configFile;
    private static ConfigurationLoader<CommentedConfigurationNode> configManager;
    private static CommentedConfigurationNode config;

    // Add Variables for config file.
    public static List<String> Aliases = new ArrayList<>();

    public static String dbType = null;
    public static String host = null;
    public static String user = null;
    public static String pass = null;
    public static String db = null;
    public static String dbPrefix = null;
    public static String pluginPrefix = null;

    public static void init(File rootDir) {
        configFile = new File(rootDir, "config.conf");
        configManager = HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();
    }

    public static void load() {
        load(null);
    }

    public static void load(CommandSource src) {
        // load file
        try {
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                config = configManager.load();
                configManager.save(config);
            }
            config = configManager.load();
        } catch (IOException e) {
            VoteCounter.getLogger().error("Failed to load or create config file");
            e.printStackTrace();
            if (src != null) {
                src.sendMessage(Text.of(TextColors.RED, "Failed to load or create config file"));
            }
        }

        // We need to make sure that nothing is using the wrong type!

        Utils.ensureListAlias(config.getNode("Aliases"), Arrays.asList("market", "sm"));
        Utils.ensureString(config.getNode("Database", "dbType"), "H2");
        Utils.ensureString(config.getNode("Database", "Host"), "localhost");
        Utils.ensureString(config.getNode("Database", "Username"), "root");
        Utils.ensureString(config.getNode("Database", "Password"), "");
        Utils.ensureString(config.getNode("Database", "DB Name"), "smarket");
        Utils.ensureString(config.getNode("Database", "DB Prefix"), "sm_");
        Utils.ensureString(config.getNode("Messages", "Plugin Prefix"), "market");

        //Set the comment in the config
        config.getNode("Aliases").setComment("The aliases the plugin uses for commands. The first value is always displayed when running the base command!" + "\n" + "Requires server restart to change!");
        config.getNode("Database", "dbType").setComment("This is the type of DB your using. Available options H@, MySQL (Default: H2)");
        config.getNode("Database", "Host").setComment("This should be an IP, hostname, or localhost. (Default: localhost)");
        config.getNode("Database", "Username").setComment("This is the username for the database. If H2 Leave at default. (Default: root)");
        config.getNode("Database", "Password").setComment("Password for the database. (Default: '')");
        config.getNode("Database", "DB Name").setComment("Name of the DB. (Default: market");
        config.getNode("Database", "DB Prefix").setComment("Table Prefix for DB (Default: sm_)");
        config.getNode("messages", "Plugin Prefix").setComment("Prefix for chat commands (Default: market)");
        save();
    }

    public static void save() {
        try {
            configManager.save(config);
        } catch (IOException e) {
            VoteCounter.getLogger().error("Could not save or load config file !");
        }
    }

    public static void loadValues() {
        try {

            Aliases = getNode("Aliases").getList(TypeToken.of(String.class));
            dbType = getNode("Database", "dbType").getString();
            host = getNode("Database", "Host").getString();
            user = getNode("Database", "Username").getString();
            pass = getNode("Database", "PAssword").getString();
            db = getNode("Database", "DB Name").getString();
            dbPrefix = getNode("Database", "DB Prefix").getString();
            pluginPrefix = getNode("Messages", "Plugin Prefix").getString();


        } catch (Exception e) {
            VoteCounter.getLogger().error("Failed to load config values to variables!");
            e.printStackTrace();
        }
    }

    public static CommentedConfigurationNode getNode(Object... path) {
        return config.getNode(path);
    }

    static class Utils {

        public static void ensureString(CommentedConfigurationNode node, String def) {
            if (node.getString() == null) {
                node.setValue(def);
            }
        }

        public static void ensureListAlias(CommentedConfigurationNode node, List<String> def) {
            if (!(node.getValue() instanceof List)) {
                node.setValue(def);
            }
            try {
                if (node.getList(TypeToken.of(String.class)).size() == 0) {
                    node.setValue(def);
                }
            } catch (ObjectMappingException e) {
                e.printStackTrace();
            }
        }
    }
}