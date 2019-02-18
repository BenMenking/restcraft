package net.menking.alter_vue.restHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.UUID;
import net.menking.alter_vue.gson.PlayerDeathEventSerializationHandler;
import net.menking.alter_vue.gson.PlayerJoinEventSerializationHandler;
import net.menking.alter_vue.gson.PlayerSerializationHandler;
import net.menking.alter_vue.gson.ServerSerializationHandler;
import net.menking.alter_vue.gson.WorldSerializationHandler;
import net.menking.alter_vue.gson.OfflinePlayerSerializationHandler;
import net.menking.alter_vue.gson.PlayerDeathEventSerializationHandler;
import net.menking.alter_vue.gson.PlayerJoinEventSerializationHandler;
import net.menking.alter_vue.gson.PlayerLoginEventSerializationHandler;
import net.menking.alter_vue.jsonplugin.CraftRestPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author bmenking
 */
public class RestHandler implements HttpHandler {

    private ArrayList<String> methods;
    private JavaPlugin instance = null;
    private HashMap<String, String> channelPlayerJoin = null;
    private HashMap<String, String> channelPlayerLeave = null;
    private HashMap<String, String> channelPlayerDeath = null;
    private HashMap<String, String> channelPlayerChat = null;
    private ArrayList<String> allowedIPs = null;
    private String access_path = "";
    private HttpServer server;
    private Gson gson;
    private int port;
    
    static final int PLAYER_JOIN = 0;
    static final int PLAYER_LEAVE = 1;
    static final int PLAYER_DEATH = 2;
    static final int PLAYER_CHAT = 3;
    static final int PLAYER_LOGIN = 4;

    public RestHandler(JavaPlugin inst) {
        this.methods = new ArrayList<String>();
        this.instance = inst;

        this.gson = new GsonBuilder()
                .registerTypeAdapter(Player.class, new PlayerSerializationHandler())
                .registerTypeAdapter(World.class, new WorldSerializationHandler())
                .registerTypeAdapter(Server.class, new ServerSerializationHandler())
                .registerTypeAdapter(OfflinePlayer.class, new OfflinePlayerSerializationHandler())
                .registerTypeAdapter(PlayerDeathEvent.class, new PlayerDeathEventSerializationHandler())
                .registerTypeAdapter(PlayerJoinEvent.class, new PlayerJoinEventSerializationHandler())
                .registerTypeAdapter(PlayerLoginEvent.class, new PlayerLoginEventSerializationHandler())
                .create();
        
        this.channelPlayerJoin = new HashMap<String, String>();
        this.channelPlayerLeave = new HashMap<String, String>();
        this.channelPlayerDeath = new HashMap<String, String>();
        this.channelPlayerChat = new HashMap<String, String>();
        
        // populate the allow list (regardless of whether it's actually used
        this.allowedIPs = new ArrayList<String>(this.instance.getConfig().getStringList("ip_restrict.allow"));
    }

    public void start() throws IOException {
        this.access_path = this.instance.getConfig().getString("access_prefix", "/api");
        this.port = this.instance.getConfig().getInt("port", 4567);

        InetSocketAddress inet = null;

        if (this.instance.getConfig().getBoolean("ip_restrict.only_local")) {
            inet = new InetSocketAddress(InetAddress.getLoopbackAddress(), this.port);
        } else {
            inet = new InetSocketAddress(this.port);
        }
        this.server = HttpServer.create(inet, 0);
        server.createContext(this.access_path, this);
        server.setExecutor(null);
        server.start();
    }

    public void stop() {
        this.server.stop(0);
    }

    public void clearSubscriptions() {
        this.methods.clear();
    }

    public String addSubscription(int channel, String callback) {
        String uuid = UUID.randomUUID().toString();
        
        switch(channel) {
            case RestHandler.PLAYER_CHAT:
                this.instance.getServer().getLogger().info("Adding subscription to CHAT");
                this.channelPlayerChat.put(uuid, callback);
                break;
            case RestHandler.PLAYER_DEATH:
                this.instance.getServer().getLogger().info("Adding subscription to DEATH");
                this.channelPlayerDeath.put(uuid, callback);
                break;
            case RestHandler.PLAYER_LEAVE:
                this.instance.getServer().getLogger().info("Adding subscription to LEAVE");
                this.channelPlayerLeave.put(uuid, callback);
                break;
            case RestHandler.PLAYER_JOIN:
            case RestHandler.PLAYER_LOGIN:
                this.instance.getServer().getLogger().info("Adding subscription to JOIN/LOGIN");
                this.channelPlayerJoin.put(uuid, callback);
                break;
            default:
                break;
        }
        
        return uuid;
    }
    
    public void removeSubscription(String token) {
        if( this.channelPlayerChat.containsKey(token) ) {
            this.channelPlayerChat.remove(token);
        }
        if( this.channelPlayerLeave.containsKey(token) ) {
            this.channelPlayerLeave.remove(token);
        }
        if( this.channelPlayerJoin.containsKey(token) ) {
            this.channelPlayerJoin.remove(token);
        }
        if( this.channelPlayerDeath.containsKey(token) ) {
            this.channelPlayerDeath.remove(token);
        }
    }

    public void fireEvent(Event event) {
        this.instance.getServer().getLogger().info("event instanceof " + event.getClass().getName());
        
        if( event instanceof PlayerDeathEvent ) {
            for( String uri : this.channelPlayerDeath.values() ) {
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                }
                catch( MalformedURLException e) {
                    this.instance.getServer().getLogger().warning("Attempting to send to callback: " + e.getMessage());
                }
                catch( IOException e ) {
                    this.instance.getServer().getLogger().warning("Attempting to send to callback: " + e.getMessage());
                }
            }
        }
        else if( event instanceof PlayerQuitEvent ) {
            for( String uri : this.channelPlayerLeave.values() ) {
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                }
                catch( MalformedURLException e) {
                    this.instance.getServer().getLogger().warning("Attempting to send to callback: " + e.getMessage());
                }
                catch( IOException e ) {
                    this.instance.getServer().getLogger().warning("Attempting to send to callback: " + e.getMessage());
                }
            }
            
        }
        else if( event instanceof AsyncPlayerChatEvent ) {
            for( String uri : this.channelPlayerChat.values() ) {
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                }
                catch( MalformedURLException e) {
                    this.instance.getServer().getLogger().warning("Attempting to send to callback: " + e.getMessage());
                }
                catch( IOException e ) {
                    this.instance.getServer().getLogger().warning("Attempting to send to callback: " + e.getMessage());
                }
            }
            
        }
        else if( event instanceof PlayerJoinEvent ) {
            for( String uri : this.channelPlayerJoin.values() ) {
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                }
                catch( MalformedURLException e) {
                    this.instance.getServer().getLogger().warning("Attempting to send to callback: " + e.getMessage());
                }
                catch( IOException e ) {
                    this.instance.getServer().getLogger().warning("Attempting to send to callback: " + e.getMessage());
                }
            }
            
        }
        else if( event instanceof PlayerLoginEvent ) {
            for( String uri : this.channelPlayerJoin.values() ) {
                this.instance.getServer().getLogger().info("Sending PlayerLoginEvent to " + uri);
                
                try {
                    JsonArray arr = new JsonArray();
                    arr.add(this.gson.toJsonTree(event, PlayerLoginEvent.class));
                    
                    String urlParameters  = arr.toString();
                    this.instance.getServer().getLogger().info("JSON: " + urlParameters);
                    byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
                    int    postDataLength = postData.length;
                    String request        = uri;
                    URL    url            = new URL( request );
                    HttpURLConnection conn= (HttpURLConnection) url.openConnection();           
                    conn.setDoOutput( true );
                    conn.setInstanceFollowRedirects( false );
                    conn.setRequestMethod( "POST" );
                    conn.setRequestProperty( "Content-Type", "application/json"); 
                    conn.setRequestProperty( "charset", "utf-8");
                    conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
                    conn.setUseCaches( false );
                    try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
                       wr.write( postData );
                    } 
                    conn.disconnect();
                    this.instance.getServer().getLogger().info("Done sending PlayerLoginEvent to " + uri);
                }
                catch( MalformedURLException e) {
                    this.instance.getServer().getLogger().warning("Attempting to send to callback: " + e.getMessage());
                }
                catch( IOException e ) {
                    this.instance.getServer().getLogger().warning("Attempting to send to callback: " + e.getMessage());
                }
            }
            
        }
        else {
            this.instance.getServer().getLogger().warning("Received event class " + event.getClass().getName() + " but ignoring");
        }
    }
    
    @Override
    public void handle(HttpExchange t) throws IOException {
        /*
         * /players - returns a list of all online players
         * /player/{uuid} - returns informatino about the online player
         * /server - returns server information
         */
        this.instance.getServer().getLogger().info(CraftRestPlugin.header + "Received HTTP request: " + t.getRequestURI().getPath()
                + " from " + t.getRemoteAddress().getHostString() + " to " + t.getLocalAddress().getHostString());

        JsonObject result = new JsonObject();
        int responseCode = 200;

        // check with the ip_restriction list first
        if( !this.instance.getConfig().getBoolean("ip_restrict.only_local") && !this.allowedIPs.contains(t.getRemoteAddress().getHostString())) {
            result.add("error", new JsonPrimitive("IP Restriction.  Cannot talk with " + t.getRemoteAddress().getHostString()));
            responseCode = 500;
        } 
        else if (t.getRequestURI().getPath().equalsIgnoreCase(this.access_path + "/players")) {
            JsonArray arr = new JsonArray();
            for (Player p : this.instance.getServer().getOnlinePlayers()) {
                arr.add(this.gson.toJsonTree(p, Player.class));
            }
            result.add("players", arr);
        } else if (t.getRequestURI().getPath().equalsIgnoreCase(this.access_path + "/worlds")) {
            JsonArray arr = new JsonArray();
            for (World w : this.instance.getServer().getWorlds()) {
                arr.add(this.gson.toJsonTree(w, World.class));
            }
            result.add("worlds", arr);
        } else if (t.getRequestURI().getPath().matches(".*/server")) {
            JsonArray arr = new JsonArray();
            arr.add(this.gson.toJsonTree(this.instance.getServer(), Server.class));
            result.add("server", arr);
        } else if (t.getRequestURI().getPath().matches(".*/player/(.*)")) {
            this.instance.getServer().getLogger().info("Matches on '" + t.getRequestURI().getPath() + "'");
            Pattern pattern = Pattern.compile(".*/player/(.*)");
            Matcher matcher = pattern.matcher(t.getRequestURI().getPath());

            String uuid = matcher.group(1);

            OfflinePlayer p = this.instance.getServer().getOfflinePlayer(uuid);
            
            JsonArray arr = new JsonArray();
            arr.add(this.gson.toJsonTree(p, OfflinePlayer.class));
            result.add("player", arr);
        } else if (t.getRequestURI().getPath().matches(".*/subscribe/(.*)/(.*)")) {
            this.instance.getServer().getLogger().info("matched on subscribe");
            Pattern pattern = Pattern.compile(".*/subscribe/([^/]+)/(.*)");
            this.instance.getServer().getLogger().info("matched on subscribe2");
            Matcher matcher = pattern.matcher(t.getRequestURI().getPath());
            this.instance.getServer().getLogger().info("matched on subscribe3 = " + Integer.toString(matcher.groupCount()));
            
            String channel = "";
            String callback = "";
            while( matcher.find() ) {
                channel = matcher.group(1);
                callback = matcher.group(2);
            }

            this.instance.getServer().getLogger().info("subscription request: " + channel + " @ " + callback);
            
            if( channel.equalsIgnoreCase("player_death") ) {
                String token = this.addSubscription(RestHandler.PLAYER_DEATH, callback);
                result.add("token", new JsonPrimitive(token));
            }
            else if( channel.equalsIgnoreCase("player_join") || channel.equalsIgnoreCase("player_login" ) ) {
                String token = this.addSubscription(RestHandler.PLAYER_JOIN, callback); 
                result.add("token", new JsonPrimitive(token));
            }
            else if( channel.equalsIgnoreCase("player_leave") ) {
                String token = this.addSubscription(RestHandler.PLAYER_LEAVE, callback); 
                result.add("token", new JsonPrimitive(token));
            }
            else if( channel.equalsIgnoreCase("player_chat") ) {
                String token = this.addSubscription(RestHandler.PLAYER_CHAT, callback); 
                result.add("token", new JsonPrimitive(token));
            }            
        } else {
            result.add("error", new JsonPrimitive("Bad Request - that method is not available"));
            responseCode = 400;
        }

        String json = result.toString();

        t.sendResponseHeaders(responseCode, json.getBytes().length);
        OutputStream os = t.getResponseBody();
        os.write(json.getBytes());
        os.close();
    }

    @Override
    public String toString() {
        return "started on port " + Integer.toString(this.port) + " at path " + this.access_path;
    }
}
