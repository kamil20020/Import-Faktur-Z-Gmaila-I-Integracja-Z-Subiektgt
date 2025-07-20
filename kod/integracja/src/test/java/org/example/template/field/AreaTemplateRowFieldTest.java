package org.example.template.field;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static org.junit.jupiter.api.Assertions.*;

class AreaTemplateRowFieldTest {

    @Test
    void shouldGetRect() {

        //given
        AreaTemplateRowField field = new AreaTemplateRowField(
            10,
            100,
            20,
            120
        );

        //when
        Rectangle2D.Double gotRect = field.getRect();

        //then
        assertNotNull(gotRect);
        assertEquals(4.8d, gotRect.x);
        assertEquals(43.199999999999996d, gotRect.getWidth());
        assertEquals(9.6d, gotRect.y);
        assertEquals(48d, gotRect.getHeight());
    }

    @Test
    void shouldHandleRect() {

        //given
        Rectangle2D.Float rect = new Rectangle2D.Float(
            1,
            2,
            3,
            4
        );

        AreaTemplateRowField areaTemplateRowField = new AreaTemplateRowField();

        //when
        areaTemplateRowField.handleRect(rect);

        //then
        assertEquals(rect.x, areaTemplateRowField.getXMinCord());
        assertEquals(rect.getMaxX(), areaTemplateRowField.getXMaxCord());
        assertEquals(rect.y, areaTemplateRowField.getYMinCord());
        assertEquals(rect.getMaxY(), areaTemplateRowField.getYMaxCord());
    }

}