/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.menking.alter_vue.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author bmenking
 */
public class WorldSerializationHandler implements JsonSerializer<World>, JsonDeserializer<World>{
    public JsonElement serialize(World src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("name", new JsonPrimitive(src.getName()));
        result.add("allow_animals", new JsonPrimitive(src.getAllowAnimals()));
        result.add("allow_monsters", new JsonPrimitive(src.getAllowMonsters()));
        result.add("seed", new JsonPrimitive(src.getSeed()));
        result.add("difficulty", new JsonPrimitive(src.getDifficulty().toString()));
        result.add("environment", new JsonPrimitive(src.getEnvironment().toString()));
        result.add("time", new JsonPrimitive(src.getFullTime()));
        JsonArray players = new JsonArray();
        for(Player p : src.getPlayers() ) {
            players.add(new JsonPrimitive(p.getUniqueId().toString()));
        }
        result.add("players", players);
        return result;
    }
    
    public World deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return null;
    }

}
