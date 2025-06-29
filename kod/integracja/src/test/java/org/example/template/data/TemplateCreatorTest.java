package org.example.template.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplateCreatorTest {

    @Test
    void shouldExtract() {


    }

    @Test
    void shouldExtractCreatorName() {


    }

    @Test
    void shouldExtractCreatorStreet() {

        //given
        String expectedStreet = "Adres 123";

        String[] words = new String[]{"Firma 123", expectedStreet, "12-345 Wrocław", "1234"};

        //when
        String gotStreet = TemplateCreator.extractCreatorStreet(words, 1);

        //then
        assertNotNull(gotStreet);
        assertEquals(expectedStreet, gotStreet);
    }

    @Test
    void shouldExtractCreatorCity() {

        throw new IllegalStateException("Not tested yet");
    }

    @Test
    void shouldExtractCreatorPostCode() {


    }

    @Test
    void shouldExtractCreatorNip() {


    }

}