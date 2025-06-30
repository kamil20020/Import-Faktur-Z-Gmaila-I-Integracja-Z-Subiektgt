package org.example.template.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateCreatorTest {

    @ParameterizedTest
    @CsvSource({
        "Firma123, Wrocław",
        "Firma 123, Ścinawka średnia",
    })
    void shouldExtractWithoutSkipSpace(String expectedName, String expectedCity) {

        //given
        String expectedStreet = "Adres 123";
        String expectedPostCode = "12-345";

        String expectedNip = "1234";
        String nipInput = "NIP: " + expectedNip;

        String[] words = new String[]{expectedName, expectedStreet, expectedPostCode + " " + expectedCity, nipInput};

        Map<String, Integer> fieldsSkipSpaceMappings = new HashMap<>();

        fieldsSkipSpaceMappings.put("nip", 0);

        //when
        TemplateCreator templateCreator = TemplateCreator.extract(words, fieldsSkipSpaceMappings, 5);

        //then
        assertNotNull(templateCreator);
        assertEquals(expectedName, templateCreator.name());
        assertEquals(expectedStreet, templateCreator.street());
        assertEquals(expectedPostCode, templateCreator.postCode());
        assertEquals(expectedCity, templateCreator.city());
        assertEquals(expectedNip, templateCreator.nip());
    }

    @ParameterizedTest
    @CsvSource({
        "Firma123, Wrocław",
        "Firma 123, Ścinawka średnia",
    })
    void shouldExtractWithSkipSpace(String expectedName, String expectedCity) {

        //given
        String expectedStreet = "Adres 123";
        String expectedPostCode = "12-345";

        String expectedNip = "1234";
        String nipInput = "NIP: " + expectedNip;

        String[] words = new String[]{expectedName, expectedStreet, expectedPostCode + " " + expectedCity, "Tel: 123", nipInput};

        Map<String, Integer> fieldsSkipSpaceMappings = new HashMap<>();

        fieldsSkipSpaceMappings.put("nip", 1);

        //when
        TemplateCreator templateCreator = TemplateCreator.extract(words, fieldsSkipSpaceMappings, 6);

        //then
        assertNotNull(templateCreator);
        assertEquals(expectedName, templateCreator.name());
        assertEquals(expectedStreet, templateCreator.street());
        assertEquals(expectedPostCode, templateCreator.postCode());
        assertEquals(expectedCity, templateCreator.city());
        assertEquals(expectedNip, templateCreator.nip());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "Firma123",
        "Firma 123"
    })
    void shouldExtractCreatorName(String expectedProductName) {

        //given
        String[] words = new String[]{expectedProductName, "Adres 123", "12-345 Wrocław", "1234"};

        //when
        String gotProductName = TemplateCreator.extractCreatorName(words, 5);

        //then
        assertNotNull(gotProductName);
        assertEquals(expectedProductName, gotProductName);
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

    @ParameterizedTest
    @CsvSource(value = {
        "Wrocław",
        "Ścinawka średnia"
    })
    void shouldExtractCreatorCity(String expectedCity) {

        //given
        String[] words = new String[]{"Firma 123", "Adres 123", "12-345 " + expectedCity, "1234"};

        //when
        String gotCity = TemplateCreator.extractCreatorCity(words, 2);

        //then
        assertNotNull(gotCity);
        assertEquals(expectedCity, gotCity);
    }

    @Test
    void shouldExtractCreatorPostCode() {

        //given
        String expectedPostCode = "12-345";
        String postCodeInput = " " + expectedPostCode + " ";

        String[] words = new String[]{"Firma 123", "Adres 123", postCodeInput + " Wrocław", "1234"};

        //when
        String gotPostCode = TemplateCreator.extractCreatorPostCode(words, 2);

        //then
        assertNotNull(gotPostCode);
        assertEquals(expectedPostCode, gotPostCode);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "NIP: 1234, 1234",
        " NIP: 12345 , 12345",
        "NIP: PL12346, 12346",
        " NIP: PL12342 , 12342",
        " NIP: PL 12346 , 12346",
    })
    void shouldExtractCreatorNipWithoutSkipSpace(String nipInput, String expectedNip) {

        //given
        String[] words = new String[]{"Firma 123", "Adres 123", "12-345 Wrocław", nipInput};

        //when
        String gotNip = TemplateCreator.extractCreatorNip(words, 3, 0);

        //then
        assertNotNull(gotNip);
        assertEquals(expectedNip, gotNip);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "NIP: 1234, 1234",
        " NIP: 12345 , 12345",
        "NIP: PL12346, 12346",
        " NIP: PL12342 , 12342",
        " NIP: PL 12346 , 12346",
    })
    void shouldExtractCreatorNipWithSkipSpace(String nipInput, String expectedNip) {

        //given
        String[] words = new String[]{"Firma 123", "Adres 123", "12-345 Wrocław", "Tel: 1234", nipInput};

        //when
        String gotNip = TemplateCreator.extractCreatorNip(words, 3, 1);

        //then
        assertNotNull(gotNip);
        assertEquals(expectedNip, gotNip);
    }

}