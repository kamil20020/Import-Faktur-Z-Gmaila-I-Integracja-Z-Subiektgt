package org.example.template.row;

public enum TemplateRowType {

    AREA("obszar"),
    HORIZONTAL("poziomo");

    private final String name;

    private TemplateRowType(String name){

        this.name = name;
    }

    @Override
    public String toString(){

        return name;
    }

}
