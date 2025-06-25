package org.example.api.gmail.general;

import org.example.api.BasicAuthApi;
import org.example.api.response.BasicAuthData;
import org.example.service.PropertiesService;
import org.example.service.SecureStorageService;
import org.springframework.stereotype.Service;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public abstract class GmailBasicAuthApi extends BasicAuthApi {

    protected static String clientId = "";
    protected static String secret = "";

    private static boolean isInitialized = false;

    private static final String CLIENT_ID_PROPERTY_NAME = "gmail.client.id";
    private static final String secretPrePostfix = "gmail.";

    public GmailBasicAuthApi(String subDomain, String laterPrefix, String hostPropertyName, PropertiesService propertiesService) {

        super(subDomain, laterPrefix, hostPropertyName, propertiesService);
    }

    public GmailBasicAuthApi(String laterPrefix, String hostPropertyName, PropertiesService propertiesService) {

        super(laterPrefix, hostPropertyName, propertiesService);
    }

    public static synchronized void init(SecureStorageService secureStorageService, PropertiesService propertiesService){

        if(isInitialized){
            return;
        }

        Optional<BasicAuthData> basicAuthDataOpt = BasicAuthApi.init(CLIENT_ID_PROPERTY_NAME, secretPrePostfix, secureStorageService, propertiesService);

        if(basicAuthDataOpt.isEmpty()){
            return;
        }

        BasicAuthData basicAuthData = basicAuthDataOpt.get();

        secret = basicAuthData.secret();
        clientId = basicAuthData.clientId();

        isInitialized = true;
    }

    public static String getSecretKeyPropertyName(){

        return BasicAuthApi.getSecretKeyPropertyName(secretPrePostfix);
    }

}
