package org.example.gui.integration;

import lombok.RequiredArgsConstructor;
import org.example.exception.UnloggedException;
import org.example.gui.ChangeableGui;
import org.example.service.auth.AuthService;
import org.example.service.auth.GmailAuthService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;

@Component
public class LoginGui extends ChangeableGui {

    private JPanel mainPanel;
    private JButton loginButton;

    private String deviceCode;

    private final AuthService authService;
    private Runnable handleSuccessAuth;

    public LoginGui(AuthService authService) {

        this.authService = authService;

        loginButton.addActionListener(e -> {

            if (!authService.doesUserPassedFirstLoginToApp()) {

                handleFirstLogin();
            }
            else if (deviceCode == null) {

                handleGenerateCode();
            }
            else {

                handleLogin();
            }
        });
    }

    public void setHandleSuccessAuth(Runnable handleSuccessAuth) {

        this.handleSuccessAuth = handleSuccessAuth;
    }

    public void handleLogout() {

        deviceCode = null;
        loginButton.setText("Połącz");
    }

    private void handleFirstLogin() {

        String gotUserPassword = JOptionPane.showInputDialog(
                mainPanel,
                "Proszę podać hasło do aplikacji",
                "Formularz logowania",
                JOptionPane.INFORMATION_MESSAGE
        );

        if (gotUserPassword == null) {
            return;
        }

        try {

            authService.initSecret(gotUserPassword);
        } catch (UnloggedException e) {

            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Wprowadzono niepoprawne hasło",
                    "Powiadomienie o błędzie",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        } catch (IllegalStateException e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Wystąpił błąd podczas logowania do aplikacji",
                    "Powiadomienie o błędzie",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        JOptionPane.showMessageDialog(
                mainPanel,
                "Zalogowano do aplikacji",
                "Powiadomienie",
                JOptionPane.INFORMATION_MESSAGE
        );

        loginButton.setText("Połącz");
    }

    private void handleGenerateCode() {

//        GenerateDeviceCodeResponse generateDeviceCodeResponse;

        try {
            authService.generateCode();
        } catch (IllegalStateException e) {

            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Nie udało się rozpocząć procedury łączenia aplikacji z Allegro",
                    "Powiadomienie o błędzie",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

//        String verificationUrlComplete = generateDeviceCodeResponse.getVerificationUriComplete();
//        deviceCode = generateDeviceCodeResponse.getDeviceCode();
//
//        handleVerificationUriComplete(verificationUrlComplete);
    }

    private void handleVerificationUriComplete(String verificationUrlComplete) {

        JOptionPane.showMessageDialog(
                mainPanel,
                """
                            Proszę o zaakceptowanie przyznania uprawnień aplikacji
                            do niektórych własnych danych w Allegro. Aplikacja
                            przekieruje do strony Allegro po naciśnięciu
                            przycisku OK
                        """,
                "Powiadomienie",
                JOptionPane.INFORMATION_MESSAGE
        );

        URI verificationUriComplete = URI.create(verificationUrlComplete);

        try {
            Desktop.getDesktop().browse(verificationUriComplete);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        loginButton.setText("Gotowe");
    }

    private void handleLogin() {

//        try {
//            authService.loginToAllegro(deviceCode);
//        } catch (IllegalStateException e) {
//
//            e.printStackTrace();
//
//            JOptionPane.showMessageDialog(
//                    mainPanel,
//                    "Prawdopodobnie nie zezwolono dostępu aplikacji do Allegro",
//                    "Powiadomienie o błędzie",
//                    JOptionPane.ERROR_MESSAGE
//            );
//
//            return;
//        }
//
//        handleSuccessAuth.run();
//
//        JOptionPane.showMessageDialog(
//                mainPanel,
//                "Pomyślnie połączono aplikację z Allegro",
//                "Powiadomienie",
//                JOptionPane.INFORMATION_MESSAGE
//        );
    }

    public JPanel getMainPanel() {

        return mainPanel;
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
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, -1, 26, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setHorizontalAlignment(0);
        label1.setHorizontalTextPosition(0);
        label1.setText("Łączenie aplikacji z Gmail");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(40, 0, 0, 0);
        mainPanel.add(label1, gbc);
        loginButton = new JButton();
        loginButton.setText("Logowanie do aplikacji");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 5.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(40, 0, 0, 0);
        mainPanel.add(loginButton, gbc);
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
