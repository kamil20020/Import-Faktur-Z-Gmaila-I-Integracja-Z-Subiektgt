package org.example.template.row;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateRowTest {

    @Test
    void shouldGetValues() {

        throw new IllegalStateException("Not tested yet");
    }

    @Test
    void shouldGetEmptyValuesForNoFields() {

        //given
        TemplateRow templateRow = new TemplateRow();

        //when
        Map<String, String> gotValues = templateRow.getValues(null);

        //then
        assertNotNull(gotValues);
        assertTrue(gotValues.isEmpty());
    }

    @Test
    void shouldGetEmptyValuesForEmptyFields() {

        //given
        TemplateRow templateRow = TemplateRow.builder()
            .fields(new ArrayList<>())
            .build();

        //when
        Map<String, String> gotValues = templateRow.getValues(null);

        //then
        assertNotNull(gotValues);
        assertTrue(gotValues.isEmpty());
    }

}