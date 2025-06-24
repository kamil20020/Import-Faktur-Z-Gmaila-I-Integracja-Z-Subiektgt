package org.example.api.gmail;

import org.example.api.gmail.general.GmailBearerAuthApi;
import org.example.exception.UnloggedException;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GmailMessageApi extends GmailBearerAuthApi {

    public GmailMessageApi() {

        super("/gmail/v1/users/me/messages");
    }

    public HttpResponse<String> getPage(Integer pageSize, String pageToken) throws IllegalStateException, UnloggedException {

        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(API_PREFIX + getQueryParamsPostFix(
                "maxResults", pageSize.toString(),
                "q", "to:me has:attachment subject:faktura filename:pdf",
                "pageToken", pageToken
            )))
            .header("Content-Type", "application/json");

        return super.send(httpRequestBuilder);
    }

    public HttpResponse<String> getMessageById(String messageId) throws IllegalStateException, UnloggedException{

        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(API_PREFIX + "/" + messageId))
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

}
