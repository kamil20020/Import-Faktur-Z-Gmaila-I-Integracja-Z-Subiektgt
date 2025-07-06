package org.example.template.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateCreatorTest {

    @Test
    void shouldExtractW() {

        //given
        Map<String, String> templateRowFieldsValuesMappings = new HashMap<>();

        String expectedName = "Name 123";
        String expectedStreet = "Street 12";
        String expectedCity = "Miasto jakieś";
        String expectedPostCode = "12-345";
        String expectedNip = "1234";

        templateRowFieldsValuesMappings.put("name", expectedName);
        templateRowFieldsValuesMappings.put("street", expectedStreet);
        templateRowFieldsValuesMappings.put("city", expectedCity);
        templateRowFieldsValuesMappings.put("post-code", expectedPostCode);
        templateRowFieldsValuesMappings.put("nip", expectedNip);

        //when
        TemplateCreator templateCreator = TemplateCreator.extract(templateRowFieldsValuesMappings);

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
        "  Firma123  , Firma123",
        "  Firma 123  , Firma 123"
    })
    void shouldExtractName(String inputName, String expectedName) {

        //given
        //when
        String gotName = TemplateCreator.extractName(inputName);

        //then
        assertNotNull(gotName);
        assertEquals(expectedName, gotName);
    }

    @ParameterizedTest
    @CsvSource(value = {
        " ulica 12 , ulica 12",
        "  jakaś ulica 12  , jakaś ulica 12"
    })
    void shouldExtractStreet(String inputStreet, String expectedStreet) {

        //given
        //when
        String gotStreet = TemplateCreator.extractStreet(inputStreet);

        //then
        assertNotNull(gotStreet);
        assertEquals(expectedStreet, gotStreet);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "  Wrocław , Wrocław",
        "  Ścinawka średnia , Ścinawka średnia"
    })
    void shouldExtractCity(String inputCity, String expectedCity) {

        //given
        //when
        String gotCity = TemplateCreator.extractCity(inputCity);

        //then
        assertNotNull(gotCity);
        assertEquals(expectedCity, gotCity);
    }

    @Test
    void shouldExtractPostCode() {

        //given
        String expectedPostCode = "12-345";
        String postCodeInput = " " + expectedPostCode + " ";

        //when
        String gotPostCode = TemplateCreator.extractPostCode(postCodeInput);

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
    void shouldExtractNipWithoutSkipSpace(String nipInput, String expectedNip) {

        //given
        //when
        String gotNip = TemplateCreator.extractNip(nipInput);

        //then
        assertNotNull(gotNip);
        assertEquals(expectedNip, gotNip);
    }

}