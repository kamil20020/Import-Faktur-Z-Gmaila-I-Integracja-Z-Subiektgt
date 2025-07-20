package org.example.template;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TemplateTest {

    @Test
    void shouldSearchInContentWithSearch() {

        //given
        String content = """
            aaaaa bbbbbbbbbbb cccccccc
            aaaaaaaaa abc aaaaaaa
            aaaaaaaaaaa def ddddd abc
            aaaaaaa
        """;

        List<String> values = List.of("abc", "def");

        //when
        Optional<String> gotResultOpt = Template.searchInContent(content, values);

        //then
        assertTrue(gotResultOpt.isPresent());

        String gotValue = gotResultOpt.get();

        assertEquals("abc", gotValue);
    }

    @Test
    void shouldSearchInContentWithNoSearch() {

        //given
        String content = """
            aaaaa bbbbbbbbbbb cccccccc
            aaaaaaaaa abc aaaaaaa
            aaaaaaaaaaa def ddddd abc
            aaaaaaa
        """;

        List<String> values = List.of("ab1c", "de1f");

        //when
        Optional<String> gotResultOpt = Template.searchInContent(content, values);

        //then
        assertTrue(gotResultOpt.isEmpty());
    }

    @Test
    void shouldSearchInContentWhenContentHasNoLines() {

        //given
        String content = "aaa";
        List<String> values = List.of("abc", "def");

        //when
        Optional<String> gotResultOpt = Template.searchInContent(content, values);

        //then
        assertTrue(gotResultOpt.isEmpty());
    }

    @Test
    void shouldNotSearchInContentWhenThereIsNoContent() {

        //given
        List<String> values = List.of("abc, def");

        //when
        Optional<String> gotResultOpt = Template.searchInContent(null, values);

        //then
        assertTrue(gotResultOpt.isEmpty());
    }

    @Test
    void shouldNotSearchInContentWhenThereIsEmptyContent() {

        //given
        List<String> values = List.of("abc, def");

        //when
        Optional<String> gotResultOpt = Template.searchInContent("", values);

        //then
        assertTrue(gotResultOpt.isEmpty());
    }

    @Test
    void shouldNotSearchInContentWhenThereIsNoValues() {

        //given
        //when
        Optional<String> gotResultOpt = Template.searchInContent("content", null);

        //then
        assertTrue(gotResultOpt.isEmpty());
    }

    @Test
    void shouldNotSearchInContentWhenThereIsEmptyValues() {

        //given
        //when
        Optional<String> gotResultOpt = Template.searchInContent("content", new ArrayList<>());

        //then
        assertTrue(gotResultOpt.isEmpty());
    }

}