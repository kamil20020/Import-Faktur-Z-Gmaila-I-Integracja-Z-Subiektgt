package org.example.template;

import java.awt.geom.Rectangle2D;

public record TemplateRectCords(

    TemplateCords leftTop,
    TemplateCords rightDown
){

    public Rectangle2D.Double getRect(){

        double width = rightDown.x() - leftTop.x();
        double height = rightDown.y() - leftTop.y();

        return new Rectangle2D.Double(
            TemplateCords.convertPxToPt(leftTop.x()),
            TemplateCords.convertPxToPt(leftTop.y()),
            TemplateCords.convertPxToPt(width),
            TemplateCords.convertPxToPt(height)
        );
    }

}
