package org.example.gui.add_schema.fields.concrete;

import org.example.gui.ChangeableGui;
import org.example.gui.add_schema.SchemaFieldsGuiAbstract;
import org.example.gui.add_schema.field.SchemaFieldGui;
import org.example.template.row.TemplateRow;

import java.util.function.Consumer;

public abstract class ConcreteSchemaGui extends ChangeableGui {

    protected SchemaFieldsGuiAbstract schemaFieldsGui;
    protected final Consumer<SchemaFieldGui> onSelect;

    public ConcreteSchemaGui(Consumer<SchemaFieldGui> onSelect){

        this.onSelect = onSelect;
    }

    public TemplateRow getData(){

        if(schemaFieldsGui == null){

            return null;
        }

        return schemaFieldsGui.getData();
    }

}
