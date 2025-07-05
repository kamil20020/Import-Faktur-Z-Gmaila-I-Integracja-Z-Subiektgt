package org.example.service.unit;

import org.example.api.gmail.response.MessagesPageResponse;
import org.example.api.sfera.request.CreateOrderRequest;
import org.example.exception.ConflictException;
import org.example.exception.FileReadException;
import org.example.model.gmail.own.Message;
import org.example.model.gmail.own.MessageAttachment;
import org.example.model.gmail.own.MessageAttachmentCombinedId;
import org.example.model.gmail.generated.MessageHeader;
import org.example.mapper.sfera.SferaOrderMapper;
import org.example.service.GmailMessageService;
import org.example.service.InvoiceService;
import org.example.service.TemplateService;
import org.example.service.sfera.SferaOrderService;
import org.example.template.data.DataExtractedFromTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private TemplateService templateService;

    @Mock
    private SferaOrderService sferaOrderService;

    @Mock
    private GmailMessageService gmailMessageService;

    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    void shouldGetMessagesPage() {

        //given
        int expectedPageSize = 10;
        String expectedPageToken = "token";

        MessagesPageResponse expectedMessagesPageResponse = new MessagesPageResponse(
            null,
            null,
            null
        );

        //when
        Mockito.when(gmailMessageService.getPage(anyInt(), any(), any())).thenReturn(expectedMessagesPageResponse);

        MessagesPageResponse gotPage = invoiceService.getMessagesPage(expectedPageSize, expectedPageToken, "");

        //then
        assertNotNull(gotPage);
        assertEquals(expectedMessagesPageResponse, gotPage);

        Mockito.verify(gmailMessageService).getPage(expectedPageSize, expectedPageToken, "");
    }

    @Test
    void shouldLoadInvoiceDetailsAfterGeneralLoad() {

        //given
        String messageId = "message-id";
        Integer attachmentIndex = 1;

        MessageAttachmentCombinedId messageAttachmentCombinedId = MessageAttachmentCombinedId.builder()
            .messageId(messageId)
            .attachmentIndex(attachmentIndex)
            .build();

        String messageAttachmentIdStr = messageAttachmentCombinedId.toString();

        MessageAttachment messageAttachment = MessageAttachment.builder()
            .combinedId(messageAttachmentCombinedId)
            .build();

        String expectedExternalId = "external-id";

        //when
        Mockito.when(sferaOrderService.getSubiektIdByExternalId(any())).thenReturn(expectedExternalId);

        invoiceService.loadInvoiceDetailsAfterGeneralLoad(messageAttachment);

        //then
        assertEquals(expectedExternalId, messageAttachment.getExternalId());

        Mockito.verify(sferaOrderService).getSubiektIdByExternalId(messageAttachmentIdStr);
    }

    @Test
    void shouldLoadInvoicesDetails() {

        //given
        MessageHeader messageHeader = new MessageHeader(null, null);

        List<MessageHeader> messageHeaders = List.of(messageHeader);

        Message message = new Message();

        List<Message> messages = List.of(message);

        MessageAttachment expectedMessageAttachment = MessageAttachment.builder()
            .combinedId(new MessageAttachmentCombinedId("12", 1))
            .build();

        MessageAttachment expectedMessageAttachment1 = MessageAttachment.builder()
            .combinedId(new MessageAttachmentCombinedId("22", 2))
            .build();

        List<MessageAttachment> expectedMessagesAttachments = List.of(expectedMessageAttachment, expectedMessageAttachment1);

        //when
        Mockito.when(gmailMessageService.extractMessages(any())).thenReturn(messages);
        Mockito.when(gmailMessageService.getMessagesAttachments(any())).thenReturn(expectedMessagesAttachments);

        List<MessageAttachment> gotMessagesAttachments = invoiceService.loadInvoicesDetails(messageHeaders);

        //then
        assertNotNull(gotMessagesAttachments);
        assertFalse(gotMessagesAttachments.isEmpty());
        assertEquals(expectedMessagesAttachments.size(), gotMessagesAttachments.size());
        assertTrue(gotMessagesAttachments.containsAll(expectedMessagesAttachments));

        Mockito.verify(gmailMessageService).extractMessages(messageHeaders);
        Mockito.verify(gmailMessageService).getMessagesAttachments(messages);

        for(MessageAttachment messageAttachment : expectedMessagesAttachments){

            String expectedMessageAttachmentCombinedIdStr = messageAttachment.getCombinedId().toString();

            Mockito.verify(sferaOrderService).getSubiektIdByExternalId(expectedMessageAttachmentCombinedIdStr);
        }
    }

    @Test
    void shouldCreateInvoice() {

        //given
        String messageId = "message-id";
        Integer attachmentIndex = 1;
        String attachmentId = "attachment-id";

        byte[] messageAttachmentData = "message-attachment-data".getBytes(StandardCharsets.UTF_8);

        MessageAttachmentCombinedId messageAttachmentCombinedId = new MessageAttachmentCombinedId(messageId, attachmentIndex);

        MessageAttachment messageAttachment = MessageAttachment.builder()
            .id(attachmentId)
            .combinedId(messageAttachmentCombinedId)
            .build();

        String messageAttachmentIdStr = messageAttachmentCombinedId.toString();

        DataExtractedFromTemplate expectedDataExtractedFromTemplate = new DataExtractedFromTemplate(
            null,
            null,
            null,
            null,
            null,
            null,
            true,
            null
        );

        CreateOrderRequest expectedRequest = new CreateOrderRequest();

        String expectedExternalId = "external-id";

        //when
        Mockito.when(gmailMessageService.getMessageAttachmentData(any(), any())).thenReturn(messageAttachmentData);
        Mockito.when(templateService.applyGoodTemplateForData(any())).thenReturn(expectedDataExtractedFromTemplate);
        Mockito.when(sferaOrderService.create(any(CreateOrderRequest.class))).thenReturn(expectedExternalId);

        try(
            MockedStatic<SferaOrderMapper> sferaOrderMapperMock = Mockito.mockStatic(SferaOrderMapper.class);
        ){

            sferaOrderMapperMock.when(() -> SferaOrderMapper.map(any(), any())).thenReturn(expectedRequest);

            invoiceService.createInvoice(messageAttachment);

            //then
            sferaOrderMapperMock.verify(() -> SferaOrderMapper.map(expectedDataExtractedFromTemplate, messageAttachmentIdStr));
        }

        Mockito.verify(gmailMessageService).getMessageAttachmentData(messageId, attachmentId);
        Mockito.verify(templateService).applyGoodTemplateForData(messageAttachmentData);
        Mockito.verify(sferaOrderService).create(expectedRequest);

        assertNotNull(messageAttachment.getExternalId());
        assertEquals(expectedExternalId, messageAttachment.getExternalId());
    }

    @Test
    void shouldNotCreateInvoiceWithExternalId() {

        //given
        MessageAttachment messageAttachment = MessageAttachment.builder()
            .externalId("external-id")
            .build();

        //when
        //then
        assertThrows(
            ConflictException.class,
            () -> invoiceService.createInvoice(messageAttachment)
        );
    }

    @Test
    void shouldNotCreateInvoiceWithNotFoundTemplate() {

        //given
        byte[] messageAttachmentData = "message-attachment-data".getBytes(StandardCharsets.UTF_8);

        String messageId = "message-id";
        Integer attachmentIndex = 1;
        String attachmentId = "attachment-id";

        MessageAttachment messageAttachment = MessageAttachment.builder()
            .id(attachmentId)
            .combinedId(new MessageAttachmentCombinedId(messageId, attachmentIndex))
            .build();

        //when
        Mockito.when(gmailMessageService.getMessageAttachmentData(any(), any())).thenReturn(messageAttachmentData);
        Mockito.when(templateService.applyGoodTemplateForData(any())).thenThrow(FileReadException.class);

        //then
        assertThrows(
            IllegalStateException.class,
            () -> invoiceService.createInvoice(messageAttachment)
        );

        Mockito.verify(gmailMessageService).getMessageAttachmentData(messageId, attachmentId);
        Mockito.verify(templateService).applyGoodTemplateForData(messageAttachmentData);
    }

    @Test
    void shouldCreateInvoices() {

        //given
        MessageAttachment expectedMessageAttachment = MessageAttachment.builder()
            .combinedId(new MessageAttachmentCombinedId("12", 1))
            .build();

        MessageAttachment expectedMessageAttachment1 = MessageAttachment.builder()
            .combinedId(new MessageAttachmentCombinedId("22", 2))
            .build();

        List<MessageAttachment> expectedMessagesAttachments = List.of(expectedMessageAttachment, expectedMessageAttachment1);

        //when
        try(
            MockedStatic<SferaOrderMapper> sferaOrderMapperMock = Mockito.mockStatic(SferaOrderMapper.class);
        ){

            invoiceService.createInvoices(expectedMessagesAttachments);

            //then
            for(MessageAttachment messageAttachment : expectedMessagesAttachments){

                String messageAttachmentCombinedIdStr = messageAttachment.getCombinedId().toString();

                sferaOrderMapperMock.verify(() -> SferaOrderMapper.map(any(), eq(messageAttachmentCombinedIdStr)));
            }
        }
    }

    @Test
    void shouldRedirectToMessage() {

        //given
        String expectedMessageId = "message-id";

        //when
        invoiceService.redirectToMessage(expectedMessageId);

        //then
        Mockito.verify(gmailMessageService).redirectToMessage(expectedMessageId);
    }

}