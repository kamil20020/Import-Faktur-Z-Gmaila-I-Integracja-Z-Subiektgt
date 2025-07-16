package org.example.template;

public class TemplateCords{

    private static final double DPI = 150.0;
    private static final double SCALE = 72.0 / DPI;

    private TemplateCords(){


    }

    public static double convertPxToPt(double px){

        return px * SCALE;
    }

    public static double convertPtToPx(double pt){

        return pt / SCALE;
    }

}
