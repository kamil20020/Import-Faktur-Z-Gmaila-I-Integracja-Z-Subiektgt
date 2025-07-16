package org.example.template;

import org.junit.jupiter.api.Test;

import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.*;

class TemplateRectCordsTest {

    private static final DecimalFormat decimalFormat = new DecimalFormat("##.#");

    @Test
    void shouldGetRect() {

        //given

        //when
        Rectangle2D.Double gotRect = TemplateRectCords.getRect(10, 20, 100, 120);

        //then
        assertNotNull(gotRect);
        assertEquals(4.8d, gotRect.x);
        assertEquals(9.6d, gotRect.y);

        double gotWidth = parseDouble(gotRect.width);
        double gotHeight = parseDouble(gotRect.height);

        assertEquals(43.2d, gotWidth);
        assertEquals(48d, gotHeight);
    }

    private static double parseDouble(double value){

        String formattedValue = decimalFormat.format(value)
            .replaceAll(",", "\\.");

        return Double.parseDouble(formattedValue);
    }

}