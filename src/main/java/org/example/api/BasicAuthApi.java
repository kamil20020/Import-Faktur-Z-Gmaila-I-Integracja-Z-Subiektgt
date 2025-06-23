package org.example.api;

import org.example.api.response.BasicAuthData;
import org.example.mapper.Base64Mapper;
import org.example.service.PropertiesService;
import org.example.service.SecureStorage;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class BasicAuthApi extends Api {

    protected static final String SECRET_POSTFIX = "secret";

    public BasicAuthApi(String subDomain, String laterPrefix, String hostPropertyName) {

        super(subDomain, laterPrefix, hostPropertyName);
    }

    public BasicAuthApi(String laterPrefix, String hostPropertyName) {

        super(laterPrefix, hostPropertyName);
    }

    protected static BasicAuthData init(String clientIdPropertyName, String secretPrePostfix){

        String key = getSecretKeyPropertyName(secretPrePostfix);

        if(!SecureStorage.doesExist(key)){

            return new BasicAuthData(null, "");
        }

        String clientId = PropertiesService.getProperty(clientIdPropertyName);
        String secret = SecureStorage.getCredentialsPassword(key);

        return new BasicAuthData(
            clientId,
            secret
        );
    }

    protected static String getSecretKeyPropertyName(String secretPrePostfix){

        return secretPrePostfix + SECRET_POSTFIX;
    }

}
