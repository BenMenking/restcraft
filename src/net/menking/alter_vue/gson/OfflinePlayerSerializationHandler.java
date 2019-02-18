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
import org.bukkit.OfflinePlayer;

/**
 *
 * @author bmenking
 */
public class OfflinePlayerSerializationHandler implements JsonSerializer<OfflinePlayer>, JsonDeserializer<OfflinePlayer> {
    public JsonElement serialize(OfflinePlayer src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("bed_spawn_location", new JsonPrimitive(src.getBedSpawnLocation().toString()));
        result.add("first_played", new JsonPrimitive(src.getFirstPlayed()));
        result.add("last_played", new JsonPrimitive(src.getLastPlayed()));
        result.add("name", new JsonPrimitive(src.getName()));
        result.add("uuid", new JsonPrimitive(src.getUniqueId().toString()));
        result.add("played_before", new JsonPrimitive(src.hasPlayedBefore()));
        result.add("banned", new JsonPrimitive(src.isBanned()));
        result.add("online", new JsonPrimitive(src.isOnline()));
        result.add("whitelisted", new JsonPrimitive(src.isWhitelisted()));
        result.add("op", new JsonPrimitive(src.isOp()));
        return result;
    }
    
    public OfflinePlayer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return null;
    }

    
}
