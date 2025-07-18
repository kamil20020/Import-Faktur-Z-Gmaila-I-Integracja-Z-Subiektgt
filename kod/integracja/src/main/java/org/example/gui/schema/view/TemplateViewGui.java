package org.example.gui.schema.view;

import org.example.gui.ChangeableGui;
import org.example.template.data.DataExtractedFromTemplate;
import org.example.template.data.TemplateCreator;
import org.example.template.data.TemplateInvoiceItem;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.List;
import java.util.Locale;

public class TemplateViewGui extends ChangeableGui {

    private JPanel mainPanel;

    private JLabel templateNameValueLabel;
    private JPanel productsPanel;
    private JLabel placeValueLabel;
    private JLabel deliveryEndValueLabel;
    private JLabel creationDateValueLabel;
    private JLabel titleValueLabel;
    private JLabel authorNameValueLabel;
    private JLabel streetValueLabel;
    private JLabel postCodeValueLabel;
    private JLabel cityValueLabel;
    private JLabel nipValueLabel;
    private JLabel totalPriceValueLabel;
    private JPanel dataPanel;
    private JLabel payDateValueLabel;

    private TemplateProductsViewGui templateProductsViewGui;

    public void setData(String templateTitle, DataExtractedFromTemplate data) {

        if (data == null) {
            return;
        }

        templateNameValueLabel.setText(templateTitle);

        updateBasicData(data);
        updateAuthorData(data.creator());

        List<TemplateInvoiceItem> products = data.invoiceItems();

        templateProductsViewGui.updateProducts(products);

        String totalPriceStr = handleToString(data.totalPrice());
        totalPriceValueLabel.setText(totalPriceStr);

        String payDateStr = handleToString(data.payDate());
        payDateValueLabel.setText(payDateStr);
    }

    private void updateBasicData(DataExtractedFromTemplate data) {

        placeValueLabel.setText(data.place());

        String receiveDateStr = handleToString(data.receiveDate());
        deliveryEndValueLabel.setText(receiveDateStr);

        String creationDateStr = handleToString(data.receiveDate());
        creationDateValueLabel.setText(creationDateStr);
        titleValueLabel.setText(data.title());
    }

    private String handleToString(Object value) {

        if (value == null) {
            return "";
        }

        return value.toString();
    }

    private void updateAuthorData(TemplateCreator data) {

        if (data == null) {
            return;
        }

        authorNameValueLabel.setText(data.name());
        streetValueLabel.setText(data.street());
        postCodeValueLabel.setText(data.postCode());
        cityValueLabel.setText(data.city());
        nipValueLabel.setText(data.nip());
    }

    @Override
    public JPanel getMainPanel() {

        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        templateProductsViewGui = new TemplateProductsViewGui();
        productsPanel = templateProductsViewGui.getMainPanel();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
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
        final JScrollPane scrollPane1 = new JScrollPane();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane1, gbc);
        dataPanel = new JPanel();
        dataPanel.setLayout(new GridBagLayout());
        scrollPane1.setViewportView(dataPanel);
        dataPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(60, 32, 60, 32), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        label1.setText("Miejsce wystawienia");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 60, 0, 0);
        dataPanel.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Data zakończenia dostawy");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 60, 0, 0);
        dataPanel.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Data wystawienia");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 60, 0, 0);
        dataPanel.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Numer faktury");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 60, 0, 0);
        dataPanel.add(label4, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Nazwa");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 60, 0, 0);
        dataPanel.add(label5, gbc);
        final JLabel label6 = new JLabel();
        Font label6Font = this.$$$getFont$$$(null, -1, 16, label6.getFont());
        if (label6Font != null) label6.setFont(label6Font);
        label6.setHorizontalAlignment(10);
        label6.setText("Podstawowe informacje");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(32, 0, 0, 0);
        dataPanel.add(label6, gbc);
        final JLabel label7 = new JLabel();
        Font label7Font = this.$$$getFont$$$(null, -1, 16, label7.getFont());
        if (label7Font != null) label7.setFont(label7Font);
        label7.setText("Informacje o nadawcy");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(32, 0, 0, 0);
        dataPanel.add(label7, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("Ulica");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 60, 0, 0);
        dataPanel.add(label8, gbc);
        final JLabel label9 = new JLabel();
        label9.setText("Kod pocztowy");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 60, 0, 0);
        dataPanel.add(label9, gbc);
        final JLabel label10 = new JLabel();
        label10.setText("Miasto");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 60, 0, 0);
        dataPanel.add(label10, gbc);
        final JLabel label11 = new JLabel();
        label11.setText("Nip");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 60, 0, 0);
        dataPanel.add(label11, gbc);
        final JLabel label12 = new JLabel();
        Font label12Font = this.$$$getFont$$$(null, -1, 16, label12.getFont());
        if (label12Font != null) label12.setFont(label12Font);
        label12.setText("Dane o produktach");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(32, 0, 0, 0);
        dataPanel.add(label12, gbc);
        final JLabel label13 = new JLabel();
        Font label13Font = this.$$$getFont$$$(null, -1, 16, label13.getFont());
        if (label13Font != null) label13.setFont(label13Font);
        label13.setText("Cena końcowa");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(20, 0, 0, 0);
        dataPanel.add(label13, gbc);
        final JLabel label14 = new JLabel();
        label14.setText("Kwota");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 60, 0, 0);
        dataPanel.add(label14, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 0, 0);
        dataPanel.add(productsPanel, gbc);
        placeValueLabel = new JLabel();
        placeValueLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 0, 0, 0);
        dataPanel.add(placeValueLabel, gbc);
        deliveryEndValueLabel = new JLabel();
        deliveryEndValueLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        dataPanel.add(deliveryEndValueLabel, gbc);
        creationDateValueLabel = new JLabel();
        creationDateValueLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        dataPanel.add(creationDateValueLabel, gbc);
        titleValueLabel = new JLabel();
        titleValueLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        dataPanel.add(titleValueLabel, gbc);
        authorNameValueLabel = new JLabel();
        authorNameValueLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 0, 0, 0);
        dataPanel.add(authorNameValueLabel, gbc);
        streetValueLabel = new JLabel();
        streetValueLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        dataPanel.add(streetValueLabel, gbc);
        postCodeValueLabel = new JLabel();
        postCodeValueLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        dataPanel.add(postCodeValueLabel, gbc);
        cityValueLabel = new JLabel();
        cityValueLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        dataPanel.add(cityValueLabel, gbc);
        nipValueLabel = new JLabel();
        nipValueLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 11;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        dataPanel.add(nipValueLabel, gbc);
        totalPriceValueLabel = new JLabel();
        totalPriceValueLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 15;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 0, 0, 0);
        dataPanel.add(totalPriceValueLabel, gbc);
        templateNameValueLabel = new JLabel();
        templateNameValueLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        dataPanel.add(templateNameValueLabel, gbc);
        final JLabel label15 = new JLabel();
        label15.setText("Nazwa szablonu");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 60, 0, 0);
        dataPanel.add(label15, gbc);
        final JLabel label16 = new JLabel();
        Font label16Font = this.$$$getFont$$$(null, -1, 16, label16.getFont());
        if (label16Font != null) label16.setFont(label16Font);
        label16.setText("Termin płatności");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(20, 0, 0, 0);
        dataPanel.add(label16, gbc);
        final JLabel label17 = new JLabel();
        label17.setText("Data");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 17;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 60, 0, 0);
        dataPanel.add(label17, gbc);
        payDateValueLabel = new JLabel();
        payDateValueLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 17;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 0, 0, 0);
        dataPanel.add(payDateValueLabel, gbc);
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
