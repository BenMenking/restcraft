package net.menking.alter_vue.jsonplugin;

import java.io.IOException;
import net.menking.alter_vue.restHandler.RestHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author bmenking
 */
public class CraftRestPlugin extends JavaPlugin implements Listener {

    public static String header = "[CraftRestPlugin] ";
    private RestHandler server;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);

        try {
            this.server = new RestHandler((JavaPlugin) this);
            this.server.start();
        } catch (IOException e) {
            getServer().getLogger().severe(CraftRestPlugin.header + "Could not start HTTP server.  Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        this.server.stop();
    }

    @EventHandler
    public void OnPlayerDeathEvent(PlayerDeathEvent event) {
        getServer().getLogger().info("PlayerDeathEvent");
        this.server.fireEvent(event);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        getServer().getLogger().info("PlayerLoginEvent");
        this.server.fireEvent(event);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        getServer().getLogger().info("PlayerQuitEvent");
        this.server.fireEvent(event);    
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        getServer().getLogger().info("AsyncPlayerChatEvent");
        this.server.fireEvent(event);        
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //this.server.fireEvent(event);        
        return false;
    }
}
