package org.elton.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.elton.plugin.Commands.GiveMarioItemCommand;
import org.elton.plugin.Listeners.*;
import org.elton.plugin.MyUtils.MyUtils;

/**
 * Main plugin
 */
public final class TheMarioPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        //creates static plugin ref
        MyUtils.onEnable(this);
        //Register commands
        registerCommands();
        //registers Listeners
        registerListeners();

        //Sends out a startup message
        this.getLogger().info("Enabled The Mario Plugin. Let's-a-go");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        //Sends out a shutdown message
        this.getLogger().info("Disabled The Mario Plugin. Bye Bye!");
    }

    /**
     * Rgisters the required commands for this plugin
     */
    private void registerCommands() {
        getCommand("givemario").setExecutor(new GiveMarioItemCommand(this));
    }

    /**
     * Registers required listeners for this plugin
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new SprintListener(this), this);
        getServer().getPluginManager().registerEvents(new StatisticListener(), this);
        getServer().getPluginManager().registerEvents(new JumpListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemPickupListener(this), this);
        getServer().getPluginManager().registerEvents(new PowerupListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new DamageListener(this), this);
    }


}
