package org.example.template.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TemplateConverterTest {

    @ParameterizedTest
    @CsvSource(value = {
        "5, 5",
        "'5,000', 5",
        "'5.000', 5",
        "'1 5.000', 15",
        "'1 1 5.000', 115"
    })
    void shouldConvertToInteger(String rawValue, Integer expectedValue) {

        //given

        //when
        Integer gotValue = TemplateConverter.convertToInteger(rawValue);

        //then
        assertEquals(expectedValue, gotValue);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "5, 5",
        "5.5, 5.5",
        "'5,5', 5.5",
        "'2 5,5', 25.5",
        "'4 2 5,2', 425.2",
        "'20%', 20",
        "'20.5%', 20.5",
        "'20.6PLN', 20.6",
        "'20.8 PLN', 20.8",
        "'20.0.8 PLN', 200.8",
        "'20.30,80 PLN', 2030.80",
    })
    void shouldConvertToBigDecimal(String rawValue, BigDecimal expectedValue) {

        //given

        //when
        BigDecimal gotValue = TemplateConverter.convertToBigDecimal(rawValue);

        //then
        assertEquals(expectedValue, gotValue);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "2025-12-15, 15, 12, 2025",
        "2024.08.18, 18, 8, 2024",
        "2023/10/04, 4, 10, 2023",
        "21-04-2021, 21, 4, 2021",
        "26.02.2020, 26, 2, 2020",
        "30/05/2026, 30, 5, 2026"
    })
    void shouldTryToParseLocalDate(String rawDate, Integer expectedNumberOfMonthDays, Integer expectedMonth, Integer expectedYear) {

        //given

        //when
        LocalDate gotDate = TemplateConverter.tryToParseLocalDate(rawDate);

        //then
        assertNotNull(gotDate);
        assertEquals(expectedNumberOfMonthDays, gotDate.getDayOfMonth());
        assertEquals(expectedMonth, gotDate.getMonthValue());
        assertEquals(expectedYear, gotDate.getYear());
    }

    @Test
    void shouldTryToParseLocalDateWithFormat() {

        //given
        String expectedFormat = "dd MM yyyy";
        String expectedRawDate = "15 12 2024";

        //when
        LocalDate gotDate = TemplateConverter.tryToParseLocalDate(expectedFormat, expectedRawDate);

        //then
        assertNotNull(gotDate);
        assertEquals(15, gotDate.getDayOfMonth());
        assertEquals(12, gotDate.getMonthValue());
        assertEquals(2024, gotDate.getYear());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "2.2, ., 1",
        ".2.2.3., ., 4",
        "., ., 1",
        "22, ., 0",
        ", , 0",
        "2, , 0",
        ", ., 0",
        "2.2, ',', 0"
    })
    public void shouldGetNumberOfOccurencesInStr(String value, String searchStr, int expectedNumberOfOccurs){

        //given
        //when
        int gotResult = TemplateConverter.getNumberOfOccurencesInStr(searchStr, value);

        //then
        assertEquals(expectedNumberOfOccurs, gotResult);
    }

    @Test
    public void shouldClearValue(){

        //given
        String value = "   aa  b c ";
        String expectedValue = "aa  b c";

        //when
        String gotValue = TemplateConverter.clearValue(value);

        //then
        assertNotNull(gotValue);
        assertEquals(expectedValue, gotValue);
    }

    @Test
    public void shouldClearValueForNoValue(){

        //given
        //when
        String gotValue = TemplateConverter.clearValue(null);

        //then
        assertNull(gotValue);
    }

    @Test
    public void shouldClearValueForEmptyValue(){

        //given
        //when
        String gotValue = TemplateConverter.clearValue("");

        //then
        assertNull(gotValue);
    }

}