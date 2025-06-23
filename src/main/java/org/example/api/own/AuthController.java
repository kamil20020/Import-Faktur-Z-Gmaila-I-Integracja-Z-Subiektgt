package org.example.api.own;

import org.example.api.LoginTokenApi;
import org.example.api.gmail.login.GmailLoginTokenApi;
import org.example.service.auth.GmailAuthService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("https://accounts.google.com/")
@RestController
public class AuthController {

    private final GmailAuthService gmailAuthService;

    public AuthController(){

        LoginTokenApi loginTokenApi = new GmailLoginTokenApi();

        gmailAuthService = new GmailAuthService(null, loginTokenApi);
    }

    @GetMapping()
    public String handleAuthorizationCode(@RequestParam("code") String code){

        gmailAuthService.login(code);

        return "Proszę o powrót do aplikacji Integracja maili z Subiekt GT";
    }

    @GetMapping("/token")
    public String handleToken(){

        return "123";
    }

}
