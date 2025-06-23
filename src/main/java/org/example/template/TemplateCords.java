package org.example.template;

public record TemplateCords(

    Integer x,
    Integer y
){

    private static final double DPI = 150.0;
    private static final double SCALE = 72.0 / DPI;

    public static double convertPxToPt(double px){

        return px * SCALE;
    }
}
