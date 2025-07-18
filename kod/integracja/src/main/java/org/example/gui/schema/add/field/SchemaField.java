package org.example.gui.schema.add.field;

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
