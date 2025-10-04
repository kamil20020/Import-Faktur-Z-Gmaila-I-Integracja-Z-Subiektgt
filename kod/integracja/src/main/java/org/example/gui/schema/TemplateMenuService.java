package org.example.gui.schema;

import org.example.api.Api;
import org.example.gui.ChangeableGui;
import org.example.gui.pdf_viewer.FileDialogHandler;
import org.example.gui.schema.add.AddSchemaGui;
import org.example.gui.schema.add.AddSchemaManagementGui;
import org.example.loader.FileReader;
import org.example.loader.pdf.PdfFileReader;
import org.example.service.TemplateService;
import org.example.template.Template;
import org.example.template.data.DataExtractedFromTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class TemplateMenuService {

    private final TemplateService templateService;
    private final AddSchemaGui addSchemaGui;

    private Dialog dialog;
    private JPanel panel;
    private GridBagConstraints gbc;

    private static final Logger log = LoggerFactory.getLogger(Api.class);

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

    public void testTemplates(){

        Optional<File> gotFileOpt = FileDialogHandler.getLoadFileDialogSelectedPath("Wybieranie faktury", "*.pdf");

        if (gotFileOpt.isEmpty()) {
            return;
        }

        File gotFile = gotFileOpt.get();
        Path path = gotFile.toPath();

        byte[] fileData = FileReader.getDataFormPath(path);

        Optional<Template> foundTemplate = templateService.findTemplateForData(fileData);

        if(foundTemplate.isEmpty()){

            log.info("Template was not found");

            return;
        }

        DataExtractedFromTemplate gotData = templateService.applyGoodTemplateForData(fileData);

        AddSchemaManagementGui.showExtractedData(gotData.title(), gotData);
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
