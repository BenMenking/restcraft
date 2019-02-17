package net.menking.alter_vue.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.bukkit.Server;
import org.bukkit.entity.Player;

/**
 *
 * @author bmenking
 */
public class ServerSerializationHandler implements JsonSerializer<Server>, JsonDeserializer<Server> { 
    public JsonElement serialize(Server src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("max_players", new JsonPrimitive(src.getMaxPlayers()));
        result.add("default_gamemode", new JsonPrimitive(src.getDefaultGameMode().toString()));
        result.add("connection_throttle", new JsonPrimitive(src.getConnectionThrottle()));
        result.add("bukkit_version", new JsonPrimitive(src.getBukkitVersion()));
        result.add("motd", new JsonPrimitive(src.getMotd()));
        result.add("name", new JsonPrimitive(src.getName()));
        result.add("server_id", new JsonPrimitive(src.getServerId()));
        result.add("server_port", new JsonPrimitive(src.getPort()));
        result.add("server_name", new JsonPrimitive(src.getServerName()));
        result.add("shutdown_message", new JsonPrimitive(src.getShutdownMessage()));
        return result;
    }
    
    public Server deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return null;
    }
    
}
