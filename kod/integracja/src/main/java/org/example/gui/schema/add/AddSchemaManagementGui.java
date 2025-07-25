package org.example.gui.schema.add;

import org.example.gui.ChangeableGui;
import org.example.gui.schema.add.field.SchemaFieldGui;
import org.example.gui.schema.add.fields.concrete.*;
import org.example.gui.schema.view.TemplateViewGui;
import org.example.service.TemplateService;
import org.example.template.Template;
import org.example.template.data.DataExtractedFromTemplate;
import org.example.template.row.HeightTemplateRow;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Locale;

@Component
public class AddSchemaManagementGui extends ChangeableGui {

    private JPanel mainPanel;

    private JPanel dataPanel;
    private JPanel creatorPanel;
    private JPanel basicInfoPanel;
    private JPanel productsPanel;
    private JPanel totalPricePanel;
    private JPanel payDatePanel;

    private JButton loadButton;
    private JButton saveButton;
    private JTextField templateNameInput;

    private SchemaFieldGui selectedFieldGui;

    private ConcreteSchemaGui schemaCreatorGui;
    private ConcreteSchemaGui schemaBasicInfoGui;
    private ConcreteSchemaGui schemaProductsGui;
    private ConcreteSchemaGui schemaFinalPriceGui;
    private ConcreteSchemaGui schemaPayDateGui;

    private byte[] fileData = null;

    private final TemplateService templateService;

    public AddSchemaManagementGui(TemplateService templateService) {

        this.templateService = templateService;

        $$$setupUI$$$();

        loadButton.addActionListener(l -> handleLoadData());
        saveButton.addActionListener(l -> handleSave());
    }

    @Override
    public JPanel getMainPanel() {

        return mainPanel;
    }

    public void handleOnSelect(Rectangle2D.Float rect) {

        if (selectedFieldGui == null) {
            return;
        }

        selectedFieldGui.updateCords(rect);
    }

    public void handleOnSelectField(SchemaFieldGui selectedFieldGui) {

        this.selectedFieldGui = selectedFieldGui;
    }

    private void handleLoadData() {

        if (fileData == null) {
            return;
        }

        Template createdTemplate = createTemplate();

        DataExtractedFromTemplate data = templateService.applyTemplate(createdTemplate, fileData);

        System.out.println(data);

        showExtractedData(data);
    }

    private Template createTemplate() {

        return new Template(
                false,
                schemaBasicInfoGui.getData(),
                schemaCreatorGui.getData(),
                (HeightTemplateRow) schemaProductsGui.getData(),
                (HeightTemplateRow) schemaFinalPriceGui.getData(),
                (HeightTemplateRow) schemaPayDateGui.getData()
        );
    }

    private void handleSave() {

        Template createdTemplate = createTemplate();

        String templateName = templateNameInput.getText();

        try {
            templateService.addTemplate(templateName, createdTemplate);
        } catch (RuntimeException e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Powiadomienie o błędzie",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        JOptionPane.showMessageDialog(
                null,
                "Utworzono szablon " + templateName,
                "Powiadomienie",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showExtractedData(DataExtractedFromTemplate data) {

        TemplateViewGui templateViewGui = new TemplateViewGui();

        String templateName = templateNameInput.getText();

        templateViewGui.setData(templateName, data);

        JDialog dialog = new JDialog((Frame) null, "Wydobyte dane", true);

        dialog.add(templateViewGui.getMainPanel());

        dialog.setSize(700, 760);
        dialog.setLocationRelativeTo(null);

        dialog.setVisible(true);
    }

    public void setFileData(byte[] data) {

        this.fileData = data;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        schemaBasicInfoGui = new SchemaBasicInfoGui(this::handleOnSelectField);
        basicInfoPanel = schemaBasicInfoGui.getMainPanel();

        schemaCreatorGui = new SchemaCreatorGui(this::handleOnSelectField);
        creatorPanel = schemaCreatorGui.getMainPanel();

        schemaProductsGui = new SchemaProductsGui(this::handleOnSelectField);
        productsPanel = schemaProductsGui.getMainPanel();

        schemaFinalPriceGui = new SchemaFinalPriceGui(this::handleOnSelectField);
        totalPricePanel = schemaFinalPriceGui.getMainPanel();

        schemaPayDateGui = new SchemaPayDateGui(this::handleOnSelectField);
        payDatePanel = schemaPayDateGui.getMainPanel();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, 22, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setHorizontalAlignment(0);
        label1.setText("Tworzenie szablonu");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(label1, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainPanel.add(scrollPane1, gbc);
        dataPanel = new JPanel();
        dataPanel.setLayout(new GridBagLayout());
        scrollPane1.setViewportView(dataPanel);
        dataPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        dataPanel.add(basicInfoPanel, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        dataPanel.add(creatorPanel, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        dataPanel.add(productsPanel, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        dataPanel.add(totalPricePanel, gbc);
        loadButton = new JButton();
        loadButton.setText("Przetestuj szablon");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(40, 0, 0, 0);
        dataPanel.add(loadButton, gbc);
        saveButton = new JButton();
        saveButton.setText("Zapisz szablon");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 26, 0);
        dataPanel.add(saveButton, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Nazwa szablonu");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(20, 0, 0, 10);
        dataPanel.add(label2, gbc);
        templateNameInput = new JTextField();
        templateNameInput.setMinimumSize(new Dimension(120, 30));
        templateNameInput.setPreferredSize(new Dimension(120, 30));
        templateNameInput.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 10, 0, 0);
        dataPanel.add(templateNameInput, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        dataPanel.add(payDatePanel, gbc);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
