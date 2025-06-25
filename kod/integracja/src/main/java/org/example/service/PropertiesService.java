package org.example.service;

import com.sun.jna.platform.unix.solaris.LibKstat;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Service
public class PropertiesService {

    private final Properties properties = new Properties();

    private static final String PROPERTIES_FILE_NAME = "application.properties";

    private PropertiesService(){

        InputStream inputStream = PropertiesService.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);

        try{

            properties.load(inputStream);
        }
        catch (IOException e) {

            e.printStackTrace();
        }
    }

    public  <T> T getProperty(String propertyName, Class<T> type){

        return (T) getProperty(propertyName);
    }

    public String getProperty(String propertyName){

        return properties.getProperty(propertyName);
    }

    public Properties getProperties(){

        return properties;
    }

}
