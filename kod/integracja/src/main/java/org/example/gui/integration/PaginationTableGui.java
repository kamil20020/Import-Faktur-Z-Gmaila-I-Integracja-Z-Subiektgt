package org.example.gui.integration;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PaginationTableGui extends JPanel {

    private JPanel mainPanel;

    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane tableScroll;

    private JToolBar pagination;
    private JButton previousButton;
    private JButton nextButton;

    private JToolBar paginationSize;
    private JComboBox<Integer> paginationSizeSelector;
    private JLabel rowsInfo;
    private JButton currentPageButton;

    private final String[] tableHeaders;

    private List<Object[]> data;

    private final BiFunction<Integer, Integer, PaginationTableData> loadData;
    private final Function<Object, Object[]> convertToRow;

    private final Map<Integer, Predicate<Object[]>> filters = new HashMap<>();

    private int offset = 0;
    private int prevOffset = 0;
    private int pageSize = 10;
    private int totalNumberOfRows = 0;

    private volatile boolean isLoading = false;

    public PaginationTableGui(
            String[] tableHeaders,
            BiFunction<Integer, Integer, PaginationTableData> loadData,
            Function<Object, Object[]> convertToRow
    ) {

        this.tableHeaders = tableHeaders;
        this.loadData = loadData;
        this.convertToRow = convertToRow;

        $$$setupUI$$$();

        tableScroll.getVerticalScrollBar().setUnitIncrement(20);

        previousButton.addActionListener(e -> handlePreviousButton());

        nextButton.addActionListener(e -> handleNextButton());

        paginationSizeSelector.addActionListener(e -> handlePageSizeSelectorChange());

        currentPageButton.addActionListener(e -> handleActualPageButton());
    }

    public record PaginationTableData<T>(
            List<T[]> data,
            int totalNumberOfRows
    ) {
    }

    private void handlePreviousButton() {

        if (offset == 0) {

            return;
        }

        prevOffset = offset;

        offset -= pageSize;

        handleLoadTableExceptions();

        currentPageButton.setText(String.valueOf(getActualPage() + 1));
    }

    private void handleNextButton() {

        if (offset + pageSize >= totalNumberOfRows || data.isEmpty()) {
            return;
        }

        prevOffset = offset;

        offset += pageSize;

        handleLoadTableExceptions();

        currentPageButton.setText(String.valueOf(getActualPage() + 1));
    }

    private void handlePageSizeSelectorChange() {

        pageSize = getCurrentPageSize();

        handleLoadTableExceptions();
    }

    public void handleLoadTableExceptions() {

        mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            loadTable();
        } catch (IllegalAccessException e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(mainPanel, "Nie udało się załadować tabeli", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    public int getOffset() {

        return offset;
    }

    public int getPrevOffset() {

        return prevOffset;
    }

    private void loadTable() throws IllegalAccessException {

        isLoading = true;

        new Thread(() -> {

            PaginationTableData<Object> gotRawTableData = loadData.apply(offset, pageSize);

            if (gotRawTableData == null) {
                return;
            }

            data = convertRawData(gotRawTableData.data);

            SwingUtilities.invokeLater(() -> {

                loadTableDataRows();

                totalNumberOfRows = gotRawTableData.totalNumberOfRows();
                rowsInfo.setText("Wiersze " + (offset + 1) + " - " + (offset + tableModel.getRowCount()) + " z " + totalNumberOfRows);

                mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            });

            isLoading = false;

        }).start();
    }

    private <T> List<Object[]> convertRawData(List<T[]> rawData) {

        List<Object[]> convertedData = new ArrayList<>();

        for (Object gotDataRawRow : rawData) {

            Object[] newRow = convertToRow.apply(gotDataRawRow);

            convertedData.add(newRow);
        }

        return convertedData;
    }

    public void clearFilters() {

        filters.clear();

        loadTableDataRows();
    }

    public void addFilter(Integer filterIndex, Predicate<Object[]> filter) {

        filters.put(filterIndex, filter);

        loadTableDataRows();
    }

    public void removeFilter(Integer filterIndex) {

        if (!filters.containsKey(filterIndex)) {
            return;
        }

        filters.remove(filterIndex);

        loadTableDataRows();
    }

    private void loadTableDataRows() {

        tableModel.setNumRows(0);

        for (Object[] dataRow : data) {

            if (!testFilters(dataRow)) {
                continue;
            }

            tableModel.addRow(dataRow);
        }
    }

    private boolean testFilters(Object[] dataRow) {

        for (Predicate<Object[]> filter : filters.values()) {

            boolean filterPassed = filter.test(dataRow);

            if (!filterPassed) {
                return false;
            }
        }

        return true;
    }

    private void handleActualPageButton() {

        int currentPage = getActualPage();

        currentPageButton.setText(String.valueOf(currentPage + 1));

        prevOffset = offset;

        handleLoadTableExceptions();
    }

    private int getActualPage() {

        return offset / pageSize;
    }

    private int getNumberOfPages() {

        return (int) Math.ceil((double) totalNumberOfRows / (double) pageSize);
    }

    private Integer getCurrentPageSize() {

        String currentSelectedPageSize = (String) paginationSizeSelector.getSelectedItem();

        return Integer.valueOf(currentSelectedPageSize);
    }

    public JPanel getMainPanel() {

        return mainPanel;
    }

    public void selectAll() {

        table.selectAll();
    }

    public void unselectAll() {

        table.clearSelection();
    }

    public int[] getSelectedRowIndices() {

        return table.getSelectedRows();
    }

    public List<Object[]> getSelectedData() {

        List<Object[]> selectedData = new ArrayList<>();

        int[] selectedRowsIndices = getSelectedRowIndices();

        if (selectedRowsIndices.length == 0) {

            return selectedData;
        }

        int numberOfCols = table.getColumnCount();

        for (int selectedRowIndex : selectedRowsIndices) {

            Object[] colsValues = new Object[numberOfCols];

            for (int colIndex = 0; colIndex < numberOfCols; colIndex++) {

                colsValues[colIndex] = tableModel.getValueAt(selectedRowIndex, colIndex);
            }

            selectedData.add(colsValues);
        }

        return selectedData;
    }

    public void setSkipColumns(Integer[] skipColumns) {

        for (Integer skipColumn : skipColumns) {

            TableColumn tableColumn = table.getColumnModel().getColumn(skipColumn);

            tableColumn.setWidth(0);
            tableColumn.setMinWidth(0);
            tableColumn.setMaxWidth(0);
        }
    }

    public void updateRowCol(int rowIndex, int colIndex, String firstFieldId, Object newValue) throws IllegalStateException {

        Optional<Object[]> foundRowOpt = data.stream()
                .filter(dataRow -> Objects.equals(dataRow[0], firstFieldId))
                .findFirst();

        if (foundRowOpt.isEmpty()) {

            throw new IllegalStateException("Nie znaleziono wiersza o id " + firstFieldId);
        }

        Object[] foundRow = foundRowOpt.get();

        foundRow[colIndex] = newValue;

        tableModel.setValueAt(newValue, rowIndex, colIndex);
    }

    public void setOnClickColumn(int columnIndex, Consumer<String> handleClick) {

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getButton() != 3) { //other than right mouse button

                    return;
                }

                Point point = e.getPoint();

                int clickedRow = table.rowAtPoint(point);

                int selectedRowIndex = table.getSelectedRow();

                if (clickedRow == -1 || selectedRowIndex == -1 || clickedRow != selectedRowIndex) {

                    return;
                }

                String gotValue = (String) tableModel.getValueAt(selectedRowIndex, columnIndex);

                handleClick.accept(gotValue);
            }
        });
    }

    public boolean isLoading() {

        return isLoading;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        DefaultTableCellRenderer cellsCenterRenderer = new DefaultTableCellRenderer();

        cellsCenterRenderer.setHorizontalAlignment(JLabel.CENTER);

        tableModel = new DefaultTableModel(tableHeaders, 0);
        table = new JTable(tableModel);

        for (int i = 0; i < tableHeaders.length; i++) {

            TableColumn column = table.getColumnModel().getColumn(i);

            column.setCellRenderer(cellsCenterRenderer);
        }
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
        mainPanel.setFocusable(false);
        mainPanel.setMinimumSize(new Dimension(600, 414));
        mainPanel.setOpaque(false);
        mainPanel.setPreferredSize(new Dimension(800, 600));
        mainPanel.setRequestFocusEnabled(true);
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), "            ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        tableScroll = new JScrollPane();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.gridheight = 2;
        gbc.weightx = 10.0;
        gbc.weighty = 8.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 0, 10, 0);
        mainPanel.add(tableScroll, gbc);
        table.setCellSelectionEnabled(false);
        table.setEditingColumn(-1);
        table.setEditingRow(-1);
        table.setEnabled(true);
        table.setFillsViewportHeight(false);
        table.setFocusable(false);
        table.setRowHeight(33);
        table.setRowSelectionAllowed(true);
        tableScroll.setViewportView(table);
        pagination = new JToolBar();
        pagination.setBackground(new Color(-1114625));
        pagination.setBorderPainted(false);
        pagination.setFloatable(false);
        pagination.setForeground(new Color(-1));
        pagination.setOpaque(false);
        pagination.setVisible(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 10.0;
        gbc.weighty = 1.0;
        mainPanel.add(pagination, gbc);
        pagination.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        previousButton = new JButton();
        previousButton.setText("<");
        pagination.add(previousButton);
        final JToolBar.Separator toolBar$Separator1 = new JToolBar.Separator();
        pagination.add(toolBar$Separator1);
        final JToolBar.Separator toolBar$Separator2 = new JToolBar.Separator();
        pagination.add(toolBar$Separator2);
        currentPageButton = new JButton();
        currentPageButton.setText("1");
        pagination.add(currentPageButton);
        final JToolBar.Separator toolBar$Separator3 = new JToolBar.Separator();
        pagination.add(toolBar$Separator3);
        final JToolBar.Separator toolBar$Separator4 = new JToolBar.Separator();
        pagination.add(toolBar$Separator4);
        nextButton = new JButton();
        nextButton.setText(">");
        pagination.add(nextButton);
        rowsInfo = new JLabel();
        rowsInfo.setHorizontalAlignment(11);
        rowsInfo.setText("Wiersze 0 - 0 z 0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 50, 0, 0);
        mainPanel.add(rowsInfo, gbc);
        paginationSize = new JToolBar();
        paginationSize.setBorderPainted(false);
        paginationSize.setFloatable(false);
        paginationSize.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(paginationSize, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Liczba wierszy:");
        paginationSize.add(label1);
        final JToolBar.Separator toolBar$Separator5 = new JToolBar.Separator();
        paginationSize.add(toolBar$Separator5);
        paginationSizeSelector = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("10");
        defaultComboBoxModel1.addElement("25");
        defaultComboBoxModel1.addElement("50");
        defaultComboBoxModel1.addElement("100");
        paginationSizeSelector.setModel(defaultComboBoxModel1);
        paginationSize.add(paginationSizeSelector);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
