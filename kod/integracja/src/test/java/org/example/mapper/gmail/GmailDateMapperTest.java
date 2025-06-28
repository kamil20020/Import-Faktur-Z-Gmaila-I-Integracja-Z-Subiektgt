package org.example.mapper.gmail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class GmailDateMapperTest {

    @ParameterizedTest
    @CsvSource(value = {
        "'Fri, 27 Jun 2025 09:22:32 +0200 (CEST)', 'Fri, 27 Jun 2025 09:22:32 +0200'",
        "'27 Jun 2025 09:22:32 +0200', 'Fri, 27 Jun 2025 09:22:32 +0200'",
        "'Mon, 9 Jun 2025 17:10:08 +0200', 'Mon, 9 Jun 2025 17:10:08 +0200'",
        "'Wed, 19 Feb 2025 17:10:08 +0000', 'Wed, 19 Feb 2025 17:10:08 +0000'",
    })
    void shouldGetFromStr(String expectedInputDateStr, String expectedDateStr) {

        //given

        //when
        OffsetDateTime gotDate = GmailDateMapper.fromStr(expectedInputDateStr);

        //then
        assertNotNull(gotDate);
        assertEquals(expectedDateStr, GmailDateMapper.toStr(gotDate));
    }

    @Test
    public void shouldGetFromStrWhenStrIsNull(){

        //given

        //when
        OffsetDateTime gotDate = GmailDateMapper.fromStr(null);

        //then
        assertNull(gotDate);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "2025, 6, 27, 9, 22, 32, +0200, 'Fri, 27 Jun 2025 09:22:32 +0200'",
        "2025, 6, 9, 17, 10, 8, +2, 'Mon, 9 Jun 2025 17:10:08 +0200'",
        "2025, 2, 19, 17, 10, 8, +0, 'Wed, 19 Feb 2025 17:10:08 +0000'"
    })
    public void shouldGetFromStr(Integer year, Integer month, Integer dayOfMonth, Integer hour, Integer minute, Integer second, String zoneStr, String expectedDateStr){

        //given
        ZoneOffset zoneOffset = ZoneOffset.of(zoneStr);

        OffsetDateTime date = OffsetDateTime.of(year, month, dayOfMonth, hour, minute, second, 0, zoneOffset);

        //when
        String gotStr = GmailDateMapper.toStr(date);

        //then
        assertNotNull(gotStr);
        assertEquals(expectedDateStr, gotStr);
    }

    @Test
    public void shouldGetFromStrWhenDateIsNull(){

        //given

        //when
        String gotStr = GmailDateMapper.toStr(null);

        //then
        assertNull(gotStr);
    }

}