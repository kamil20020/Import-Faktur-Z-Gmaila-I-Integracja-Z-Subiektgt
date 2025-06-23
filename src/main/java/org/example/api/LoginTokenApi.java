package org.example.api;

import java.net.http.HttpResponse;

public interface LoginTokenApi {

    HttpResponse<String> generateAccessToken(String deviceCode);
    HttpResponse<String> refreshAccessToken(String refreshToken);

}
