package org.example.api.sfera;

import org.example.api.Api;
import org.example.api.sfera.request.GeneralRequest;
import org.example.service.PropertiesService;
import org.example.service.SecureStorageService;

public abstract class SferaApi extends Api {

    private static String apiKey;

    private static boolean isInitialized = false;

    private static final String HOST_KEY = "sfera.api.host";
    public static final String SFERA_SECRET_POSTFIX = "sfera.secret";

    public SferaApi(String subDomain, String laterPrefix, PropertiesService propertiesService) {

        super(subDomain, Api.getQueryParamsPostFix("c", laterPrefix), HOST_KEY, "http", propertiesService);
    }

    public SferaApi(String laterPrefix, PropertiesService propertiesService) {

        this(null, laterPrefix, propertiesService);
    }

    public static void init(SecureStorageService secureStorageService){

        if(isInitialized){
            return;
        }

        if(!secureStorageService.doesExist(SFERA_SECRET_POSTFIX)){
            return;
        }

        apiKey = secureStorageService.getCredentialsPassword(SFERA_SECRET_POSTFIX);

        isInitialized = true;
    }

    protected static GeneralRequest createGeneralRequest(Object data){

        return new GeneralRequest("l6cw8lrjmntwa9muu63ynkpoywt2wp", data);
    }

}
