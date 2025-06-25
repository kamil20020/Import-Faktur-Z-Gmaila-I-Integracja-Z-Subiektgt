package org.example.service.auth;

import org.example.api.Api;
import org.example.api.LoginCodeApi;
import org.example.api.LoginTokenApi;
import org.example.api.response.AccessTokenResponse;
import org.example.api.response.EncryptedLoginDetails;
import org.example.exception.UnloggedException;
import org.example.loader.JsonFileLoader;
import org.example.service.SecureStorageService;
import org.example.service.SecurityService;

import javax.crypto.BadPaddingException;
import java.io.File;
import java.net.http.HttpResponse;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.function.BiConsumer;

public abstract class AuthService {

    private EncryptedLoginDetails encryptedLoginDetails = new EncryptedLoginDetails("", "", "");

    private boolean isInitialized = false;

    private final String secretKeyPostfix;

    private final LoginCodeApi loginCodeApi;
    private final LoginTokenApi loginTokenApi;

    private final SecureStorageService secureStorageService;

    public AuthService(LoginCodeApi loginCodeApi, LoginTokenApi loginTokenApi, String secretKeyPostfix, SecureStorageService secureStorageService){

        this.loginCodeApi = loginCodeApi;
        this.loginTokenApi = loginTokenApi;

        this.secureStorageService = secureStorageService;

        this.secretKeyPostfix = secretKeyPostfix;
    }

    public boolean doesUserPassedFirstLoginToApp(){

        return secureStorageService.doesExist(secretKeyPostfix);
    }

    protected void init(String authDataFilePath) throws IllegalStateException{

        if(isInitialized){
            return;
        }

        if(doesUserPassedFirstLoginToApp()){
            return;
        }

        encryptedLoginDetails = JsonFileLoader.loadFromFileOutside(authDataFilePath, EncryptedLoginDetails.class);

        new File(authDataFilePath).delete();

        isInitialized = true;
    }

    public void initSecret(String gotPassword, Runnable initAuthApi) throws IllegalStateException, UnloggedException{

        if(gotPassword == null){

            throw new IllegalStateException("User is already logged in");
        }

        if(doesUserPassedFirstLoginToApp()){

            throw new IllegalStateException("The user was already logged in to the app for the first time");
        }

        byte[] base64EncryptedAes = encryptedLoginDetails.key().getBytes();
        byte[] decodedEncryptedAes = Base64.getDecoder().decode(base64EncryptedAes);
        String expectedAesHash = encryptedLoginDetails.keyHash();
        String base64EncryptedSecret = encryptedLoginDetails.secret();

        byte[] gotDecryptedAes = decryptAesWithUserPassword(gotPassword, decodedEncryptedAes);
        String gotDecryptedAesHash = hashValue(gotDecryptedAes);

        if(!gotDecryptedAesHash.equals(expectedAesHash)){

            throw new UnloggedException();
        }

        byte[] gotAllegroSecret = decryptAllegroSecret(gotDecryptedAes, base64EncryptedSecret);

        secureStorageService.saveCredentials(secretKeyPostfix, new String(gotAllegroSecret));

        initAuthApi.run();
    }

    private byte[] decryptAesWithUserPassword(String gotPassword, byte[] base64EncryptedAes) throws UnloggedException, IllegalStateException{

        byte[] gotPasswordBytes = gotPassword.getBytes();

        byte[] targetGotPasswordBytes = new byte[16];

        System.arraycopy(gotPasswordBytes, 0, targetGotPasswordBytes, 0, Math.min(gotPasswordBytes.length, targetGotPasswordBytes.length));

        try {
            return SecurityService.decryptAes(targetGotPasswordBytes, base64EncryptedAes);
        }
        catch (BadPaddingException e){

            throw new UnloggedException();
        }
        catch (Exception e) {

            e.printStackTrace();

            throw new IllegalStateException("Could not decrypt with user password");
        }
    }

    private String hashValue(byte[] value) throws IllegalStateException{

        byte[] gotHashValue;

        try {
            gotHashValue = SecurityService.hashSha(value);
        }
        catch (NoSuchAlgorithmException e) {

            e.printStackTrace();

            throw new IllegalStateException("Did not found hash algorithm");
        }

        return new String(gotHashValue);
    }

    private byte[] decryptAllegroSecret(byte[] decryptedAes, String base64EncryptedSecret) throws IllegalStateException{

        byte[] decodedEncryptedSecret = Base64.getDecoder().decode(base64EncryptedSecret);

        try {
            return SecurityService.decryptAes(decryptedAes, decodedEncryptedSecret);
        }
        catch (Exception e) {

            e.printStackTrace();

            throw new IllegalStateException("Could not decrypt allegro secret");
        }
    }

    public void generateCode() throws IllegalStateException{

        loginCodeApi.generateCode();
    }

    protected void login(String deviceCode, BiConsumer<String, String> handleSaveAuthDataToApi) throws IllegalStateException{

        HttpResponse<String> accessTokenResponse = loginTokenApi.generateAccessToken(deviceCode);

        if(accessTokenResponse.statusCode() != 200){
            throw new IllegalStateException("User did not authorized the device code");
        }

        AccessTokenResponse accessTokenContent = Api.extractBody(accessTokenResponse, AccessTokenResponse.class);

        String accessToken = accessTokenContent.accessToken();
        String refreshToken = accessTokenContent.refreshToken();

        handleSaveAuthDataToApi.accept(accessToken, refreshToken);
    }

    public abstract void initSecret(String gotPassword);

    public abstract void login(String deviceCode);

    public abstract boolean isUserLogged();

    public abstract void logout();

}
