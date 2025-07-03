package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.api.Api;
import org.example.api.gmail.response.MessagesPageResponse;
import org.example.api.sfera.request.CreateOrderRequest;
import org.example.exception.ConflictException;
import org.example.exception.FileReadException;
import org.example.external.gmail.Message;
import org.example.external.gmail.MessageHeader;
import org.example.mapper.sfera.SferaOrderMapper;
import org.example.service.sfera.SferaOrderService;
import org.example.template.data.DataExtractedFromTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final TemplateService templateService;
    private final SferaOrderService sferaOrderService;
    private final GmailMessageService gmailMessageService;

    private static final Logger log = LoggerFactory.getLogger(Api.class);

    public MessagesPageResponse getMessagesPage(int pageSize, String pageToken, String subject) throws IllegalStateException{

        return gmailMessageService.getPage(pageSize, pageToken, subject);
    }

    public List<Message> loadInvoicesDetails(List<MessageHeader> messagesHeaders){

        List<Message> gotMessages = gmailMessageService.extractMessages(messagesHeaders);

        gotMessages
            .forEach(this::loadInvoiceDetailsAfterGeneralLoad);

        return gotMessages;
    }

    public void loadInvoiceDetailsAfterGeneralLoad(Message message){

        String messageId = message.getId();
        String messageExternalId = sferaOrderService.getSubiektIdByExternalId(messageId);

        message.setExternalId(messageExternalId);
    }

    public Map<String, String> createInvoices(List<Message> messages){

        Map<String, String> errors = new HashMap<>();

        for (Message message : messages) {

            String messageId = message.getId();

            try {

                createInvoice(message);
            }
            catch (IllegalStateException | ConflictException e) {

                e.printStackTrace();

                log.error(e.getMessage());

                errors.put(messageId, e.getMessage());
            }
        }

        return errors;
    }

    public void createInvoice(Message message) throws ConflictException, IllegalStateException {

        if(message.getExternalId() != null){

            throw new ConflictException("Istnieje już wybrana faktura zakupu w Subiekcie");
        }

        String messageId = message.getId();
        String messageAttachmentId = message.getAttachmentId();

        byte[] messageData = gmailMessageService.getMessageAttachment(messageId, messageAttachmentId);

        DataExtractedFromTemplate dataExtractedFromTemplate;

        try{

            dataExtractedFromTemplate = templateService.applyGoodTemplateForData(messageData);
        }
        catch (FileReadException e){

            e.printStackTrace();

            throw new IllegalStateException(e.getMessage());
        }

        CreateOrderRequest request = SferaOrderMapper.map(dataExtractedFromTemplate, message.getId());

        String gotExternalId = sferaOrderService.create(request);

        message.setExternalId(gotExternalId);
    }

    public void redirectToMessage(String messageId){

        gmailMessageService.redirectToMessage(messageId);
    }

}
