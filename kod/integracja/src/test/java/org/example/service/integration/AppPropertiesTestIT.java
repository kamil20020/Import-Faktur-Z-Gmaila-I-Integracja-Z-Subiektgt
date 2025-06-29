package org.example.service.integration;

import org.example.TestUtils;
import org.example.service.PropertiesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class AppPropertiesTestIT {

    private PropertiesService propertiesService;

    @BeforeEach
    public void setUp(){

        propertiesService = new PropertiesService();
    }

    @Test
    void shouldLoadProperties(){

        //given
        String expectedProperty = "gmail.googleapis.com";

        //when
        Properties gotProperties = TestUtils.getPrivateInstanceField(PropertiesService.class, "properties", propertiesService, Properties.class);

        String gotProperty = gotProperties.getProperty("gmail.mail.host");

        //then
        assertEquals(expectedProperty, gotProperty);
    }

    @Test
    void shouldGetProperty() {

        String expectedPropertyKey = "gmail.mail.host";

        String expectedProperty = "gmail.googleapis.com";

        String gotProperty = propertiesService.getProperty(expectedPropertyKey, String.class);

        assertEquals(expectedProperty, gotProperty);
    }

    @Test
    void shouldGetPropertyStr() {

        String expectedPropertyKey = "gmail.mail.host";

        String expectedProperty = "gmail.googleapis.com";

        String gotProperty = propertiesService.getProperty(expectedPropertyKey);

        assertEquals(expectedProperty, gotProperty);
    }

}