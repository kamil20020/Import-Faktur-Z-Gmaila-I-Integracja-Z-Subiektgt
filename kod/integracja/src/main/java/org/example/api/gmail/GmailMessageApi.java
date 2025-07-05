package org.example.api.gmail;

import org.example.App;
import org.example.api.Api;
import org.example.api.LoginTokenApi;
import org.example.api.gmail.general.GmailBearerAuthApi;
import org.example.exception.UnloggedException;
import org.example.service.PropertiesService;
import org.example.service.SecureStorageService;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class GmailMessageApi extends GmailBearerAuthApi {

    public GmailMessageApi(LoginTokenApi loginTokenApi, PropertiesService propertiesService, SecureStorageService secureStorageService) {

        super("/gmail/v1/users/me/messages", propertiesService, secureStorageService);
    }

    public HttpResponse<String> getPage(Integer pageSize, String pageToken, String subject) throws IllegalStateException, UnloggedException {

        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(API_PREFIX + getQueryParamsPostFix(
                "maxResults", pageSize.toString(),
                "q", "\"" + subject + "\" in:inbox to:me has:attachment filename:pdf",
                "pageToken", pageToken
            )))
            .header("Content-Type", "application/json");

        return super.send(httpRequestBuilder);
    }

    public HttpResponse<String> getMessageById(String messageId) throws IllegalStateException, UnloggedException{

        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(API_PREFIX + "/" + messageId + getQueryParamsPostFix("format", "full")))
            .header("Content-Type", "application/json");

        return super.send(httpRequestBuilder);
    }

    public HttpResponse<String> getMessageAttachment(String messageId, String attachmentId) throws IllegalStateException, UnloggedException{

        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(API_PREFIX + "/" + messageId + "/attachments/" + attachmentId))
            .header("Content-Type", "application/json");

        return super.send(httpRequestBuilder);
    }

    public void redirectToMessage(String rawMessageId){

        String encodedMessageId = URLEncoder.encode(rawMessageId, StandardCharsets.UTF_8);

        Api.redirect(
            "https://mail.google.com/mail/u/0/#inbox/" + encodedMessageId
        );
    }

}
