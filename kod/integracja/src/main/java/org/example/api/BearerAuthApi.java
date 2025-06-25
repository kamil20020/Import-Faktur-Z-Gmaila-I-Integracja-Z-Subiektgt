package org.example.api;

import org.example.api.response.AccessTokenResponse;
import org.example.api.response.BasicAuthData;
import org.example.api.response.BearerAuthData;
import org.example.exception.UnloggedException;
import org.example.service.PropertiesService;
import org.example.service.SecureStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.function.Function;

public abstract class BearerAuthApi extends Api{

    private final SecureStorageService secureStorageService;

    private static boolean isInitialized = false;
    private static BearerAuthData cachedBearerAuthData;

    private static Integer refreshTokenExpiresIn;

    private static final String ACCESS_TOKEN_CREDENTIALS_KEY_POSTFIX = "access_token";
    private static final String REFRESH_TOKEN_CREDENTIALS_KEY_POSTFIX = "refresh_token";

    private static final Logger log = LoggerFactory.getLogger(Api.class);

    public BearerAuthApi(String subDomain, String laterPrefix, String hostPropertyName, PropertiesService propertiesService, SecureStorageService secureStorageService){

        super(subDomain, laterPrefix, hostPropertyName, propertiesService);

        this.secureStorageService = secureStorageService;
    }

    public BearerAuthApi(String laterPrefix, String hostPropertyName, PropertiesService propertiesService, SecureStorageService secureStorageService){

        super(laterPrefix, hostPropertyName, propertiesService);

        this.secureStorageService = secureStorageService;
    }

    protected static synchronized Optional<BearerAuthData> init(String keysPrePostfix, SecureStorageService secureStorageService){

        if(isInitialized){

            return Optional.of(cachedBearerAuthData);
        }

        String accessTokenKey = getAccessTokenKey(keysPrePostfix);
        String refreshTokenKey = getRefreshTokenKey(keysPrePostfix);

        if(!secureStorageService.doesExist(accessTokenKey)){

            return Optional.empty();
        }

        String accessToken = secureStorageService.getCredentialsPassword(accessTokenKey);
        String refreshToken = secureStorageService.getCredentialsPassword(refreshTokenKey);
        String bearerAuthContent = getBearerAuthContent(accessToken);

        isInitialized = true;

        cachedBearerAuthData = new BearerAuthData(
            accessToken,
            refreshToken,
            bearerAuthContent
        );

        return Optional.of(cachedBearerAuthData);
    }

    protected HttpResponse<String> send(HttpRequest.Builder httpRequestBuilder, BearerAuthData bearerAuthData, Function<String, HttpResponse<String>> refreshAccessToken, String keysPrePostfix) throws IllegalStateException, UnloggedException {

        httpRequestBuilder
            .header("Authorization", bearerAuthData.headerContent());

        HttpResponse<String> gotResponse = super.send(httpRequestBuilder);

        if (gotResponse.statusCode() == 401) {

            log.info("401 - Expired access token");

            log.info("Refreshing access token");

            if(!handleRefreshAccessToken(bearerAuthData.refreshToken(), refreshAccessToken, keysPrePostfix)){

                log.info("401 - Expired refresh token");

                throw new UnloggedException();
            }
            else{

                log.info("Refreshed access token");

                httpRequestBuilder
                    .header("Authorization", bearerAuthData.headerContent());

                gotResponse = super.send(httpRequestBuilder);
            }
        }

        return gotResponse;
    }

    private boolean handleRefreshAccessToken(String refreshToken, Function<String, HttpResponse<String>> refreshAccessToken, String keysPrePostfix) throws IllegalStateException{

        HttpResponse<String> gotResponse = refreshAccessToken.apply(refreshToken);

        if(gotResponse.statusCode() == 401 || gotResponse.statusCode() == 400){

            logout(keysPrePostfix);

            return false;
        }

        AccessTokenResponse accessTokenResponse = Api.extractBody(gotResponse, AccessTokenResponse.class);

        saveAuthData(accessTokenResponse.accessToken(), accessTokenResponse.refreshToken(), keysPrePostfix);

        return true;
    }

    protected BearerAuthData saveAuthData(String accessToken, String refreshToken, String keysPrePostfix){

        String accessTokenKey = getAccessTokenKey(keysPrePostfix);
        String refreshTokenKey = getRefreshTokenKey(keysPrePostfix);

        String bearerAuthContent = getBearerAuthContent(accessToken);

        secureStorageService.saveCredentials(accessTokenKey, accessToken);

        if(refreshToken != null){

            secureStorageService.saveCredentials(refreshTokenKey, refreshToken);
        }

        return new BearerAuthData(
            accessToken,
            refreshToken,
            bearerAuthContent
        );
    }

    protected BearerAuthData logout(String keysPrePostfix){

        String accessTokenKey = getAccessTokenKey(keysPrePostfix);
        String refreshTokenKey = getRefreshTokenKey(keysPrePostfix);

        secureStorageService.delete(accessTokenKey);
        secureStorageService.delete(refreshTokenKey);

        return new BearerAuthData(
            null,
            null,
            ""
        );
    }

    private static String getAccessTokenKey(String keysPrePostfix){

        return keysPrePostfix + ACCESS_TOKEN_CREDENTIALS_KEY_POSTFIX;
    }

    private static String getRefreshTokenKey(String keysPrePostfix){

        return keysPrePostfix + REFRESH_TOKEN_CREDENTIALS_KEY_POSTFIX;
    }

    private static String getBearerAuthContent(String token){

        return "Bearer " + token;
    }

    protected static boolean isUserLogged(BearerAuthData bearerAuthData){

        return bearerAuthData.accessToken() != null;
    }

}
