/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import org.bukkit.entity.Player;

/**
 *
 * @author bmenking
 */
public class PlayerSerializationHandler implements JsonSerializer<Player>, JsonDeserializer<Player> {
     public JsonElement serialize(Player src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("uuid", new JsonPrimitive(src.getUniqueId().toString()));
        result.add("name", new JsonPrimitive(src.getDisplayName()));
        result.add("host", new JsonPrimitive(src.getAddress().getHostString()));
        result.add("port", new JsonPrimitive(src.getAddress().getPort()));
        result.add("exhaustion", new JsonPrimitive(src.getExhaustion()));
        result.add("exp_to_next_level", new JsonPrimitive(src.getExp()));
        result.add("food_level", new JsonPrimitive(src.getFoodLevel()));
        result.add("health_scale", new JsonPrimitive(src.getHealthScale()));
        result.add("health", new JsonPrimitive(src.getHealth()));
        result.add("total_exp", new JsonPrimitive(src.getTotalExperience()));
        result.add("exp_level", new JsonPrimitive(src.getLevel()));

        return result;
    }
    
    public Player deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return null;
    }
}
