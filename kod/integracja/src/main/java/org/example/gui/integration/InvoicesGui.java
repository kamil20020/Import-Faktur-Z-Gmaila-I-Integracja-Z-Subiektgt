package org.example.gui.integration;

import org.example.api.gmail.response.MessagesPageResponse;
import org.example.exception.UnloggedException;
import org.example.model.gmail.own.Message;
import org.example.gui.ChangeableGui;
import org.example.model.gmail.own.MessageAttachment;
import org.example.service.InvoiceService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InvoicesGui extends ChangeableGui {

    private JPanel mainPanel;

    private JPanel ordersPanelPlaceholder;
    private PaginationTableGui paginationTableGui;

    private JButton saveOrdersButton;
    private JButton selectAllButton;
    private JButton unselectAllButton;

    private JTextField searchInput;
    private JButton searchButton;

    private List<MessageAttachment> messagesAttachmentsPage = new ArrayList<>();

    private String prevPageToken = "";
    private String actualPageToken = "";
    private String nextPageToken = "";

    private final InvoiceService invoiceService;

    private Runnable handleLogout;

    private static final int GMAIL_MESSAGE_AND_ATTACHMENT_ID_COL_INDEX = 0;
    private static final int GMAIL_MESSAGE_ID_COL_INDEX = 1;
    private static final int SUBIEKT_ID_COL_INDEX = 5;

    private static final String NOT_GIVEN_VALUE = "Brak";

    public InvoicesGui(InvoiceService invoiceService) {

        this.invoiceService = invoiceService;

        $$$setupUI$$$();

        searchInput.setText("Faktura");

        searchButton.addActionListener(e -> handleSearch());
        selectAllButton.addActionListener(e -> selectAll());
        unselectAllButton.addActionListener(e -> unselectAll());
        saveOrdersButton.addActionListener(e -> saveInvoices());
    }

    public void setHandleLogout(Runnable handleLogout) {

        this.handleLogout = handleLogout;
    }

    private PaginationTableGui.PaginationTableData loadData(int offset, int limit) {

        String subject = searchInput.getText();

        updatePagination(offset);

        MessagesPageResponse messagesPageResponse;

        try {

            messagesPageResponse = invoiceService.getMessagesPage(limit, actualPageToken, subject);
        } catch (UnloggedException e) {

            handleLogout.run();

            return null;
        }

        nextPageToken = messagesPageResponse.nextPageToken();

        messagesAttachmentsPage = invoiceService.loadInvoicesDetails(messagesPageResponse.messageHeaders());

        int totalNumberOfRows = messagesPageResponse.totalNumberOfElements();

        PaginationTableGui.PaginationTableData<Object> data = new PaginationTableGui.PaginationTableData(
                messagesAttachmentsPage,
                totalNumberOfRows
        );

        return data;
    }

    private void updatePagination(int newOffset) {

        if (newOffset < paginationTableGui.getPrevOffset()) {

            nextPageToken = actualPageToken;
            actualPageToken = prevPageToken;
        } else if (newOffset > paginationTableGui.getPrevOffset()) {

            prevPageToken = actualPageToken;
            actualPageToken = nextPageToken;
        } else {

            actualPageToken = nextPageToken;
        }
    }

    private void handleSearch() {

        paginationTableGui.handleLoadTableExceptions();
    }

    private Object[] convertToRow(Object rawMessageAttachment) {

        MessageAttachment messageAttachment = (MessageAttachment) rawMessageAttachment;

        Message message = messageAttachment.getMessage();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        LocalDate date = message.getDate().toLocalDate();

        return new Object[]{
                messageAttachment.getId().toString(),
                message.getId(),
                message.getFrom(),
                message.getSubject(),
                messageAttachment.getIndex() + 1,
                messageAttachment.getExternalId() != null ? messageAttachment.getExternalId() : NOT_GIVEN_VALUE,
                dateTimeFormatter.format(date)
        };
    }

    private void selectAll() {

        paginationTableGui.selectAll();
    }

    private void unselectAll() {

        paginationTableGui.unselectAll();
    }

    private List<MessageAttachment> getSelectedMessagesAttachments() {

        List<Object[]> selectedMessagesData = paginationTableGui.getSelectedData();

        if (selectedMessagesData.isEmpty()) {

            return new ArrayList<>();
        }

        List<String> selectedMessageAttachmentsIds = selectedMessagesData.stream()
            .map(selectedOrderData -> selectedOrderData[GMAIL_MESSAGE_AND_ATTACHMENT_ID_COL_INDEX].toString())
            .collect(Collectors.toList());

        return messagesAttachmentsPage.stream()
            .filter(messageAttachment -> selectedMessageAttachmentsIds.contains(messageAttachment.getId().toString()))
            .collect(Collectors.toList());

    }

    private void updateTableRowCol(int rowIndex, int colIndex, String orderId, Object newValue) {

        paginationTableGui.updateRowCol(rowIndex, colIndex, orderId, newValue);
    }

    private void saveInvoices() {

        List<MessageAttachment> selectedMessagesAttachments = getSelectedMessagesAttachments();

        int[] selectedRowsIndices = paginationTableGui.getSelectedRowIndices();

        if (selectedMessagesAttachments.isEmpty()) {

            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Nie wybrano załączników wiadomości email z Gmaila",
                    "Powiadomienie",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new Thread(() -> {

            Map<String, String> errors = invoiceService.createInvoices(selectedMessagesAttachments);

            SwingUtilities.invokeLater(() -> {

                for (int i = 0; i < selectedMessagesAttachments.size(); i++) {

                    MessageAttachment selectedMessageAttachment = selectedMessagesAttachments.get(i);

                    int rowIndex = selectedRowsIndices[i];

                    String invoiceId = selectedMessageAttachment.getId().toString();

                    String invoiceExternalId = selectedMessageAttachment.getExternalId();

                    if (invoiceExternalId == null) {
                        continue;
                    }

                    updateTableRowCol(rowIndex, SUBIEKT_ID_COL_INDEX, invoiceId, invoiceExternalId);
                }

                mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

                JOptionPane.showMessageDialog(
                        mainPanel,
                        "Zapisano " + (selectedMessagesAttachments.size() - errors.size()) + " faktur zakupu w Subiekcie",
                        "Powiadomienie",
                        JOptionPane.INFORMATION_MESSAGE
                );

                displayInvoicesErrors(errors, "Powiadomienia o błędach przy tworzeniu faktur zakupu w Subiekcie");
            });

        }).start();
    }

    private void displayInvoicesErrors(Map<String, String> errors, String dialogTitle) {

        if (errors.isEmpty()) {
            return;
        }

        GridBagLayout layout = new GridBagLayout();

        JPanel panel = new JPanel(layout);

        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;

        int row = 0;

        for (Map.Entry<String, String> error : errors.entrySet()) {

            String orderIdStr = error.getKey();
            String errorMessage = error.getValue();

            JLabel orderIdLabel = new JLabel(orderIdStr);
            JLabel orderErrorMessageLabel = new JLabel(errorMessage);

            int weightY = 0;

            if (row == errors.size() - 1) {

                weightY = 1;
            }

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0;
            gbc.weighty = weightY;
            panel.add(orderIdLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = row;
            gbc.weightx = 1;
            gbc.weighty = weightY;
            orderErrorMessageLabel.setHorizontalAlignment(SwingConstants.LEFT);
            panel.add(orderErrorMessageLabel, gbc);

            row++;
        }

        JScrollPane scrollPane = new JScrollPane(panel);

        JDialog dialog = new JDialog((Frame) null, dialogTitle, false);

        dialog.setSize(680, 400);
        dialog.setLocationRelativeTo(panel);

        dialog.add(scrollPane);

        dialog.setVisible(true);
    }

    private void handleRedirectToMessage(String messageId) {

        invoiceService.redirectToMessage(messageId);
    }

    @Override
    public void load() {

        super.load();

        paginationTableGui.handleLoadTableExceptions();
    }

    public JPanel getMainPanel() {

        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        String[] tableHeaders = {"Id (wiadomość, załącznik)", "Id wiadomości", "Nadawca", "Temat", "Numer załącznika", "Subiekt Id załącznika", "Data"};

        paginationTableGui = new PaginationTableGui(tableHeaders, this::loadData, this::convertToRow);

        paginationTableGui.setSkipColumns(new Integer[]{GMAIL_MESSAGE_AND_ATTACHMENT_ID_COL_INDEX, GMAIL_MESSAGE_ID_COL_INDEX});

        paginationTableGui.setOnClickColumn(GMAIL_MESSAGE_ID_COL_INDEX, this::handleRedirectToMessage);

        ordersPanelPlaceholder = paginationTableGui.getMainPanel();
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
        mainPanel.setAlignmentX(0.0f);
        mainPanel.setAlignmentY(0.0f);
        mainPanel.setAutoscrolls(false);
        mainPanel.setMinimumSize(new Dimension(478, 138));
        mainPanel.setOpaque(true);
        mainPanel.setPreferredSize(new Dimension(1920, 200));
        mainPanel.setRequestFocusEnabled(true);
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(24, 50, 40, 50), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        label1.setAlignmentX(0.0f);
        Font label1Font = this.$$$getFont$$$(null, -1, 26, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setHorizontalAlignment(0);
        label1.setHorizontalTextPosition(0);
        label1.setText("Faktury zakupu z Gmail");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(label1, gbc);
        ordersPanelPlaceholder.setOpaque(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 10.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(8, 0, 0, 0);
        mainPanel.add(ordersPanelPlaceholder, gbc);
        ordersPanelPlaceholder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JToolBar toolBar1 = new JToolBar();
        toolBar1.setBorderPainted(false);
        toolBar1.setFloatable(false);
        toolBar1.setOpaque(false);
        toolBar1.setVisible(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(8, 0, 20, 0);
        mainPanel.add(toolBar1, gbc);
        selectAllButton = new JButton();
        selectAllButton.setText("Zaznacz wszystkie dane");
        toolBar1.add(selectAllButton);
        final JToolBar.Separator toolBar$Separator1 = new JToolBar.Separator();
        toolBar1.add(toolBar$Separator1);
        unselectAllButton = new JButton();
        unselectAllButton.setText("Odznacz wszystkie dane");
        toolBar1.add(unselectAllButton);
        final JToolBar.Separator toolBar$Separator2 = new JToolBar.Separator();
        toolBar1.add(toolBar$Separator2);
        saveOrdersButton = new JButton();
        saveOrdersButton.setText("Zapisz faktury zakupu w Subiekcie");
        toolBar1.add(saveOrdersButton);
        final JToolBar toolBar2 = new JToolBar();
        toolBar2.setBorderPainted(false);
        toolBar2.setFloatable(false);
        toolBar2.setFocusable(true);
        toolBar2.setOpaque(false);
        toolBar2.setToolTipText("Tytuł maila");
        toolBar2.setVerifyInputWhenFocusTarget(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(24, 0, 0, 0);
        mainPanel.add(toolBar2, gbc);
        searchInput = new JTextField();
        searchInput.setMinimumSize(new Dimension(100, 30));
        searchInput.setOpaque(true);
        searchInput.setPreferredSize(new Dimension(100, 30));
        toolBar2.add(searchInput);
        searchButton = new JButton();
        searchButton.setText("Wyszukaj");
        toolBar2.add(searchButton);
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

    /**
     * @noinspection ALL
     */
    private Font $$$getFont1$$$(String fontName, int style, int size, Font currentFont) {
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

}
