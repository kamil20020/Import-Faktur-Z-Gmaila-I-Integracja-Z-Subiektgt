package org.example.service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public interface SecurityService {

    static byte[] decryptAes(byte[] key, byte[] encryptedValue) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        if(encryptedValue == null){
            return null;
        }

        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        SecretKey secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);

        aesCipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        return aesCipher.doFinal(encryptedValue);
    }

    static byte[] hashSha(byte[] value) throws NoSuchAlgorithmException {

        if(value == null){
            return null;
        }

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        byte[] gotHash = messageDigest.digest(value);

        // Convert to hex

        StringBuilder stringBuilder = new StringBuilder();

        for(byte hashValue : gotHash){

            stringBuilder.append(String.format("%02X", hashValue));
        }

        return stringBuilder.toString().toLowerCase().getBytes();
    }

}