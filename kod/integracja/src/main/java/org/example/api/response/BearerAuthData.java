package org.example.api.response;

public record BearerAuthData(

    String accessToken,
    String refreshToken,
    String headerContent
){}
