package org.example.gui.add_schema.fields;

import org.example.template.field.TemplateRowFieldType;

public record SchemaField(
    String title,
    TemplateRowFieldType templateRowFieldType
){
    public SchemaField(String title){

        this(title, TemplateRowFieldType.AREA);
    }

}
