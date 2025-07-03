package org.example;

import jakarta.mail.MessagingException;
import org.example.api.gmail.general.GmailBasicAuthApi;
import org.example.api.sfera.SferaApi;
import org.example.service.LogService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.text.Normalizer;

/**
 * Hello world!
 */
@SpringBootApplication
public class App {

    public static void main(String[] args) throws MessagingException {

        LogService.init();

        SpringApplicationBuilder builder = new SpringApplicationBuilder(App.class);

        builder.headless(false).run(args);

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
//
//        GmailMessageApi gmailMessageApi = new GmailMessageApi();
//
//        gmailMessageApi.getAll();
    }
}
