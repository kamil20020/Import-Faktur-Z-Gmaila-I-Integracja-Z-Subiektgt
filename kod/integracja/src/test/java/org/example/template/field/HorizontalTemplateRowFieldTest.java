package org.example.template.field;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static org.junit.jupiter.api.Assertions.*;

class HorizontalTemplateRowFieldTest {

    @Test
    void shouldGetRect() {

        //given
        double expectedMinY = 20d;
        double expectedMaxY = 120d;

        HorizontalTemplateRowField field = new HorizontalTemplateRowField(
            10f,
            100f
        );

        //when
        Rectangle2D.Double gotRect = field.getRect(expectedMinY, expectedMaxY);

        //then
        assertEquals(4.8d, gotRect.x);
        assertEquals(43.199999999999996d, gotRect.getWidth());
        assertEquals(9.6d, gotRect.y);
        assertEquals(48d, gotRect.getHeight());
    }

    @Test
    void shouldHandleRect() {

        //given
        HorizontalTemplateRowField field = new HorizontalTemplateRowField();

        Rectangle2D.Float rect = new Rectangle2D.Float(
            1,
            2,
            3,
            4
        );

        //when
        field.handleRect(rect);

        assertEquals(rect.x, field.getXMinCord());
        assertEquals(rect.getMaxX(), field.getXMaxCord());
    }

}