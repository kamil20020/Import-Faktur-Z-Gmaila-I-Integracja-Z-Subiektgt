package org.example.service.auth;

import org.example.api.LoginCodeApi;
import org.example.api.LoginTokenApi;
import org.example.api.gmail.general.GmailBasicAuthApi;
import org.example.api.gmail.general.GmailBearerAuthApi;
import org.example.exception.UnloggedException;
import org.example.gui.Window;
import org.springframework.stereotype.Service;

@Service
public class GmailAuthService extends AuthService{

    public GmailAuthService(LoginCodeApi loginCodeApi, LoginTokenApi loginTokenApi) {

        super(loginCodeApi, loginTokenApi, GmailBasicAuthApi.getSecretKeyPropertyName());

        GmailBearerAuthApi.init(loginTokenApi::refreshAccessToken);
    }

    @Override
    public void initSecret(String gotPassword) throws IllegalStateException, UnloggedException {

        super.initSecret(gotPassword, () -> GmailBasicAuthApi.init());
    }

    @Override
    public void login(String deviceCode) throws IllegalStateException{

         super.login(deviceCode, (accessToken, refreshToken) -> {

            GmailBearerAuthApi.saveAuthData(accessToken, refreshToken);
        });
    }

    @Override
    public boolean isUserLogged() {

        return GmailBearerAuthApi.isUserLogged();
    }

    @Override
    public void logout() {

        GmailBearerAuthApi.logout();
    }

}
