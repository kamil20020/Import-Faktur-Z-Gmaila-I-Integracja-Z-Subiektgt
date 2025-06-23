package org.example.service;

import com.sun.jna.platform.unix.solaris.LibKstat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesService {

    private static final Properties properties = new Properties();

    private static final String PROPERTIES_FILE_NAME = "application.properties";

    private PropertiesService(){


    }

    public static void init(){

        InputStream inputStream = PropertiesService.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);

        try{

            properties.load(inputStream);
        }
        catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static  <T> T getProperty(String propertyName, Class<T> type){

        return (T) getProperty(propertyName);
    }

    public static String getProperty(String propertyName){

        return properties.getProperty(propertyName);
    }

    public static Properties getProperties(){

        return properties;
    }
}
