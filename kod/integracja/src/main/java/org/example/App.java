package org.example;

import jakarta.mail.MessagingException;
import org.example.api.gmail.general.GmailBasicAuthApi;
import org.example.service.PropertiesService;
import org.example.service.SecureStorage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Hello world!
 */
@SpringBootApplication
public class App {

    public static void main(String[] args) throws MessagingException {

        PropertiesService.init();
        SecureStorage.load();

        GmailBasicAuthApi.init();
//
//        GmailBearerAuthApi.init(gmailLoginTokenApi::refreshAccessToken);

//        new Window();

        SpringApplicationBuilder builder = new SpringApplicationBuilder(App.class);

        builder.headless(false).run(args);

//        TemplateService templateService = new TemplateService();
//
//        String templateFilePath = "schemas/subiekt.json";
//        File gotFile = FileReader.getFileFromInside("invoices/fra z nr.pdf");
//
//        TemplateCombinedData templateCombinedData = templateService.applyTemplate(templateFilePath, gotFile);

//        Template pdfFileTemplate = Template.load("schemas/garden-parts.json");
//        File gotFile = FileReader.getFileFromInside("invoices/329157-2025.pdf");

//        Template pdfFileTemplate = Template.load("schemas/rozkwit.json");
//        File gotFile = FileReader.getFileFromInside("invoices/FS015302025B26A.pdf");

//        System.out.println(templateCombinedData);

        System.out.println("Hello World! :D");

//        AuthService authService = new GmailAuthService(gmailLoginCodeApi, gmailLoginTokenApi);
//
////        authService.init("./auth-data.json");
////
////        authService.initSecret("Mail-Subiekt-11%");
//
//        System.out.println(authService.isUserLogged());
//        System.out.println(authService.doesUserPassedFirstLoginToApp());

//        authService.generateCode();

//        GmailAccessTokenResponse gmailAccessTokenResponse = authService.generateDeviceCodeAndVerification();
//
//        Scanner scanner = new Scanner(System.in);
//
//        scanner.nextLine();
//
//        authService.login(gmailAccessTokenResponse.deviceCode());
//
////        MailService mailService = new MailService();
////
////        mailService.createConnection();
//
//        GmailMessageApi gmailMessageApi = new GmailMessageApi();
//
//        gmailMessageApi.getAll();
    }
}
