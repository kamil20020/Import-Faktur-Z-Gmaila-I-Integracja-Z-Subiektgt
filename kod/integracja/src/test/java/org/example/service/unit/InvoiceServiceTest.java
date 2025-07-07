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
import org.example.template.Template;
import org.example.template.data.DataExtractedFromTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

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

        Message message = Message.builder()
            .subject("subject")
            .build();

        MessageAttachment messageAttachment = MessageAttachment.builder()
            .id(attachmentId)
            .combinedId(messageAttachmentCombinedId)
            .message(message)
            .build();

        String messageAttachmentIdStr = messageAttachmentCombinedId.toString();

        Template expectedTemplate = new Template(true, null, null, null, null, null, null, null);

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
        Mockito.when(templateService.findTemplate(message.getSubject())).thenReturn(Optional.of(expectedTemplate));
        Mockito.when(templateService.applyTemplate(any(), any())).thenReturn(expectedDataExtractedFromTemplate);
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
        Mockito.verify(templateService).findTemplate(message.getSubject());
        Mockito.verify(templateService).applyTemplate(expectedTemplate, messageAttachmentData);
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

        Message message = new Message();

        MessageAttachment messageAttachment = MessageAttachment.builder()
            .id(attachmentId)
            .combinedId(new MessageAttachmentCombinedId(messageId, attachmentIndex))
            .message(message)
            .build();

        //when
        Mockito.when(gmailMessageService.getMessageAttachmentData(any(), any())).thenReturn(messageAttachmentData);

        //then
        assertThrows(
            IllegalStateException.class,
            () -> invoiceService.createInvoice(messageAttachment)
        );

        Mockito.verify(gmailMessageService).getMessageAttachmentData(messageId, attachmentId);
    }

    @Test
    void shouldCreateInvoices() {

        //given
        Message message = Message.builder()
            .subject("subject")
            .build();

        MessageAttachment expectedMessageAttachment = MessageAttachment.builder()
            .combinedId(new MessageAttachmentCombinedId("12", 1))
            .message(message)
            .build();

        MessageAttachment expectedMessageAttachment1 = MessageAttachment.builder()
            .combinedId(new MessageAttachmentCombinedId("22", 2))
            .message(message)
            .build();

        Template expectedTemplate = new Template(true, null, null, null, null, null, null, null);

        List<MessageAttachment> expectedMessagesAttachments = List.of(expectedMessageAttachment, expectedMessageAttachment1);

        //when
        Mockito.when(templateService.findTemplate(message.getSubject())).thenReturn(Optional.of(expectedTemplate));

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

    @ParameterizedTest
    @CsvSource(value = {
        "subject123",
        "author123",
        "content123",
    })
    public void shouldFindGoodTemplateForTexts(String gotContent){

        //given
        Template expectedTemplate = new Template(true, null, null, null, null, null, null, null);

        byte[] expectedAttachmentData = "content".getBytes(StandardCharsets.UTF_8);

        Message message = Message.builder()
            .subject("subject123")
            .from("author123")
            .content("content123")
            .build();

        //when
        Mockito.when(templateService.findTemplate(any())).thenReturn(Optional.empty());
        Mockito.when(templateService.findTemplate(gotContent)).thenReturn(Optional.of(expectedTemplate));

        Optional<Template> gotTemplateOpt = invoiceService.findGoodTemplate(expectedAttachmentData, message);

        //then
        assertNotNull(gotTemplateOpt);
        assertTrue(gotTemplateOpt.isPresent());
        assertEquals(expectedTemplate, gotTemplateOpt.get());

        Mockito.verify(templateService).findTemplate(gotContent);
    }

    @Test
    public void shouldFindGoodTemplateForFileContent(){

        //given
        Template expectedTemplate = new Template(true, null, null, null, null, null, null, null);

        byte[] expectedAttachmentData = "content".getBytes(StandardCharsets.UTF_8);

        Message message = Message.builder()
            .subject("subject123")
            .from("author123")
            .content("content123")
            .build();

        //when
        Mockito.when(templateService.findTemplate(any())).thenReturn(Optional.empty());
        Mockito.when(templateService.findTemplateForData(any())).thenReturn(Optional.of(expectedTemplate));

        Optional<Template> gotTemplateOpt = invoiceService.findGoodTemplate(expectedAttachmentData, message);

        //then
        assertNotNull(gotTemplateOpt);
        assertTrue(gotTemplateOpt.isPresent());
        assertEquals(expectedTemplate, gotTemplateOpt.get());

        Mockito.verify(templateService).findTemplate(message.getSubject());
        Mockito.verify(templateService).findTemplate(message.getFrom());
        Mockito.verify(templateService).findTemplate(message.getContent());
        Mockito.verify(templateService).findTemplateForData(expectedAttachmentData);
    }

    @Test
    public void shouldFindTemplateWhenItDoesNotExist(){

        //given
        Message message = Message.builder()
            .subject("subject123")
            .from("author123")
            .content("content123")
            .build();

        byte[] expectedAttachmentData = "content".getBytes(StandardCharsets.UTF_8);

        //when
        Mockito.when(templateService.findTemplate(any())).thenReturn(Optional.empty());
        Mockito.when(templateService.findTemplateForData(any())).thenReturn(Optional.empty());

        Optional<Template> gotTemplateOpt = invoiceService.findGoodTemplate(expectedAttachmentData, message);

        //then
        assertNotNull(gotTemplateOpt);
        assertTrue(gotTemplateOpt.isEmpty());

        Mockito.verify(templateService).findTemplate(message.getSubject());
        Mockito.verify(templateService).findTemplate(message.getFrom());
        Mockito.verify(templateService).findTemplate(message.getContent());
        Mockito.verify(templateService).findTemplateForData(expectedAttachmentData);
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