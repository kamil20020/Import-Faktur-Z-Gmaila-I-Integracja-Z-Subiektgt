package org.example.gui.add_schema.field;

import org.example.template.field.TemplateRowFieldType;

public record SchemaField(
    String id,
    String title,
    TemplateRowFieldType templateRowFieldType
){
    public SchemaField(String id, String title){

        this(id, title, TemplateRowFieldType.AREA);
    }

}
