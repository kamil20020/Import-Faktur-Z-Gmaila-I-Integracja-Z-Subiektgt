package org.example.gui.schema.add.fields.concrete;

import org.example.gui.ChangeableGui;
import org.example.gui.schema.add.SchemaFieldsGuiAbstract;
import org.example.gui.schema.add.field.SchemaFieldGui;
import org.example.template.row.TemplateRow;

import java.util.function.Consumer;

public abstract class ConcreteSchemaGui<T extends TemplateRow> extends ChangeableGui {

    protected SchemaFieldsGuiAbstract<T> schemaFieldsGui;
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

    public void setData(T data){

        if(data == null){
            return;
        }

        schemaFieldsGui.setData(data);
    }

}
