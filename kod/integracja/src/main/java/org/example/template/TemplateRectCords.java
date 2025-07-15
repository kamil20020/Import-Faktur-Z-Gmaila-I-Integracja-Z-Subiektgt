package org.example.template;

import java.awt.geom.Rectangle2D;

public class TemplateRectCords {

    private TemplateRectCords(){


    }

    public static Rectangle2D.Double getRect(float minX, float minY, float maxX, float maxY){

        double width = maxX - minX;
        double height = maxY - minY;

        return new Rectangle2D.Double(
            TemplateCords.convertPxToPt(minX),
            TemplateCords.convertPxToPt(minY),
            TemplateCords.convertPxToPt(width),
            TemplateCords.convertPxToPt(height)
        );
    }

}
