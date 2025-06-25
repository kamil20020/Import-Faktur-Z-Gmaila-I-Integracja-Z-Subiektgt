package org.example.service;

import com.microsoft.credentialstorage.SecretStore;
import com.microsoft.credentialstorage.StorageProvider;
import com.microsoft.credentialstorage.model.StoredCredential;
import org.springframework.stereotype.Service;

@Service
public class SecureStorageService {

    private static final SecretStore<StoredCredential> store = StorageProvider.getCredentialStorage(true, StorageProvider.SecureOption.REQUIRED);

    private final String CREDENTIALS_KEY_PREFIX;

    public SecureStorageService(PropertiesService propertiesService) {

        CREDENTIALS_KEY_PREFIX = propertiesService.getProperty("secure-store.credentials-key") + "-";
    }

    public boolean doesExist(String credentialsKeyPostfix) {

        String credentialsKey = getCredentialsKey(credentialsKeyPostfix);

        return store.get(credentialsKey) != null;
    }

    public void saveCredentials(String credentialsKeyPostfix, String password) {

        saveCredentials(credentialsKeyPostfix, credentialsKeyPostfix, password);
    }

    public void saveCredentials(String credentialsKeyPostfix, String username, String password) {

        StoredCredential storedCredential = new StoredCredential(username, password.toCharArray());

        String credentialsKey = getCredentialsKey(credentialsKeyPostfix);

        store.add(credentialsKey, storedCredential);
    }

    public String getCredentialsPassword(String credentialsKeyPostfix) throws IllegalArgumentException {

        String credentialsKey = getCredentialsKey(credentialsKeyPostfix);

        StoredCredential gotStoredCredentials = store.get(credentialsKey);

        if (gotStoredCredentials == null) {
            throw new IllegalArgumentException("Secure storage did not find given credentials: " + credentialsKey);
        }

        char[] storedPassword = gotStoredCredentials.getPassword();

        return new String(storedPassword);
    }

    public boolean delete(String credentialsKeyPostfix) {

        if (!doesExist(credentialsKeyPostfix)) {

            return false;
        }

        String credentialsKey = getCredentialsKey(credentialsKeyPostfix);

        return store.delete(credentialsKey);
    }

    private String getCredentialsKey(String credentialsKeyPostfix) {

        return CREDENTIALS_KEY_PREFIX + credentialsKeyPostfix;
    }

}
