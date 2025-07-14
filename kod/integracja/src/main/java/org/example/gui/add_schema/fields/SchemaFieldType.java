package org.example.gui.add_schema.fields;

public enum SchemaFieldType {

    AREA("obszar", "(x1, y1), (x2, y2)"),
    ONLY_X("poziomo", "(x1, x2)"),
    ONLY_Y("pionowo", ("(y1, y2)"));

    private final String name;
    private final String coords;

    private SchemaFieldType(String name, String coords){

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
