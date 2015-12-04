/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.bp.poc.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 *
 * @author esteban
 */
public class EndPointHelper {
    public static final String DEF_SRC = "/META-INF/service.endpoint.properties";

    private static Properties properties = null;
    
    public synchronized static String getProperty( String key ) {
        if (properties == null){
            properties = new Properties();
            try {
                InputStream in = EndPointHelper.class.getResourceAsStream( DEF_SRC );
                if ( in != null ) {
                    properties.load( in );
                }
            } catch (IOException e) {
                throw new IllegalStateException("Error reading '"+DEF_SRC+"' file.", e);
            }
        }
        return (String) properties.get( key );
    }
    
    public synchronized static int getPropertyAsInt( String key ) {
        return Integer.parseInt(getProperty(key));
    }

    public synchronized static URL getEndPointURL( String key ) {
        try {
            return new URL( getProperty( key ) );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
