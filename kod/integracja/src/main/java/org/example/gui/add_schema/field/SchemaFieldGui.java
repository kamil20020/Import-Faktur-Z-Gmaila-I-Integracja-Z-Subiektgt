package org.example.gui.add_schema.field;

import org.example.gui.ChangeableGui;
import org.example.template.field.TemplateRowField;
import org.example.template.field.TemplateRowFieldSimpleFactory;
import org.example.template.field.TemplateRowFieldType;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Locale;
import java.util.function.Consumer;

public class SchemaFieldGui extends ChangeableGui {

    private JPanel mainPanel;

    private JLabel titleLabel;

    private JLabel cordsTitleLabel;
    private JLabel cordsLabel;
    private JButton selectPositionButton;

    private JCheckBox fieldIsHiddenCheckbox;
    private JLabel defaultValueLabel;
    private JTextField defaultValueInput;

    private JCheckBox fieldRequiresSeparateCheckbox;
    private JLabel separatorLabel;
    private JTextField separatorInput;
    private JLabel separateIndexLabel;
    private JFormattedTextField separateIndexInput;

    private final Consumer<SchemaFieldGui> onSelect;
    private final TemplateRowFieldType firstType;

    private TemplateRowFieldType type;
    private TemplateRowField data;

    public SchemaFieldGui(SchemaField schemaField, Consumer<SchemaFieldGui> onSelect) {

        this.onSelect = onSelect;
        this.firstType = schemaField.templateRowFieldType();
        this.type = firstType;

        $$$setupUI$$$();

        String schemaFieldTypeName = type.getName();
        String coords = type.getCoords();

        titleLabel.setText(schemaField.title());
        cordsTitleLabel.setText(schemaFieldTypeName);
        cordsLabel.setText(coords);

        fieldIsHiddenCheckbox.addItemListener(l -> handleCheckboxEvent(
                l,
                fieldIsHiddenCheckbox,
                this::handleSelectFieldIsHidden,
                this::handleCancelFieldIsHidden
        ));

        fieldRequiresSeparateCheckbox.addItemListener(l -> handleCheckboxEvent(
                l,
                fieldRequiresSeparateCheckbox,
                this::handleSelectIsRequiredSeparate,
                this::handleCancelIsRequiredSeparate
        ));

        selectPositionButton.addActionListener(l -> handleSelect());

        defaultValueInput.getDocument().addDocumentListener(
                getHandleChangeTextFieldValueListener(defaultValueInput, this::handleChangeDefaultValue)
        );

        separatorInput.getDocument().addDocumentListener(
                getHandleChangeTextFieldValueListener(separatorInput, this::handleChangeSeparateValue)
        );

        separateIndexInput.getDocument().addDocumentListener(
                getHandleChangeTextFieldValueListener(separateIndexInput, this::handleChangeIndexValue)
        );

        data = TemplateRowFieldSimpleFactory.create(type);
        data.setName(schemaField.id());
    }

    private void handleCheckboxEvent(ItemEvent event, Object expectedSource, Runnable onSelect, Runnable onCancel) {

        Object source = event.getSource();

        if (source != expectedSource) {
            return;
        }

        boolean isSelected = event.getStateChange() == ItemEvent.SELECTED;

        if (isSelected) {

            onSelect.run();
        } else {

            onCancel.run();
        }
    }

    private void handleSelectFieldIsHidden() {

        data.setHidden(true);
        data.setSeparator(null);
        data.setIndex(null);

        updateFieldWithType(TemplateRowFieldType.NO_CORDS);

        cordsTitleLabel.setVisible(false);
        cordsLabel.setVisible(false);
        selectPositionButton.setVisible(false);

        fieldRequiresSeparateCheckbox.setVisible(false);

        defaultValueLabel.setVisible(true);
        defaultValueInput.setVisible(true);
    }

    private void handleCancelFieldIsHidden() {

        data.setHidden(false);
        data.setDefaultValue(null);

        updateFieldWithType(firstType);

        cordsTitleLabel.setVisible(true);
        cordsLabel.setVisible(true);
        selectPositionButton.setVisible(true);

        fieldRequiresSeparateCheckbox.setVisible(true);

        defaultValueLabel.setVisible(false);
        defaultValueInput.setVisible(false);
    }

    private void handleSelectIsRequiredSeparate() {

        separatorLabel.setVisible(true);
        separatorInput.setVisible(true);
        separateIndexLabel.setVisible(true);
        separateIndexInput.setVisible(true);
    }

    private void handleCancelIsRequiredSeparate() {

        data.setSeparator(null);
        data.setIndex(null);

        separatorLabel.setVisible(false);
        separatorInput.setVisible(false);
        separateIndexLabel.setVisible(false);
        separateIndexInput.setVisible(false);
    }

    private void updateFieldWithType(TemplateRowFieldType type) {

        TemplateRowField newData = TemplateRowFieldSimpleFactory.create(type);
        newData.copy(data);
    }

    private DocumentListener getHandleChangeTextFieldValueListener(JTextField input, Consumer<String> handleChangeValue) {

        return new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {

                String newValue = input.getText();

                handleChangeValue.accept(newValue);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {

                String newValue = input.getText();

                handleChangeValue.accept(newValue);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

                String newValue = input.getText();

                handleChangeValue.accept(newValue);
            }
        };
    }

    private void handleChangeDefaultValue(String newValue) {

        data.setDefaultValue(newValue);
    }

    private void handleChangeSeparateValue(String newValue) {

        data.setSeparator(newValue);
    }

    private void handleChangeIndexValue(String newValue) {

        if (newValue == null || newValue.isBlank()) {

            data.setIndex(null);

            return;
        }

        Integer newIndex = null;

        String actualIndexStr = null;

        try {
            newIndex = Integer.valueOf(newValue);

            if (newIndex < 0) {

                newIndex = 0;
            }

            actualIndexStr = newIndex.toString();

            data.setIndex(newIndex);
        } catch (NumberFormatException e) {

            Integer actualIndex = data.getIndex();

            if (actualIndex != null) {

                actualIndexStr = actualIndex.toString();
            }
        }

        String finalActualIndexStr = actualIndexStr;

        SwingUtilities.invokeLater(() -> {

            separateIndexInput.setText(finalActualIndexStr);
        });
    }

    public void updateCords(Rectangle.Float rect) {

        data.handleRect(rect);

        String cordsStr = TemplateRowFieldType.getCoords(type, rect);

        cordsLabel.setText(cordsStr);
    }

    public void handleSelect() {

        onSelect.accept(this);
    }

    public TemplateRowField getData() {

        return data;
    }

    @Override
    public JPanel getMainPanel() {

        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

    }

// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(32, 0, 0, 0), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        fieldIsHiddenCheckbox = new JCheckBox();
        fieldIsHiddenCheckbox.setText("Pole jest ukryte");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainPanel.add(fieldIsHiddenCheckbox, gbc);
        selectPositionButton = new JButton();
        selectPositionButton.setHorizontalAlignment(0);
        selectPositionButton.setText("Współrzędne");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 0, 0);
        mainPanel.add(selectPositionButton, gbc);
        cordsLabel = new JLabel();
        Font cordsLabelFont = this.$$$getFont$$$(null, Font.PLAIN, -1, cordsLabel.getFont());
        if (cordsLabelFont != null) cordsLabel.setFont(cordsLabelFont);
        cordsLabel.setText("(0, 0), (0, 0)");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(20, 20, 0, 0);
        mainPanel.add(cordsLabel, gbc);
        cordsTitleLabel = new JLabel();
        cordsTitleLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainPanel.add(cordsTitleLabel, gbc);
        fieldRequiresSeparateCheckbox = new JCheckBox();
        fieldRequiresSeparateCheckbox.setText("Pole wymaga odzielenia");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainPanel.add(fieldRequiresSeparateCheckbox, gbc);
        separatorLabel = new JLabel();
        separatorLabel.setEnabled(true);
        separatorLabel.setText("Separator");
        separatorLabel.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 0, 0);
        mainPanel.add(separatorLabel, gbc);
        separatorInput = new JTextField();
        separatorInput.setEditable(true);
        separatorInput.setEnabled(true);
        separatorInput.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 0, 0);
        mainPanel.add(separatorInput, gbc);
        separateIndexLabel = new JLabel();
        separateIndexLabel.setEnabled(true);
        separateIndexLabel.setText("Pozycja");
        separateIndexLabel.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 0, 0);
        mainPanel.add(separateIndexLabel, gbc);
        separateIndexInput = new JFormattedTextField();
        separateIndexInput.setEditable(true);
        separateIndexInput.setEnabled(true);
        separateIndexInput.setMinimumSize(new Dimension(100, 30));
        separateIndexInput.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 0, 0);
        mainPanel.add(separateIndexInput, gbc);
        titleLabel = new JLabel();
        Font titleLabelFont = this.$$$getFont$$$(null, -1, 14, titleLabel.getFont());
        if (titleLabelFont != null) titleLabel.setFont(titleLabelFont);
        titleLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(titleLabel, gbc);
        defaultValueInput = new JTextField();
        defaultValueInput.setEnabled(true);
        defaultValueInput.setMargin(new Insets(2, 6, 2, 6));
        defaultValueInput.setMinimumSize(new Dimension(100, 30));
        defaultValueInput.setOpaque(true);
        defaultValueInput.setPreferredSize(new Dimension(100, 30));
        defaultValueInput.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 0, 0);
        mainPanel.add(defaultValueInput, gbc);
        defaultValueLabel = new JLabel();
        defaultValueLabel.setEnabled(true);
        defaultValueLabel.setOpaque(false);
        defaultValueLabel.setText("Domyślna wartość");
        defaultValueLabel.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 0, 0);
        mainPanel.add(defaultValueLabel, gbc);
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
