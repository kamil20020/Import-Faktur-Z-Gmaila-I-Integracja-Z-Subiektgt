package org.example.gui.add_schema.fields;

public record SchemaField(
    String title,
    SchemaFieldType schemaFieldType
){
    public SchemaField(String title){

        this(title, SchemaFieldType.AREA);
    }

}
