package org.example.gui.schema;

import org.example.gui.ChangeableGui;
import org.example.gui.schema.add.AddSchemaGui;
import org.example.service.TemplateService;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

@Service
public class TemplateMenuService {

    private final TemplateService templateService;
    private final AddSchemaGui addSchemaGui;

    private Dialog dialog;
    private JPanel panel;
    private GridBagConstraints gbc;

    public TemplateMenuService(TemplateService templateService, AddSchemaGui addSchemaGui){

        this.templateService = templateService;
        this.addSchemaGui = addSchemaGui;

        init();
    }

    private void init(){

        GridBagLayout layout = new GridBagLayout();

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        panel = new JPanel(layout);

        JScrollPane scrollPane = new JScrollPane(panel);

        dialog = new JDialog((Frame) null, "Dostępne szablony faktur", false);

        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(panel);

        dialog.add(scrollPane);
    }

    public void handleAddSchema(Consumer<ChangeableGui> handleChangeGui){

        handleChangeGui.accept(addSchemaGui);

        addSchemaGui.handleSelectPdf();

        System.out.println("Dodano szablon");
    }

    public void showAvaiableSchemas(){

        refreshSchemas();

        dialog.setVisible(true);
    }

    private void refreshSchemas(){

        panel.removeAll();

        Collection<String> schemasNames = templateService.getSchemasNames();
        Iterator<String> schemasNamesIterator = schemasNames.iterator();

        for(int i = 0; i < schemasNames.size(); i++){

            String schemaName = schemasNamesIterator.next();

            JLabel namesLabel = new JLabel(schemaName);
            namesLabel.setFont(new Font("", Font.PLAIN, 14));

            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 1;
            gbc.weighty = 1;
            panel.add(namesLabel, gbc);

            JButton removeButton = new JButton("Usuń");
            removeButton.addActionListener(e -> handleRemoveTemplate(schemaName));
            gbc.gridx = 1;
            gbc.gridy = i;
            gbc.weightx = 1;
            gbc.weighty = 1;
            panel.add(removeButton, gbc);
        }

        panel.revalidate();
        panel.repaint();
    }

    private void handleRemoveTemplate(String templateName){

        templateService.remove(templateName);

        refreshSchemas();

        JOptionPane.showMessageDialog(
            null,
            "Usunięto szablon " + templateName,
            "Powiadomienie",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

}
