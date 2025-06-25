package org.example.api;

import org.example.api.response.BasicAuthData;
import org.example.service.PropertiesService;
import org.example.service.SecureStorageService;

import java.util.Optional;

public abstract class BasicAuthApi extends Api {

    private static boolean isInitialized = false;

    private static BasicAuthData cachedBasicAuthData;

    protected static final String SECRET_POSTFIX = "secret";

    public BasicAuthApi(String subDomain, String laterPrefix, String hostPropertyName, PropertiesService propertiesService) {

        super(subDomain, laterPrefix, hostPropertyName, propertiesService);
    }

    public BasicAuthApi(String laterPrefix, String hostPropertyName, PropertiesService propertiesService) {

        super(laterPrefix, hostPropertyName, propertiesService);
    }

    protected static synchronized Optional<BasicAuthData> init(String clientIdPropertyName, String secretPrePostfix, SecureStorageService secureStorageService, PropertiesService propertiesService){

        if(isInitialized){

            return Optional.of(cachedBasicAuthData);
        }

        String key = getSecretKeyPropertyName(secretPrePostfix);

        if(!secureStorageService.doesExist(key)){

            return Optional.empty();
        }

        String clientId = propertiesService.getProperty(clientIdPropertyName);
        String secret = secureStorageService.getCredentialsPassword(key);

        isInitialized = true;

        cachedBasicAuthData = new BasicAuthData(
            clientId,
            secret
        );

        return Optional.of(cachedBasicAuthData);
    }

    protected static String getSecretKeyPropertyName(String secretPrePostfix){

        return secretPrePostfix + SECRET_POSTFIX;
    }

}
