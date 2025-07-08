package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.api.Api;
import org.example.api.gmail.GmailMessageApi;
import org.example.api.gmail.response.MessageDetails;
import org.example.api.gmail.response.MessagesPageResponse;
import org.example.mapper.gmail.GmailDataMapper;
import org.example.model.gmail.generated.MessageHeader;
import org.example.model.gmail.own.Message;
import org.example.model.gmail.own.MessageAttachment;
import org.example.model.gmail.generated.MessageContentPartBody;
import org.example.mapper.gmail.GmailDateMapper;
import org.example.model.gmail.own.MessageSummary;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class GmailMessageService {

    private final GmailMessageApi gmailMessageApi;

    private static final ExecutorService executorService = Executors.newFixedThreadPool(8);

    public MessagesPageResponse getPage(int pageSize, String pageToken, String subject) throws IllegalStateException{

        HttpResponse<String> gotResponse = gmailMessageApi.getPage(pageSize, pageToken, subject);

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

        return getMessage(messageSummary);
    }

    public Message getMessage(MessageSummary messageSummary){

        String messageId = messageSummary.id();
        List<String> attachmentsIds = messageSummary.attachmentsIds();

        String rawDate = messageSummary.date();

        OffsetDateTime date = GmailDateMapper.fromStr(rawDate);

        return Message.builder()
            .id(messageId)
            .from(messageSummary.from())
            .content(messageSummary.content())
            .subject(messageSummary.subject())
            .date(date)
            .attachmentsIds(attachmentsIds)
            .build();
    }

    public List<MessageAttachment> getMessagesAttachments(List<Message> messages){

        List<MessageAttachment> messageAttachments = new ArrayList<>();

        for (Message message : messages) {

            List<String> messageAttachmentsIds = message.getAttachmentsIds();

            for (int messageAttachmentIndex = 0; messageAttachmentIndex < messageAttachmentsIds.size(); messageAttachmentIndex++) {

                String messageAttachmentId = messageAttachmentsIds.get(messageAttachmentIndex);

                MessageAttachment messageAttachment = new MessageAttachment(message, messageAttachmentId, messageAttachmentIndex);

                messageAttachments.add(messageAttachment);
            }
        }

        return messageAttachments;
    }

    public byte[] getMessageAttachmentData(String messageId, String attachmentId){

        HttpResponse<String> gotResponse = gmailMessageApi.getMessageAttachment(messageId, attachmentId);

        if(gotResponse.statusCode() != 200){

            throw new IllegalStateException("Could not get message " + messageId + " attachment " + attachmentId);
        }

        MessageContentPartBody gotAttachmentResponse = Api.extractBody(gotResponse, MessageContentPartBody.class);

        String rawData = gotAttachmentResponse.getData();

        return GmailDataMapper.decode(rawData);
    }

    public void redirectToMessage(String messageId){

        gmailMessageApi.redirectToMessage(messageId);
    }

}
