package org.example.template.field;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public enum TemplateRowFieldType {

    AREA("Obszar", "(x1, y1), (x2, y2)"),
    HORIZONTAL("Poziomo", "(x1, x2)"),
    VERTICAL("Pionowo", "(y1, y2)"),
    X("Szerokość", "(x2 - x1)"),
    Y("Wysokość", "(y2 - y1)"),
    NO_CORDS("Brak współrzędnych", "brak");

    private final String name;
    private final String coords;

    private TemplateRowFieldType(String name, String coords){

        this.name = name;
        this.coords = coords;
    }

    public String getName(){

        return name;
    }

    public static String getCoords(TemplateRowFieldType type, Rectangle2D.Float rect){

        if(rect == null){

            return NO_CORDS.name;
        }

        int xMin = (int) rect.x;
        int yMin = (int) rect.y;
        int xMax = (int) (rect.x + rect.width);
        int yMax = (int) (rect.y + rect.height);

        return "(" + switch (type){

            case HORIZONTAL -> xMin + ", " + xMax;
            case AREA -> xMin + ", " + yMin + ", " + xMax + ", " + yMax;
            default -> NO_CORDS.name;
        } + ")";
    }

    public String getCoords(){

        return coords;
    }

    @Override
    public String toString(){

        return name;
    }

}
