package org.example.api.gmail.general;

import org.example.api.BearerAuthApi;
import org.example.api.response.BearerAuthData;
import org.example.service.PropertiesService;
import org.example.service.SecureStorageService;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.function.Function;

public abstract class GmailBearerAuthApi extends BearerAuthApi {

    private final SecureStorageService secureStorageService;

    private static boolean isInitialized = false;

    private static BearerAuthData bearerAuthData = new BearerAuthData(null, null, "");

    private static Function<String, HttpResponse<String>> refreshAccessToken;

    private static final String secretPrePostfix = "gmail.";
    private static final String HOST_PROPERTY_NAME = "gmail.mail.host";

    public GmailBearerAuthApi(String subDomain, String laterPrefix, PropertiesService propertiesService, SecureStorageService secureStorageService) {

        super(subDomain, laterPrefix, HOST_PROPERTY_NAME, propertiesService, secureStorageService);

        this.secureStorageService = secureStorageService;
    }

    public GmailBearerAuthApi(String laterPrefix, PropertiesService propertiesService, SecureStorageService secureStorageService) {

        super(laterPrefix, HOST_PROPERTY_NAME, propertiesService, secureStorageService);

        this.secureStorageService = secureStorageService;
    }

    public static synchronized void init(Function<String, HttpResponse<String>> refreshAccessToken1, SecureStorageService secureStorageService){

        if(isInitialized){
            return;
        }

        Optional<BearerAuthData> bearerAuthDataOpt = BearerAuthApi.init(secretPrePostfix, secureStorageService);

        if(bearerAuthDataOpt.isEmpty()){
            return;
        }

        bearerAuthData = bearerAuthDataOpt.get();

        refreshAccessToken = refreshAccessToken1;

        isInitialized = true;
    }

    @Override
    public HttpResponse<String> send(HttpRequest.Builder httpRequestBuilder) throws IllegalStateException {

        return super.send(httpRequestBuilder, bearerAuthData, refreshAccessToken, secretPrePostfix);
    }

    @Override
    protected void handleRefreshedAccessToken(BearerAuthData newBearerAuthData){

        bearerAuthData = newBearerAuthData;
    }

    public void saveAuthData(String accessToken, String refreshToken){

        bearerAuthData = super.saveAuthData(accessToken, refreshToken, secretPrePostfix);
    }

    public void logout(){

        bearerAuthData = super.logout(secretPrePostfix);
    }

    public static boolean isUserLogged(){

        return BearerAuthApi.isUserLogged(bearerAuthData);
    }

    public static String getAccessToken(){

        return bearerAuthData.accessToken();
    }

}
