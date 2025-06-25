package org.example.service.auth;

import org.example.api.LoginCodeApi;
import org.example.api.LoginTokenApi;
import org.example.api.gmail.general.GmailBasicAuthApi;
import org.example.api.gmail.general.GmailBearerAuthApi;
import org.example.exception.UnloggedException;
import org.example.gui.Window;
import org.example.service.PropertiesService;
import org.example.service.SecureStorageService;
import org.springframework.stereotype.Service;

@Service
public class GmailAuthService extends AuthService{

    private final SecureStorageService secureStorageService;
    private final PropertiesService propertiesService;
    private final GmailBearerAuthApi gmailBearerAuthApi;

    public GmailAuthService(LoginCodeApi loginCodeApi, LoginTokenApi loginTokenApi, SecureStorageService secureStorageService, PropertiesService propertiesService, GmailBearerAuthApi gmailBearerAuthApi) {

        super(loginCodeApi, loginTokenApi, GmailBasicAuthApi.getSecretKeyPropertyName(), secureStorageService);

        this.secureStorageService = secureStorageService;
        this.propertiesService = propertiesService;
        this.gmailBearerAuthApi = gmailBearerAuthApi;

        super.init("./auth-data.json");
    }

    @Override
    public void initSecret(String gotPassword) throws IllegalStateException, UnloggedException {

        super.initSecret(gotPassword, () -> GmailBasicAuthApi.init(secureStorageService, propertiesService));
    }

    @Override
    public void login(String deviceCode) throws IllegalStateException{

         super.login(deviceCode, (accessToken, refreshToken) -> {

             gmailBearerAuthApi.saveAuthData(accessToken, refreshToken);
        });
    }

    @Override
    public boolean isUserLogged() {

        return GmailBearerAuthApi.isUserLogged();
    }

    @Override
    public void logout() {

        gmailBearerAuthApi.logout();
    }

}
