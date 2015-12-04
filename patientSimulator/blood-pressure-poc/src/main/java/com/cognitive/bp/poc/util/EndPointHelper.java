/* 
 * Copyright 2015 Cognitive Medical Systems, Inc (http://www.cognitivemedicine.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
