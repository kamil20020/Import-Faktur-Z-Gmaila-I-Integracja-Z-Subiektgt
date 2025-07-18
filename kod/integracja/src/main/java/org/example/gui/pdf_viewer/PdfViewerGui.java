package org.example.gui.pdf_viewer;

import org.example.gui.ChangeableGui;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

public class PdfViewerGui extends ChangeableGui implements KeyListener {

    private JPanel mainPanel;
    private JLabel pdfFileContent;
    private JPanel drawPanel;

    private final JLayeredPane layeredPane;

    private GridBagConstraints mainPanelLayoutConstrains;

    private File loadedPdfFile = null;

    private int page = 0;
    private int numberOfPages = 1;

    public PdfViewerGui(Consumer<Rectangle2D.Float> onSelect) {

        loadMainPanelLayoutConstraints();

        drawPanel = new PdfDrawerGui(onSelect);

        $$$setupUI$$$();

        layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        layeredPane.add(pdfFileContent, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(drawPanel, JLayeredPane.PALETTE_LAYER); // wyższa warstwa

        JScrollPane scrollPane = new JScrollPane(layeredPane);

        mainPanel.removeAll();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.add(scrollPane, mainPanelLayoutConstrains);
        mainPanel.setFocusable(true);
        mainPanel.addKeyListener(this);
    }

    public PdfViewerGui() {

        this(null);
    }

    public Optional<File> handleSelectPdfFile() {

        Optional<File> gotFileOpt = FileDialogHandler.getLoadFileDialogSelectedPath("Wybieranie faktury", "*.pdf");

        if (gotFileOpt.isEmpty()) {
            return gotFileOpt;
        }

        loadedPdfFile = gotFileOpt.get();

        loadPage(page);

        return gotFileOpt;
    }

    private void loadPage(int searchPage) {

        new Thread(() -> {

            page = searchPage;

            PdfFileDetails pdfFileDetails = PdfFileRenderer.getDetails(loadedPdfFile, searchPage);

            BufferedImage image = pdfFileDetails.image();

            SwingUtilities.invokeLater(() -> {

                int width = image.getWidth();
                int height = image.getHeight();

                pdfFileContent.setIcon(new ImageIcon(image));

                layeredPane.setPreferredSize(new Dimension(width, height));
                layeredPane.setBounds(0, 0, width, height);

                pdfFileContent.setBounds(0, 0, width, height);
                drawPanel.setBounds(0, 0, width, height);

                numberOfPages = pdfFileDetails.numberOfPages();

                layeredPane.revalidate();
                layeredPane.repaint();
            });

        }).start();
    }

    private void loadMainPanelLayoutConstraints() {

        mainPanelLayoutConstrains = new GridBagConstraints();

        mainPanelLayoutConstrains.gridx = 0;
        mainPanelLayoutConstrains.gridy = 0;
        mainPanelLayoutConstrains.fill = GridBagConstraints.BOTH;
        mainPanelLayoutConstrains.weightx = 1;
        mainPanelLayoutConstrains.weighty = 1;
    }

    @Override
    public void keyTyped(KeyEvent e) {


    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (!didLoadedPdfFile()) {
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {

            if (page < numberOfPages - 1) {

                loadPage(page + 1);
            }

        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {

            if (page > 0) {

                loadPage(page - 1);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {


    }

    private boolean didLoadedPdfFile() {

        return loadedPdfFile != null;
    }

    public JPanel getMainPanel() {

        return mainPanel;
    }

    private void createUIComponents() {

        //TODO: place custom component creation code here
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Przeglądanie pdf");

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1274, 1000);
        frame.setLocationRelativeTo(null);

        PdfViewerGui pdfViewerGui = new PdfViewerGui();

        frame.add(pdfViewerGui.getMainPanel());

        frame.setVisible(true);

        pdfViewerGui.handleSelectPdfFile();
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
        mainPanel.setAutoscrolls(false);
        pdfFileContent = new JLabel();
        pdfFileContent.setAlignmentX(0.5f);
        pdfFileContent.setAutoscrolls(false);
        pdfFileContent.setHorizontalAlignment(0);
        pdfFileContent.setHorizontalTextPosition(0);
        pdfFileContent.setMaximumSize(new Dimension(0, 0));
        pdfFileContent.setMinimumSize(new Dimension(0, 0));
        pdfFileContent.setOpaque(true);
        pdfFileContent.setPreferredSize(new Dimension(0, 0));
        pdfFileContent.setText("");
        pdfFileContent.setVisible(true);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        mainPanel.add(pdfFileContent, gbc);
        drawPanel.setMinimumSize(new Dimension(0, 0));
        drawPanel.setOpaque(false);
        drawPanel.setVisible(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 10.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(drawPanel, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
