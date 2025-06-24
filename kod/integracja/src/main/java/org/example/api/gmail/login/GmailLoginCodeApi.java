package org.example.api.gmail.login;

import org.example.api.Api;
import org.example.api.LoginCodeApi;
import org.example.api.gmail.general.GmailBasicAuthApi;
import org.springframework.stereotype.Service;

@Service
public class GmailLoginCodeApi extends GmailBasicAuthApi implements LoginCodeApi {

    private static final String HOST_PROPERTY_NAME = "gmail.auth.code.host";

    public GmailLoginCodeApi() {

        super("/o/oauth2/v2/auth", HOST_PROPERTY_NAME);
    }

    @Override
    public void generateCode() {

        Api.redirect(
            API_PREFIX,
            "client_id", clientId,
            "response_type", "code",
            "access_type", "offline",
            "prompt", "consent",
            "redirect_uri", "http://localhost:9000",
            "scope", "https://www.googleapis.com/auth/gmail.readonly"
        );
    }

}
