package org.example.api.own;

import lombok.RequiredArgsConstructor;
import org.example.gui.Window;
import org.example.service.auth.GmailAuthService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("https://accounts.google.com/")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final GmailAuthService gmailAuthService;
    private final Window window;

    @GetMapping()
    public String handleAuthorizationCode(@RequestParam("code") String code){

        gmailAuthService.login(code);

        window.handleSuccessAuth();

        return "Proszę o powrót do aplikacji Integracja maili z Subiekt GT";
    }

}
