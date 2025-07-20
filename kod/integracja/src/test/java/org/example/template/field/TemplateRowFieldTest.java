package org.example.template.field;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class TemplateRowFieldTest {

    @Test
    void shouldCopy() {

        //given
        TemplateRowField field = new TemplateRowField();

        TemplateRowField field1 = TemplateRowField.builder()
            .type(TemplateRowFieldType.AREA.getName())
            .defaultValue("default value")
            .isHidden(true)
            .index(10)
            .separator("separator")
            .name("field")
            .build();

        //when
        field.copy(field1);

        //then
        assertEquals(field1, field);
    }

    @Test
    void shouldExtract() {

        //given
        TemplateRowField field = new TemplateRowField();
        String[] values = {"abc", "def"};
        String expectedValues = "abc def";

        //when
        String gotValue = field.extract(values);

        //then
        assertNotNull(gotValue);
        assertEquals(expectedValues, gotValue);
    }

    @Test
    void shouldExtractHiddenValue() {

        //given
        TemplateRowField field = TemplateRowField.builder()
            .isHidden(true)
            .defaultValue("default value")
            .build();

        //when
        String gotValue = field.extract(null);

        //then
        assertNotNull(gotValue);
        assertEquals(field.getDefaultValue(), gotValue);
    }

    @Test
    void shouldExtractWithNoValues() {

        //given
        TemplateRowField field = new TemplateRowField();

        //when
        String gotValue = field.extract(null);

        //then
        assertNull(gotValue);
    }

    @Test
    void shouldExtractWithEmptyValues() {

        //given
        TemplateRowField field = new TemplateRowField();

        //when
        String gotValue = field.extract(new String[0]);

        //then
        assertNull(gotValue);
    }

    @Test
    void shouldExtractWithIndex() {

        //given
        TemplateRowField field = TemplateRowField.builder()
            .index(1)
            .separator("\\-")
            .build();

        String[] values = {" abc def   -", " def aa "};
        String expectedValue = "def aa";

        //when
        String gotValue = field.extract(values);

        //then
        assertNotNull(gotValue);
        assertEquals(expectedValue, gotValue);
    }

    @Test
    void shouldNotExtractWithIndexAndWithNoSeparator() {

        //given
        TemplateRowField field = TemplateRowField.builder()
            .index(1)
            .build();

        String[] values = {"abc", "def"};

        //when
        //then
        assertThrows(
            IllegalArgumentException.class,
            () -> field.extract(values)
        );
    }

    @Test
    void shouldNotExtractWithIndexAndWithEmptySeparator() {

        //given
        TemplateRowField field = TemplateRowField.builder()
            .index(1)
            .separator("")
            .build();

        String[] values = {"abc", "def"};

        //when
        //then
        assertThrows(
            IllegalArgumentException.class,
            () -> field.extract(values)
        );
    }

    @ParameterizedTest
    @CsvSource(value = {
        "-1",
        "2"
    })
    void shouldNotExtractWithIndexAndWithInvalidIndex(Integer expectedIndex) {

        //given
        TemplateRowField field = TemplateRowField.builder()
            .index(expectedIndex)
            .separator("")
            .build();

        String[] values = {"abc", "def"};

        //when
        //then
        assertThrows(
            IllegalArgumentException.class,
            () -> field.extract(values)
        );
    }

    @Test
    void shouldNotHandleRect() {

        //given
        TemplateRowField field = new TemplateRowField();

        //when
        //then
        assertThrows(
            IllegalStateException.class,
            () -> field.handleRect(null)
        );
    }

}