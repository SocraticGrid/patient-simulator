/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.jcpsimsimulator.config.database;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 *
 * @author esteban
 */
public class DatabaseConfiguration {
    
    public final static String URL_KEY = "hibernate.connection.url";
    public final static String USERNAME_KEY = "hibernate.connection.username";
    public final static String PASSWORD_KEY = "hibernate.connection.password";
    public final static String DIALECT_KEY = "hibernate.dialect";
    
    private static DatabaseConfiguration INSTANCE = new DatabaseConfiguration();

    private DatabaseConfiguration() {
    
    }
    
    public static DatabaseConfiguration getInstance(){
        return INSTANCE;
    }
    
    public Map<String,String> getConfiguration(){
        Map<String, String> configuration = new HashMap<String, String>();
        
        configuration.put(URL_KEY, Preferences.userNodeForPackage(DatabasePanel.class).get(DatabasePanel.URL, DatabasePanel.URL_DEFAULT));
        configuration.put(USERNAME_KEY, Preferences.userNodeForPackage(DatabasePanel.class).get(DatabasePanel.USERNAME, DatabasePanel.USERNAME_DEFAULT));
        configuration.put(PASSWORD_KEY, Preferences.userNodeForPackage(DatabasePanel.class).get(DatabasePanel.PASSWORD, DatabasePanel.PASSWORD_DEFAULT));
        configuration.put(DIALECT_KEY, Preferences.userNodeForPackage(DatabasePanel.class).get(DatabasePanel.DIALECT, DatabasePanel.DIALECT_DEFAULT));
        
        return configuration;
    }
    
}
