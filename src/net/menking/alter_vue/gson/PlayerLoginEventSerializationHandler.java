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
import org.bukkit.event.player.PlayerLoginEvent;

/**
 *
 * @author bmenking
 */
public class PlayerLoginEventSerializationHandler implements JsonSerializer<PlayerLoginEvent>, JsonDeserializer<PlayerLoginEvent> {

    @Override
    public JsonElement serialize(PlayerLoginEvent src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("uuid", new JsonPrimitive(src.getPlayer().getUniqueId().toString()));
        result.add("name", new JsonPrimitive(src.getPlayer().getDisplayName()));
        //result.add("host", new JsonPrimitive(src.getPlayer().getAddress().getHostString()));
        //result.add("port", new JsonPrimitive(src.getPlayer().getAddress().getPort()));
        return result;
    }

    @Override
    public PlayerLoginEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
