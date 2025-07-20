package org.example.template.data;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateBasicDataTest {

    @Test
    void shouldExtract() {

        //given
        LocalDate expectedCreationDate = LocalDate.of(2025, 7, 20);
        LocalDate expectedReceiveDate = LocalDate.of(2026, 2, 8);

        Map<String, String> values = Map.of(
            "place", "city",
            "creationDate", expectedCreationDate.toString(),
            "receiveDate", expectedReceiveDate.toString(),
            "title", "title 123"
        );

        //when
        TemplateBasicData gotData = TemplateBasicData.extract(values);

        //then
        assertNotNull(gotData);
        assertEquals(values.get("place"), gotData.getPlace());
        assertEquals(expectedCreationDate, gotData.getCreationDate());
        assertEquals(expectedReceiveDate, gotData.getReceiveDate());
        assertEquals(values.get("title"), gotData.getTitle());
    }

    @Test
    public void shouldExtractForNoValues(){

        //given
        //when
        TemplateBasicData gotData = TemplateBasicData.extract(null);

        //then
        assertNotNull(gotData);
        assertEquals("", gotData.getPlace());
        assertNull(gotData.getCreationDate());
        assertNull(gotData.getReceiveDate());
        assertEquals("", gotData.getTitle());
    }

    @Test
    public void shouldExtractForEmptyValues(){

        //given
        //when
        TemplateBasicData gotData = TemplateBasicData.extract(new HashMap<>());

        //then
        assertNotNull(gotData);
        assertEquals("", gotData.getPlace());
        assertNull(gotData.getCreationDate());
        assertNull(gotData.getReceiveDate());
        assertEquals("", gotData.getTitle());
    }

}