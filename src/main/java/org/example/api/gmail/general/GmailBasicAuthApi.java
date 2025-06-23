package org.example.api.gmail.general;

import org.example.api.BasicAuthApi;
import org.example.api.response.BasicAuthData;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GmailBasicAuthApi extends BasicAuthApi {

    protected static String clientId = "";

    protected static String secret = "";

    private static final String CLIENT_ID_PROPERTY_NAME = "gmail.client.id";

    private static final String secretPrePostfix = "gmail.";

    public GmailBasicAuthApi(String subDomain, String laterPrefix, String hostPropertyName) {

        super(subDomain, laterPrefix, hostPropertyName);
    }

    public GmailBasicAuthApi(String laterPrefix, String hostPropertyName) {

        super(laterPrefix, hostPropertyName);
    }

    public static void init(){

        BasicAuthData basicAuthData = BasicAuthApi.init(CLIENT_ID_PROPERTY_NAME, secretPrePostfix);

        secret = basicAuthData.secret();
        clientId = basicAuthData.clientId();
    }

    public static String getSecretKeyPropertyName(){

        return BasicAuthApi.getSecretKeyPropertyName(secretPrePostfix);
    }

}
