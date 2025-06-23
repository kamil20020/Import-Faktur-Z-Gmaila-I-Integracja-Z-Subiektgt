package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.api.Api;
import org.example.api.gmail.GmailMessageApi;
import org.example.api.gmail.response.MessageDetails;
import org.example.api.gmail.response.MessagesPageResponse;
import org.example.external.gmail.Message;
import org.example.external.gmail.MessageContentPartBody;
import org.example.external.gmail.MessageSummary;
import org.example.mapper.Base64Mapper;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Base64;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final GmailMessageApi gmailMessageApi;

    public MessagesPageResponse getPage(int pageSize) throws IllegalStateException{

        HttpResponse<String> gotResponse = gmailMessageApi.getPage(pageSize);

        if(gotResponse.statusCode() != 200){

            throw new IllegalStateException("Could not get messages");
        }

        return Api.extractBody(gotResponse, MessagesPageResponse.class);
    }

    public Message getMessageById(String messageId) throws IllegalStateException{

        HttpResponse<String> gotResponse = gmailMessageApi.getMessageById(messageId);

        if(gotResponse.statusCode() != 200){

            throw new IllegalStateException("Could not get message " + messageId);
        }

        MessageDetails gotMessageDetails = Api.extractBody(gotResponse, MessageDetails.class);

        MessageSummary messageSummary = gotMessageDetails.getSummary();

        return getCompleteMessage(messageSummary);
    }

    private Message getCompleteMessage(MessageSummary messageSummary){

        String messageId = messageSummary.id();
        String attachmentId = messageSummary.attachmentId();

        byte[] attachmentData = getMessageAttachment(messageId, attachmentId);

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .optionalStart()
                .appendPattern("EEE, ")
            .optionalEnd()
            .appendPattern("dd MMM yyyy HH:mm:ss Z")
            .toFormatter(Locale.ENGLISH);

        OffsetDateTime date = OffsetDateTime.parse(messageSummary.date(), formatter);

        return Message.builder()
            .id(messageId)
            .from(messageSummary.from())
            .subject(messageSummary.subject())
            .date(date)
            .attachmentData(attachmentData)
            .build();
    }

    private byte[] getMessageAttachment(String messageId, String attachmentId){

        HttpResponse<String> gotResponse = gmailMessageApi.getMessageAttachment(messageId, attachmentId);

        if(gotResponse.statusCode() != 200){

            throw new IllegalStateException("Could not get message " + messageId + " attachment " + attachmentId);
        }

        MessageContentPartBody gotAttachmentResponse = Api.extractBody(gotResponse, MessageContentPartBody.class);

        String rawData = gotAttachmentResponse.data();

        rawData = rawData.replaceAll("\\s", "");

        int padding = 4 - (rawData.length() % 4);

        if (padding < 4) {

            rawData += "=".repeat(padding);
        }

        return Base64.getUrlDecoder().decode(rawData);
    }

}
