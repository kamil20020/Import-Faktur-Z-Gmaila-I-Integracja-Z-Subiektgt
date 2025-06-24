package org.example.api.gmail.general;

import org.example.api.BearerAuthApi;
import org.example.api.response.BearerAuthData;
import org.example.service.SecureStorage;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Function;

public class GmailBearerAuthApi extends BearerAuthApi {

    private static BearerAuthData bearerAuthData = new BearerAuthData(null, null, "");

    private static final String secretPrePostfix = "gmail.";

    private static Function<String, HttpResponse<String>> refreshAccessToken;

    private static final String HOST_PROPERTY_NAME = "gmail.mail.host";

    public GmailBearerAuthApi(String subDomain, String laterPrefix) {

        super(subDomain, laterPrefix, HOST_PROPERTY_NAME);
    }

    public GmailBearerAuthApi(String laterPrefix) {

        super(laterPrefix, HOST_PROPERTY_NAME);
    }

    public static void init(Function<String, HttpResponse<String>> refreshAccessToken1){

        bearerAuthData = BearerAuthApi.init(secretPrePostfix);

        refreshAccessToken = refreshAccessToken1;
    }

    @Override
    public HttpResponse<String> send(HttpRequest.Builder httpRequestBuilder) throws IllegalStateException {

        return super.send(httpRequestBuilder, bearerAuthData, refreshAccessToken, secretPrePostfix);
    }

    public static void saveAuthData(String accessToken, String refreshToken){

        bearerAuthData = BearerAuthApi.saveAuthData(accessToken, refreshToken, secretPrePostfix);
    }

    public static void logout(){

        bearerAuthData = BearerAuthApi.logout(secretPrePostfix);
    }

    public static boolean isUserLogged(){

        return BearerAuthApi.isUserLogged(bearerAuthData);
    }

    public static String getAccessToken(){

        return bearerAuthData.accessToken();
    }

}
