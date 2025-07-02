package org.example.service.unit;

import org.example.api.gmail.response.MessagesPageResponse;
import org.example.api.sfera.request.CreateOrderRequest;
import org.example.exception.ConflictException;
import org.example.exception.FileReadException;
import org.example.external.gmail.Message;
import org.example.external.gmail.MessageHeader;
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
import static org.mockito.Mockito.times;

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
        Mockito.when(gmailMessageService.getPage(anyInt(), any())).thenReturn(expectedMessagesPageResponse);

        MessagesPageResponse gotPage = invoiceService.getMessagesPage(expectedPageSize, expectedPageToken);

        //then
        assertNotNull(gotPage);
        assertEquals(expectedMessagesPageResponse, gotPage);

        Mockito.verify(gmailMessageService).getPage(expectedPageSize, expectedPageToken);
    }

    @Test
    void shouldLoadInvoiceDetailsAfterGeneralLoad() {

        //given
        String messageId = "message-id";

        Message message = Message.builder()
            .id(messageId)
            .build();

        String expectedExternalId = "external-id";

        //when
        Mockito.when(sferaOrderService.getSubiektIdByExternalId(any())).thenReturn(expectedExternalId);

        invoiceService.loadInvoiceDetailsAfterGeneralLoad(message);

        //then
        assertEquals(expectedExternalId, message.getExternalId());

        Mockito.verify(sferaOrderService).getSubiektIdByExternalId(messageId);
    }

    @Test
    void shouldLoadInvoicesDetails() {

        //given
        MessageHeader messageHeader = new MessageHeader(null, null);

        List<MessageHeader> messageHeaders = List.of(messageHeader);

        Message expectedMessage1 = Message.builder()
            .id("message-id-1")
            .build();

        Message expectedMessage2 = Message.builder()
            .id("message-id-2")
            .build();

        List<Message> expectedMessages = List.of(expectedMessage1, expectedMessage2);

        //when
        Mockito.when(gmailMessageService.extractMessages(any())).thenReturn(expectedMessages);

        List<Message> gotMessages = invoiceService.loadInvoicesDetails(messageHeaders);

        //then
        assertNotNull(gotMessages);
        assertFalse(gotMessages.isEmpty());
        assertEquals(expectedMessages.size(), gotMessages.size());
        assertTrue(gotMessages.containsAll(expectedMessages));

        Mockito.verify(gmailMessageService).extractMessages(messageHeaders);

        for(Message expectedMessage : expectedMessages){

            String expectedMessageId = expectedMessage.getId();

            Mockito.verify(sferaOrderService).getSubiektIdByExternalId(expectedMessageId);
        }
    }

    @Test
    void shouldCreateInvoice() {

        //given
        String messageId = "message-id";
        byte[] messageAttachmentData = "message-attachment-data".getBytes(StandardCharsets.UTF_8);

        Message message = Message.builder()
            .id(messageId)
            .attachmentData(messageAttachmentData)
            .build();

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
        Mockito.when(templateService.applyGoodTemplateForData(any())).thenReturn(expectedDataExtractedFromTemplate);
        Mockito.when(sferaOrderService.create(any(CreateOrderRequest.class))).thenReturn(expectedExternalId);

        try(
            MockedStatic<SferaOrderMapper> sferaOrderMapperMock = Mockito.mockStatic(SferaOrderMapper.class);
        ){

            sferaOrderMapperMock.when(() -> SferaOrderMapper.map(any(), any())).thenReturn(expectedRequest);

            invoiceService.createInvoice(message);

            //then
            sferaOrderMapperMock.verify(() -> SferaOrderMapper.map(expectedDataExtractedFromTemplate, messageId));
        }

        Mockito.verify(templateService).applyGoodTemplateForData(messageAttachmentData);
        Mockito.verify(sferaOrderService).create(expectedRequest);

        assertNotNull(message.getExternalId());
        assertEquals(expectedExternalId, message.getExternalId());
    }

    @Test
    void shouldNotCreateInvoiceWithExternalId() {

        //given
        Message message = Message.builder()
            .externalId("external-id")
            .build();

        //when
        //then
        assertThrows(
            ConflictException.class,
                () -> invoiceService.createInvoice(message)
        );
    }

    @Test
    void shouldNotCreateInvoiceWithNotFoundTemplate() {

        //given
        byte[] messageAttachmentData = "message-attachment-data".getBytes(StandardCharsets.UTF_8);

        Message message = Message.builder()
            .attachmentData(messageAttachmentData)
            .build();

        //when
        Mockito.when(templateService.applyGoodTemplateForData(any())).thenThrow(FileReadException.class);

        //then
        assertThrows(
            IllegalStateException.class,
            () -> invoiceService.createInvoice(message)
        );

        Mockito.verify(templateService).applyGoodTemplateForData(messageAttachmentData);
    }

    @Test
    void shouldCreateInvoices() {

        //given
        Message message1 = Message.builder()
            .id("message-id-1")
            .build();

        Message message2 = Message.builder()
            .id("message-id-2")
            .build();

        List<Message> messages = List.of(message1, message2);

        //when
        try(
            MockedStatic<SferaOrderMapper> sferaOrderMapperMock = Mockito.mockStatic(SferaOrderMapper.class);
        ){

            invoiceService.createInvoices(messages);

            //then
            for(Message message : messages){

                String messageId = message.getId();

                sferaOrderMapperMock.verify(() -> SferaOrderMapper.map(any(), eq(messageId)));
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