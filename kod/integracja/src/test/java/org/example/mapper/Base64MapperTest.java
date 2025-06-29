package org.example.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Base64MapperTest {

    @Test
    void shouldMapToBase64() {

        //given
        String rawText = "Raw text";
        String expectedEncodedText = "UmF3IHRleHQ=";

        //when
        String gotEncodedText = Base64Mapper.mapToBase64(rawText);

        //then
        assertEquals(expectedEncodedText, gotEncodedText);
    }

    @Test
    public void shouldMapToBase64WithNull(){

        //given

        //when
        String gotEncodedText = Base64Mapper.mapToBase64(null);

        //then
        assertNull(gotEncodedText);
    }

    @Test
    void shouldMapFromBase64() {

        //given
        String encodedText = "UmF3IHRleHQ=";
        String expectedRawText = "Raw text";

        //when
        String gotRawText = Base64Mapper.mapFromBase64(encodedText);

        //then
        assertEquals(expectedRawText, gotRawText);
    }

    @Test
    public void shouldMapFromBase64WithNull(){

        //given

        //when
        String gotRawText = Base64Mapper.mapFromBase64(null);

        //then
        assertNull(gotRawText);
    }
}