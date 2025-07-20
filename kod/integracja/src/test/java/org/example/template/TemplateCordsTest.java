package org.example.template;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplateCordsTest {

    @Test
    void shouldConvertPxToPt() {

        //given
        double numberOfPixels = 23d;

        //when
        double gotResult = TemplateCords.convertPxToPt(numberOfPixels);

        //then
        assertEquals(11.04d, gotResult);
    }

    @Test
    public void shouldConvertPtToPx(){

        //given
        double ptValue = 11.04d;

        //when
        double gotResult = TemplateCords.convertPtToPx(ptValue);

        //then
        assertEquals(23d, gotResult);
    }

}