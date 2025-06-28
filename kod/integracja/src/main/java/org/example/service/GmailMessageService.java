package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.api.Api;
import org.example.api.gmail.GmailMessageApi;
import org.example.api.gmail.response.MessageDetails;
import org.example.api.gmail.response.MessagesPageResponse;
import org.example.external.gmail.Message;
import org.example.external.gmail.MessageContentPartBody;
import org.example.external.gmail.MessageHeader;
import org.example.external.gmail.MessageSummary;
import org.example.mapper.gmail.GmailDateMapper;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class GmailMessageService {

    private final GmailMessageApi gmailMessageApi;

    private static final ExecutorService executorService = Executors.newFixedThreadPool(8);

    public MessagesPageResponse getPage(int pageSize, String pageToken) throws IllegalStateException{

        HttpResponse<String> gotResponse = gmailMessageApi.getPage(pageSize, pageToken);

        if(gotResponse.statusCode() != 200){

            throw new IllegalStateException("Could not get messages");
        }

        return Api.extractBody(gotResponse, MessagesPageResponse.class);
    }

    public List<Message> extractMessages(List<MessageHeader> messageHeaders) throws IllegalStateException{

        if(messageHeaders == null){

            return new ArrayList<>();
        }

        List<Message> messages = Collections.synchronizedList(new ArrayList<>());

        List<Callable<Message>> messagesTasks = new ArrayList<>();

        for(MessageHeader messageHeader : messageHeaders){

            String messageId = messageHeader.id();

            Callable<Message> task = () -> {

                try{

                    return getMessageById(messageId);
                }
                catch (IllegalStateException e){

                    e.printStackTrace();
                }

                return null;
            };

            messagesTasks.add(task);
        }

        try{

            List<Future<Message>> futures = executorService.invokeAll(messagesTasks);

            for(Future<Message> future : futures){

                if(future.isCancelled()){

                    throw new IllegalStateException("Could not get gmail message");
                }

                Message gotMessage = future.get();

                messages.add(gotMessage);
            }
        }
        catch (InterruptedException | ExecutionException e) {

            e.printStackTrace();

            throw new IllegalStateException("Could not get gmail messages");
        }

        return messages;
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

        String rawDate = messageSummary.date();

        OffsetDateTime date = GmailDateMapper.fromStr(rawDate);

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

    public static void main(String[] args){

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .optionalStart()
                .appendPattern("EEE, ")
            .optionalEnd()
            .appendPattern("d MMM yyyy HH:mm:ss Z")
            .optionalStart()
                .appendLiteral(" (")
                .appendZoneText(TextStyle.SHORT)
                .appendLiteral(")")
            .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        String rawDate = "Fri, 27 Jun 2025 09:22:32 +0200 (CEST)";
        String rawDate1 = "Fri, 27 Jun 2025 09:22:32 +0200";
        String rawDate2 = "Mon, 9 Jun 2025 17:10:08 +0200";

        OffsetDateTime date = OffsetDateTime.parse(rawDate2, formatter);

        System.out.println(date);

    }

}
