package com.embersyndicate.votecounter;

import com.embersyndicate.votecounter.config.ConfigHandler;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;

@Plugin(
        id = "votevounter",
        name = "Vote Counter",
        version =  "1.0.0SNAPSHOT",
        description = "A Vote Counter plugin to give set rewards at x amount of votes.",
        dependencies = @Dependency(id = "nuvotifier")
)

public class VoteCounter {

    @Inject private Game game;
    private Game getGame() { return this.game; }

    @Inject
    private PluginContainer plugin;
    public PluginContainer getPlugin() { return this.plugin; }

    public static VoteCounter getInstance() { return instance; }

    private  static VoteCounter instance;

    @Inject private Logger logger;
    public static Logger  getLogger() { return getInstance().logger; }

    @DefaultConfig(sharedRoot = true)
    private File defaultCfgDir;

    @Listener
    public void onInit(GameInitializationEvent event) {
        instance = this;
        File rootDir = new File(defaultCfgDir, "votecounter");
        ConfigHandler.init(rootDir);
        //CommandList.RegisterCommands();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("Vote Counter is running on version " + "@VERSION@" + ".");
        ConfigHandler.load();
        ConfigHandler.loadValues();

    }
}
