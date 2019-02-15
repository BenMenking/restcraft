package net.menking.alter_vue.restHandler;

import java.util.ArrayList;

/**
 *
 * @author bmenking
 */
public class RestHandler {
    private ArrayList<String> methods;
    
    public void RestHandler() {
        this.methods = new ArrayList<String>();
    }
    
    public void clear() {
        this.methods.clear();
    }
    
    public void addMethod(String pathRegex) {
        
    }
}
