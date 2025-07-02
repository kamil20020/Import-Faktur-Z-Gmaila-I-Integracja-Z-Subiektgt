package org.example.api.gmail.login;

import org.example.api.LoginTokenApi;
import org.example.api.gmail.general.GmailBasicAuthApi;
import org.example.api.gmail.general.GmailBearerAuthApi;
import org.example.service.PropertiesService;
import org.example.service.SecureStorageService;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GmailLoginTokenApi extends GmailBasicAuthApi implements LoginTokenApi {

    private static final String HOST_PROPERTY_NAME = "gmail.auth.token.host";

    public GmailLoginTokenApi(SecureStorageService secureStorageService, PropertiesService propertiesService) {

        super("/token", HOST_PROPERTY_NAME, propertiesService);

        GmailBasicAuthApi.init(secureStorageService, propertiesService);
        GmailBearerAuthApi.init(this::refreshAccessToken, secureStorageService);
    }

    @Override
    public HttpResponse<String> generateAccessToken(String deviceCode) {

        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.noBody())
            .uri(URI.create(API_PREFIX + getQueryParamsPostFix(
                "client_id", clientId,
                "client_secret", secret,
                "redirect_uri", "http://localhost:9000",
                "code", deviceCode,
                "grant_type", "authorization_code"
            )))
            .header("Content-Type", "application/x-www-form-urlencoded");

        return super.send(httpRequestBuilder);
    }

    @Override
    public HttpResponse<String> refreshAccessToken(String refreshToken) {

        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.noBody())
            .uri(URI.create(API_PREFIX + getQueryParamsPostFix(
                "client_id", clientId,
                "client_secret", secret,
                "refresh_token", refreshToken,
                "grant_type", "refresh_token"
            )))
            .header("Content-Type", "application/x-www-form-urlencoded");

        return super.send(httpRequestBuilder);
    }

}
