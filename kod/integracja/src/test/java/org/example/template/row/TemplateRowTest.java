package org.example.template.row;

import org.example.template.field.AreaTemplateRowField;
import org.example.template.field.TemplateRowField;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class TemplateRowTest {

    @Test
    void shouldGetValues() {

        //given
        TemplateRowField defaultValueField = TemplateRowField.builder()
            .name("field-1")
            .isHidden(true)
            .defaultValue("default-value")
            .build();

        AreaTemplateRowField field = new AreaTemplateRowField(
            1,
            2,
            3,
            4
        );

        field.setName("field-123");

        List<TemplateRowField> fields = List.of(defaultValueField, field);

        Function<AreaTemplateRowField, String[]> handleExtractValues = areaField -> {

            String fieldName = areaField.getName();

            return new String[]{fieldName};
        };

        TemplateRow templateRow = new TemplateRow(null, fields);

        //when
        Map<String, String> gotValues = templateRow.getValues(handleExtractValues);

        //then
        assertNotNull(gotValues);
        assertEquals(2, gotValues.size());
        assertEquals(defaultValueField.getDefaultValue(), gotValues.get(defaultValueField.getName()));
        assertEquals(field.getName(), gotValues.get(field.getName()));
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