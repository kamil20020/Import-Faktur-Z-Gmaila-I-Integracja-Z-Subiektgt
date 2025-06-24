package org.example.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EncryptedLoginDetails(

    String key,
    String keyHash,
    String secret
){}
