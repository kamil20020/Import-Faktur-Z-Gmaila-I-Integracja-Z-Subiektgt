package org.example.service;

import com.microsoft.credentialstorage.SecretStore;
import com.microsoft.credentialstorage.StorageProvider;
import com.microsoft.credentialstorage.model.StoredCredential;

public class SecureStorage {

    private static final SecretStore<StoredCredential> store = StorageProvider.getCredentialStorage(true, StorageProvider.SecureOption.REQUIRED);

    private static String CREDENTIALS_KEY_PREFIX;

    private SecureStorage() {


    }

    public static void load() {

        CREDENTIALS_KEY_PREFIX = PropertiesService.getProperty("secure-store.credentials-key") + "-";
    }

    public static boolean doesExist(String credentialsKeyPostfix) {

        String credentialsKey = getCredentialsKey(credentialsKeyPostfix);

        return store.get(credentialsKey) != null;
    }

    public static void saveCredentials(String credentialsKeyPostfix, String password) {

        saveCredentials(credentialsKeyPostfix, credentialsKeyPostfix, password);
    }

    public static void saveCredentials(String credentialsKeyPostfix, String username, String password) {

        StoredCredential storedCredential = new StoredCredential(username, password.toCharArray());

        String credentialsKey = getCredentialsKey(credentialsKeyPostfix);

        store.add(credentialsKey, storedCredential);
    }

    public static String getCredentialsPassword(String credentialsKeyPostfix) throws IllegalArgumentException {

        String credentialsKey = getCredentialsKey(credentialsKeyPostfix);

        StoredCredential gotStoredCredentials = store.get(credentialsKey);

        if (gotStoredCredentials == null) {
            throw new IllegalArgumentException("Secure storage did not find given credentials: " + credentialsKey);
        }

        char[] storedPassword = gotStoredCredentials.getPassword();

        return new String(storedPassword);
    }

    public static boolean delete(String credentialsKeyPostfix) {

        if (!doesExist(credentialsKeyPostfix)) {

            return false;
        }

        String credentialsKey = getCredentialsKey(credentialsKeyPostfix);

        return store.delete(credentialsKey);
    }

    private static String getCredentialsKey(String credentialsKeyPostfix) {

        return CREDENTIALS_KEY_PREFIX + credentialsKeyPostfix;
    }

}
