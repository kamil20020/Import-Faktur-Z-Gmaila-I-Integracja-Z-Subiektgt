package org.example.api.gmail.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GmailAccessTokenResponse (

    @JsonProperty("device_code")
    String deviceCode,

    @JsonProperty("user_code")
    String userCode,

    @JsonProperty("verification_url")
    String verificationUrl

){}
