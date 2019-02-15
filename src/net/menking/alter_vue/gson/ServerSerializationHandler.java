/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.menking.alter_vue.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import org.bukkit.Server;

/**
 *
 * @author bmenking
 */
public class ServerSerializationHandler {
    public JsonElement serialize(Server src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
//        result.add("op", new JsonPrimitive(src.isOp()));
        return result;
    }
    
    public Server deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return null;
    }
    
}
