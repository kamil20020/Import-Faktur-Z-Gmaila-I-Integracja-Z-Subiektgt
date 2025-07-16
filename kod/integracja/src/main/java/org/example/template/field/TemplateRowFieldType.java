package org.example.template.field;

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

    public String getCoords(){

        return coords;
    }

    @Override
    public String toString(){

        return name;
    }

}
