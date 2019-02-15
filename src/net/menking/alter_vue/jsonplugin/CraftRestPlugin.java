/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.menking.alter_vue.jsonplugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.menking.alter_vue.gson.PlayerSerializationHandler;
import net.menking.alter_vue.gson.WorldSerializationHandler;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author bmenking
 */
public class CraftRestPlugin extends JavaPlugin implements Listener, HttpHandler {
    private HttpServer server;
    private Gson gson;
    private static String header = "[CraftRestPlugin] ";
    private ArrayList<String> apiSubscriptions = null;
    
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this,  this);
        
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Player.class, new PlayerSerializationHandler())
                .registerTypeAdapter(World.class, new WorldSerializationHandler())
                .create();
        
        try {
            this.server = HttpServer.create(new InetSocketAddress(getConfig().getInt("port", 4567)), 0);
            server.createContext(getConfig().getString("access_prefix", "api"), this);
            server.setExecutor(null);
            server.start();
        }
        catch( IOException e ) {
            getServer().getLogger().severe(CraftRestPlugin.header + "Could not start HTTP server.  Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }
        
        this.apiSubscriptions = new ArrayList<String>();
    }

    @Override
    public void onDisable() {
        this.server.stop(0);
    }
    
    @EventHandler
    public void OnPlayerDeathEvent(PlayerDeathEvent event) {
        for( String callback : this.apiSubscriptions ) {
            
        }
    }
    
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        for( String callback : this.apiSubscriptions ) {
            
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event ) {
        for( String callback : this.apiSubscriptions ) {
            
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { 
        for( String callback : this.apiSubscriptions ) {
            
        }
        return false; 
    }
  
    @Override
    public void handle(HttpExchange t) throws IOException {
        /*
         * /players - returns a list of all online players
         * /player/{uuid} - returns informatino about the online player
         * /server - returns server information
         */
        getServer().getLogger().info(CraftRestPlugin.header + "Received HTTP request: " + t.getRequestURI().getPath()
                + " from " + t.getRemoteAddress().getHostString() + " to " + t.getLocalAddress().getHostString());
        
        JsonObject result = new JsonObject();
        int responseCode = 200;
        
        if( !t.getLocalAddress().getHostString().equalsIgnoreCase("127.0.0.1") ) {
            result.add("error", new JsonPrimitive("IP Restriction.  Cannot respond."));
            responseCode = 500;
        }
        else if( t.getRequestURI().getPath().equalsIgnoreCase(getConfig().getString("access_prefix", "api") + "/players") ) {
            JsonArray arr = new JsonArray();
            for(Player p : getServer().getOnlinePlayers() ) {
                arr.add(this.gson.toJsonTree(p, Player.class));
            }
            result.add("players", arr);
        }
        else if( t.getRequestURI().getPath().equalsIgnoreCase(getConfig().getString("access_prefix", "api") + "/worlds") ) {
            JsonArray arr = new JsonArray();
            for(World w : getServer().getWorlds() ) {
                arr.add(this.gson.toJsonTree(w, World.class));
            }
            result.add("worlds", arr);
        }
        else if( t.getRequestURI().getPath().matches(".*/player/(.*)") ) {
            getServer().getLogger().info("Matches on '" + t.getRequestURI().getPath() + "'");
            Pattern pattern = Pattern.compile(".*/player/(.*)");
            Matcher matcher = pattern.matcher(t.getRequestURI().getPath());
            
            String uuid = matcher.group(1);
            
            OfflinePlayer p = getServer().getOfflinePlayer(uuid);
            
        }        
        else if(  t.getRequestURI().getPath().equalsIgnoreCase(getConfig().getString("access_prefix", "api") + "/subscribe") ) {
            
        }
        else {
            result.add("error", new JsonPrimitive("Bad Request - that method is not available"));
            responseCode = 400;
        }
        
        String json = result.toString();
        
        t.sendResponseHeaders(responseCode, json.getBytes().length);
        OutputStream os = t.getResponseBody();
        os.write(json.getBytes());
        os.close();
    }    
}
