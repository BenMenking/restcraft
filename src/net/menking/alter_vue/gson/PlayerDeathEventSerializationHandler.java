/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 *
 * @author bmenking
 */
public class PlayerDeathEventSerializationHandler implements JsonSerializer<PlayerDeathEvent>, JsonDeserializer<PlayerDeathEvent>{

    @Override
    public JsonElement serialize(PlayerDeathEvent src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("death_message", new JsonPrimitive(src.getDeathMessage()));
        result.add("keep_inventory", new JsonPrimitive(src.getKeepInventory()));
        result.add("keep_level", new JsonPrimitive(src.getKeepInventory()));
        result.add("uuid", new JsonPrimitive(src.getEntity().getUniqueId().toString()));
        result.add("name", new JsonPrimitive(src.getEntity().getDisplayName()));
        result.add("host", new JsonPrimitive(src.getEntity().getAddress().getHostString()));
        result.add("port", new JsonPrimitive(src.getEntity().getAddress().getPort()));
        return result;
    }

    @Override
    public PlayerDeathEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
