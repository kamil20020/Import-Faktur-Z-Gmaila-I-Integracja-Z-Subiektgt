package org.example.service.integration;

import org.example.service.SecurityService;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class SecurityServiceTestIT {

    @Test
    void shouldDecryptAes() throws Exception {

        byte[] key = "1234567890123456".getBytes();

        byte[] rawText = "raw text".getBytes();

        String encryptedValueWithBase64 = "74KoNtbxkg7NlkXzoLVDBA==";
        byte[] encryptedValue = Base64.getDecoder().decode(encryptedValueWithBase64);

        byte[] decryptedValue = SecurityService.decryptAes(key, encryptedValue);

        assertArrayEquals(rawText, decryptedValue);
    }

    @Test
    void shouldNotDecryptAesWhenValueIsNull() throws Exception{

        byte[] gotValue = SecurityService.decryptAes(null, null);

        assertNull(gotValue);
    }

    @Test
    void shouldHashSha() throws Exception{

        byte[] rawValue = "raw value".getBytes();
        byte[] expectedHash = "c0b31b84a261c7fe82ab163d6f1e3068de1e17ba0c5e12a3c57669236ec91a5c".getBytes();

        byte[] gotHash = SecurityService.hashSha(rawValue);

        assertArrayEquals(expectedHash, gotHash);
    }

    @Test
    void shouldNotHashWhenValueIsNull() throws Exception{

        byte[] gotHash = SecurityService.hashSha(null);

        assertNull(gotHash);
    }

}