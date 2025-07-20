package org.example.template.field;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class TemplateRowFieldSimpleFactoryTest {

    @ParameterizedTest
    @CsvSource(value = {
        "HORIZONTAL, HorizontalTemplateRowField",
        "AREA, AreaTemplateRowField",
        "VERTICAL, TemplateRowField",
    })
    void shouldCreate(String fieldTypeStr, String expectedClassStr) {

        //given
        TemplateRowFieldType type = TemplateRowFieldType.valueOf(fieldTypeStr);

        //when
        TemplateRowField gotField = TemplateRowFieldSimpleFactory.create(type);

        //then
        assertNotNull(gotField);
        assertEquals(expectedClassStr, gotField.getClass().getSimpleName());
    }

}