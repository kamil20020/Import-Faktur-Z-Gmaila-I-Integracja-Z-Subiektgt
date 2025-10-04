package org.example.gui.schema.add;

import org.example.gui.ChangeableGui;
import org.example.gui.schema.add.field.SchemaField;
import org.example.gui.schema.add.field.SchemaFieldGui;
import org.example.template.field.TemplateRowField;
import org.example.template.row.TemplateRow;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class SchemaFieldsGuiAbstract<T extends TemplateRow> extends ChangeableGui {

    private JPanel panel;

    private List<SchemaFieldGui> schemaFieldsGuiList = new ArrayList<>();

    protected final String title;
    private final List<SchemaField> schemaFields;
    private final Consumer<SchemaFieldGui> onSelect;

    public SchemaFieldsGuiAbstract(String title, List<SchemaField> schemaFields, Consumer<SchemaFieldGui> onSelect){

        this.title = title;
        this.schemaFields = schemaFields;
        this.onSelect = onSelect;
    }

    public abstract TemplateRow getData();

    public void setData(T data){

        List<TemplateRowField> fieldsData = data.getFields();

        for(int i = 0; i < schemaFields.size(); i++){

            SchemaFieldGui schemaFieldGui = schemaFieldsGuiList.get(i);
            TemplateRowField dataRowField = fieldsData.get(i);

            schemaFieldGui.setData(dataRowField);
        }
    }

    public List<TemplateRowField> getRowsFields(){

        return schemaFieldsGuiList.stream()
            .map(fieldGui -> fieldGui.getData())
            .collect(Collectors.toList());
    }

    protected void setPanel(JPanel panel){

        this.panel = panel;
    }

    protected void loadFields(JPanel fieldsPanel) {

        GridBagConstraints mainPanelLayoutConstrains = new GridBagConstraints();

        for (int i = 0; i < schemaFields.size(); i++) {

            SchemaField schemaField = schemaFields.get(i);

            mainPanelLayoutConstrains.gridx = 0;
            mainPanelLayoutConstrains.gridy = i;
            mainPanelLayoutConstrains.weightx = 1;
            mainPanelLayoutConstrains.weighty = 0;
            mainPanelLayoutConstrains.fill = GridBagConstraints.HORIZONTAL;

            SchemaFieldGui schemaFieldGui = new SchemaFieldGui(schemaField, onSelect);

            schemaFieldsGuiList.add(schemaFieldGui);

            fieldsPanel.add(schemaFieldGui.getMainPanel(), mainPanelLayoutConstrains);
        }
    }

    @Override
    public JPanel getMainPanel() {

        return panel;
    }

}
