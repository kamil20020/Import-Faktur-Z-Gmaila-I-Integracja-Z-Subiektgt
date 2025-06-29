package org.example.service.unit;

import com.microsoft.credentialstorage.SecretStore;
import com.microsoft.credentialstorage.model.StoredCredential;
import org.example.TestUtils;
import org.example.service.PropertiesService;
import org.example.service.SecureStorageService;
import org.example.service.SecurityService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class SecureStorageTest {

    private SecretStore secretStoreMock = Mockito.mock(SecretStore.class);

    @Mock
    private PropertiesService propertiesServiceMock;

    @InjectMocks
    private SecureStorageService secureStorageService;

    private static final String expectedKeyPrefix = "prefix";
    private static final String expectedKeyPostfix = "key";
    private static final String expectedKey = "prefix-key";
    private static final String expectedUsername = "username";
    private static final String expectedPassword = "password";

    @BeforeEach
    public void setUp(){

        secretStoreMock = Mockito.mock(SecretStore.class);

        TestUtils.updatePrivateStaticField(SecureStorageService.class, "store", secretStoreMock);
    }

    private void setUpCredentialsKeyPrefix(String credentialsKeyPrefix){

        TestUtils.updatePrivateInstanceField(SecureStorageService.class, "CREDENTIALS_KEY_PREFIX", secureStorageService, credentialsKeyPrefix + "-");
    }

    @Test
    void shouldCreate(){

        String expectedCredentialsPrefix = "expected credentials prefix";
        String expectedCredentialsPrefixKey = "secure-store.credentials-key";
        String expectedValue = expectedCredentialsPrefix + "-";

        Mockito.when(propertiesServiceMock.getProperty(any())).thenReturn(expectedCredentialsPrefix);

        SecureStorageService secureStorageService = new SecureStorageService(propertiesServiceMock);

        String gotValue = TestUtils.getPrivateInstanceField(SecureStorageService.class, "CREDENTIALS_KEY_PREFIX", secureStorageService, String.class);

        assertEquals(expectedValue, gotValue);

        Mockito.verify(propertiesServiceMock, Mockito.times(2)).getProperty(expectedCredentialsPrefixKey);
    }

    @Test
    void shouldGetDoesExistWhenExist(){

        StoredCredential expectedValue = new StoredCredential("", new char[0]);

        setUpCredentialsKeyPrefix(expectedKeyPrefix);

        Mockito.when(secretStoreMock.get(any())).thenReturn(expectedValue);

        boolean doestExist = secureStorageService.doesExist(expectedKeyPostfix);

        assertTrue(doestExist);

        Mockito.verify(secretStoreMock).get(expectedKey);
    }

    @Test
    void shouldNotGetDoesExistWhenDoesNotExist(){

        setUpCredentialsKeyPrefix(expectedKeyPrefix);

        Mockito.when(secretStoreMock.get(any())).thenReturn(null);

        boolean doestExist = secureStorageService.doesExist(expectedKeyPostfix);

        assertFalse(doestExist);

        Mockito.verify(secretStoreMock).get(expectedKey);
    }

    @Test
    void shouldSaveCredentials(){

        setUpCredentialsKeyPrefix(expectedKeyPrefix);

        secureStorageService.saveCredentials(expectedKeyPostfix, expectedUsername, expectedPassword);

        ArgumentCaptor<StoredCredential> storedCredentialCaptor = ArgumentCaptor.forClass(StoredCredential.class);

        Mockito.verify(secretStoreMock).add(eq(expectedKey), storedCredentialCaptor.capture());

        StoredCredential gotStoredCredentials = storedCredentialCaptor.getValue();

        assertEquals(expectedUsername, gotStoredCredentials.getUsername());
        assertArrayEquals(expectedPassword.toCharArray(), gotStoredCredentials.getPassword());
    }

    @Test
    void shouldSaveCredentialsPassword(){

        setUpCredentialsKeyPrefix(expectedKeyPrefix);

        secureStorageService.saveCredentials(expectedKeyPostfix, expectedPassword);

        ArgumentCaptor<StoredCredential> storedCredentialCaptor = ArgumentCaptor.forClass(StoredCredential.class);

        Mockito.verify(secretStoreMock).add(eq(expectedKey), storedCredentialCaptor.capture());

        StoredCredential gotStoredCredentials = storedCredentialCaptor.getValue();

        assertEquals(expectedKeyPostfix, gotStoredCredentials.getUsername());
        assertArrayEquals(expectedPassword.toCharArray(), gotStoredCredentials.getPassword());
    }

    @Test
    void shouldGetCredentialsPassword(){

        setUpCredentialsKeyPrefix(expectedKeyPrefix);

        StoredCredential expectedStoredCredential = new StoredCredential(
            "username",
            expectedPassword.toCharArray()
        );

        Mockito.when(secretStoreMock.get(any())).thenReturn(expectedStoredCredential);

        String gotPassword = secureStorageService.getCredentialsPassword(expectedKeyPostfix);

        assertEquals(expectedPassword, gotPassword);

        Mockito.verify(secretStoreMock).get(expectedKey);
    }

    @Test
    void shouldNotGetCredentialsPasswordWhenKeyPostfixDoestNotExist(){

        setUpCredentialsKeyPrefix(expectedKeyPrefix);

        Mockito.when(secretStoreMock.get(any())).thenReturn(null);

        assertThrows(
            IllegalArgumentException.class,
            () -> secureStorageService.getCredentialsPassword(expectedKeyPostfix)
        );

        Mockito.verify(secretStoreMock).get(expectedKey);
    }

    @Test
    void shouldDelete() {

        setUpCredentialsKeyPrefix(expectedKeyPrefix);

        StoredCredential expectedStoredCredential = new StoredCredential(
            expectedUsername,
            expectedPassword.toCharArray()
        );

        Mockito.when(secretStoreMock.get(any())).thenReturn(expectedStoredCredential);
        Mockito.when(secretStoreMock.delete(any())).thenReturn(true);

        boolean wasDeleted = secureStorageService.delete(expectedKeyPostfix);

        assertTrue(wasDeleted);

        Mockito.verify(secretStoreMock).get(expectedKey);
        Mockito.verify(secretStoreMock).delete(expectedKey);
    }

    @Test
    void shouldNotDeleteWhenKeyPostfixDoestNotExist() {

        setUpCredentialsKeyPrefix(expectedKeyPrefix);

        Mockito.when(secretStoreMock.get(any())).thenReturn(null);

        boolean wasDeleted = secureStorageService.delete(expectedKeyPostfix);

        assertFalse(wasDeleted);

        Mockito.verify(secretStoreMock).get(expectedKey);
    }

}