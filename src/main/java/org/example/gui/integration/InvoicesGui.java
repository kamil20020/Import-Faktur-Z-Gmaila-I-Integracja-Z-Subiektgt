package org.example.gui.integration;

import org.example.api.gmail.response.MessagesPageResponse;
import org.example.exception.UnloggedException;
import org.example.external.gmail.Message;
import org.example.gui.BooleanSelectOptions;
import org.example.gui.ChangeableGui;
import org.example.service.MessageService;
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
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class InvoicesGui extends ChangeableGui {

    private JPanel mainPanel;

    private JPanel ordersPanelPlaceholder;
    private PaginationTableGui paginationTableGui;

    private JButton saveOrdersButton;
    private JButton selectAllButton;
    private JButton unselectAllButton;

    private List<Message> messagesPage = new ArrayList<>();

    private String prevPageToken = "";
    private String actualPageToken = "";
    private String nextPageToken = "";

    private final MessageService messageService;

    private Runnable handleLogout;

    private static final int SUBIEKT_ID_SENT_COL_INDEX = 1;
    private static final int ALLEGRO_IS_INVOICE_COL_INDEX = 6;
    private static final int ALLEGRO_DOCUMENT_SENT_COL_INDEX = 7;

    private static final String NOT_GIVEN_VALUE = "Brak";

    public InvoicesGui(MessageService messageService) {

        this.messageService = messageService;

        $$$setupUI$$$();

        selectAllButton.addActionListener(e -> selectAll());
        unselectAllButton.addActionListener(e -> unselectAll());
        saveOrdersButton.addActionListener(e -> saveMessages());
    }

    public void setHandleLogout(Runnable handleLogout) {

        this.handleLogout = handleLogout;
    }

    private PaginationTableGui.PaginationTableData loadData(int offset, int limit) {

        if (offset < paginationTableGui.getOffset()) {

            nextPageToken = actualPageToken;
            actualPageToken = prevPageToken;
        } else if (offset > paginationTableGui.getOffset()) {

            prevPageToken = actualPageToken;
            actualPageToken = nextPageToken;
        }

        MessagesPageResponse messagesPageResponse;

        try {

            messagesPageResponse = messageService.getPage(limit, actualPageToken);
        } catch (UnloggedException e) {

            handleLogout.run();

            return null;
        }

        nextPageToken = messagesPageResponse.nextPageToken();

        messagesPage = messageService.extractMessages(messagesPageResponse.messageHeaders());

        int totalNumberOfRows = messagesPageResponse.totalNumberOfElements();

        PaginationTableGui.PaginationTableData<Object> data = new PaginationTableGui.PaginationTableData(
                messagesPage,
                totalNumberOfRows
        );

        return data;
    }

    private Object[] convertToRow(Object rawMessage) {

        Message message = (Message) rawMessage;

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        LocalDate date = message.getDate().toLocalDate();

        return new Object[]{
            message.getId(),
            message.getFrom(),
            message.getSubject(),
            dateTimeFormatter.format(date)
        };
    }

    private void selectAll() {

        paginationTableGui.selectAll();
    }

    private void unselectAll() {

        paginationTableGui.unselectAll();
    }

    private List<Message> getSelectedMessages() {

        List<Object[]> selectedMessagesData = paginationTableGui.getSelectedData();

        if (selectedMessagesData.isEmpty()) {

            return new ArrayList<>();
        }

        List<String> selectedOrdersIds = selectedMessagesData.stream()
                .map(selectedOrderData -> selectedOrderData[0].toString())
                .collect(Collectors.toList());

        return messagesPage.stream()
                .filter(order -> selectedOrdersIds.contains(order.getId()))
                .collect(Collectors.toList());

    }

    private void updateTableRowCol(int rowIndex, int colIndex, String orderId, Object newValue) {

        paginationTableGui.updateRowCol(rowIndex, colIndex, orderId, newValue);
    }

    private void saveMessages() {

        List<Message> selectedMessages = getSelectedMessages();

        int[] selectedRowsIndices = paginationTableGui.getSelectedRowIndices();

        if (selectedMessages.isEmpty()) {

            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Nie wybrano wiadomości email z Gmaila",
                    "Powiadomienie",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }
//
//        mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//
//        new Thread(() -> {
//
//            int numberOfSavedOrders = sferaOrderService.create(selectedOrders);
//
//            SwingUtilities.invokeLater(() -> {
//
//                for (int i = 0; i < selectedOrders.size(); i++) {
//
//                    Order selectedOrder = selectedOrders.get(i);
//
//                    int rowIndex = selectedRowsIndices[i];
//
//                    if (selectedOrder.getExternalId() == null) {
//                        continue;
//                    }
//
//                    String orderId = selectedOrder.getId().toString();
//
//                    updateTableRowCol(rowIndex, SUBIEKT_ID_SENT_COL_INDEX, orderId, selectedOrder.getExternalId());
//                }
//
//                mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//
//                JOptionPane.showMessageDialog(
//                        mainPanel,
//                        "Zapisano " + numberOfSavedOrders + " zamówień w Subiekcie",
//                        "Powiadomienie",
//                        JOptionPane.INFORMATION_MESSAGE
//                );
//            });
//
//        }).start();
    }

    @Override
    public void load() {

        if (isLoaded()) {
            return;
        }

        super.load();

        paginationTableGui.handleLoadTableExceptions();
    }

    public JPanel getMainPanel() {

        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        String[] tableHeaders = {"Id wiadomości email", "Adres email nadawcy", "Temat wiadomości", "Data"};

        paginationTableGui = new PaginationTableGui(tableHeaders, this::loadData, this::convertToRow);

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
        mainPanel.setPreferredSize(new Dimension(1920, 980));
        mainPanel.setRequestFocusEnabled(true);
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(32, 50, 40, 50), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
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
        gbc.gridy = 1;
        gbc.gridwidth = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 10.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 0, 0, 0);
        mainPanel.add(ordersPanelPlaceholder, gbc);
        ordersPanelPlaceholder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JToolBar toolBar1 = new JToolBar();
        toolBar1.setBorderPainted(false);
        toolBar1.setFloatable(false);
        toolBar1.setOpaque(false);
        toolBar1.setVisible(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
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
