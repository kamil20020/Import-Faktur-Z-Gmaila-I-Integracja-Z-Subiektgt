package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.api.Api;
import org.example.api.gmail.response.MessagesPageResponse;
import org.example.api.sfera.request.CreateOrderRequest;
import org.example.exception.ConflictException;
import org.example.exception.FileReadException;
import org.example.model.gmail.generated.MessageHeader;
import org.example.model.gmail.own.Message;
import org.example.model.gmail.own.MessageAttachment;
import org.example.model.gmail.own.MessageAttachmentCombinedId;
import org.example.mapper.sfera.SferaOrderMapper;
import org.example.service.sfera.SferaOrderService;
import org.example.template.data.DataExtractedFromTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    public List<MessageAttachment> loadInvoicesDetails(List<MessageHeader> messagesHeaders){

        List<Message> gotMessages = gmailMessageService.extractMessages(messagesHeaders);

        List<MessageAttachment> messagesAttachments = gmailMessageService.getMessagesAttachments(gotMessages);

        messagesAttachments
            .forEach(this::loadInvoiceDetailsAfterGeneralLoad);

        return messagesAttachments;
    }

    public void loadInvoiceDetailsAfterGeneralLoad(MessageAttachment messageAttachment){

        MessageAttachmentCombinedId combinedId = messageAttachment.getCombinedId();

        String combinedIdStr = combinedId.toString();

        String invoiceExternalIdStr = sferaOrderService.getSubiektIdByExternalId(combinedIdStr);

        messageAttachment.setExternalId(invoiceExternalIdStr);
    }

    public Map<String, String> createInvoices(List<MessageAttachment> messageAttachments){

        Map<String, String> errors = new HashMap<>();

        for (MessageAttachment messageAttachment : messageAttachments) {

            try {

                createInvoice(messageAttachment);
            }
            catch (IllegalStateException | ConflictException e) {

                e.printStackTrace();

                log.error(e.getMessage());

                String messageId = messageAttachment.getMessageId();
                Integer index = messageAttachment.getIndex();

                errors.put(messageId + ", " + (index + 1), e.getMessage());
            }
        }

        return errors;
    }

    public void createInvoice(MessageAttachment messageAttachment) throws ConflictException, IllegalStateException {

        if(messageAttachment.getExternalId() != null){

            throw new ConflictException("Istnieje już wybrana faktura zakupu w Subiekcie");
        }

        String messageId = messageAttachment.getMessageId();
        String messageAttachmentId = messageAttachment.getId();

        byte[] messageData = gmailMessageService.getMessageAttachmentData(messageId, messageAttachmentId);

        DataExtractedFromTemplate dataExtractedFromTemplate;

        try{

            dataExtractedFromTemplate = templateService.applyGoodTemplateForData(messageData);
        }
        catch (FileReadException e){

            e.printStackTrace();

            throw new IllegalStateException(e.getMessage());
        }

        String messageAttachmentCombinedIdStr = messageAttachment.getCombinedId().toString();

        CreateOrderRequest request = SferaOrderMapper.map(dataExtractedFromTemplate, messageAttachmentCombinedIdStr);

        String gotExternalId = sferaOrderService.create(request);

        messageAttachment.setExternalId(gotExternalId);
    }

    public void redirectToMessage(String messageId){

        gmailMessageService.redirectToMessage(messageId);
    }

}
