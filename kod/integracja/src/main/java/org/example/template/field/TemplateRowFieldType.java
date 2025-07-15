package org.example.template.field;

public enum TemplateRowFieldType {

    AREA("obszar", "(x1, y1), (x2, y2)"),
    HORIZONTAL("poziomo", "(x1, x2)"),
    VERTICAL("pionowo", "(y1, y2)"),
    X("szerokość", "(x2 - x1)"),
    Y("wysokość", "(y2 - y1)"),
    NO_CORDS("brak współrzędnych", "brak");

    private final String name;
    private final String coords;

    private TemplateRowFieldType(String name, String coords){

        this.name = name;
        this.coords = coords;
    }

    public String getName(){

        return name;
    }

    public String getCoords(){

        return coords;
    }

    @Override
    public String toString(){

        return name;
    }

}
