package org.example.template.row;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HeightTemplateRowTest {

    @Test
    void shouldGetValues() {


    }

    @ParameterizedTest
    @CsvSource(value = {
        "123, 0",
        "456  a, 1",
        "789, 4",
        "a78, 3"
    })
    public void shouldGetLineStartIndex(String searchValue, int expectedIndex){

        //given
        List<String> lines = List.of(
            "123",
            "456  a b c",
            " 789",
            "a789 aaa"
        );

        //when
        int gotIndex = HeightTemplateRow.getLineStartIndex(lines, searchValue);

        //then
        assertEquals(expectedIndex, gotIndex);
    }

    @Test
    public void shouldGetValuesRow(){


    }

}