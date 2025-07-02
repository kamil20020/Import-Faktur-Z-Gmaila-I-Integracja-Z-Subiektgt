package org.example.service.unit;

import org.example.TestHttpResponse;
import org.example.api.Api;
import org.example.api.gmail.GmailMessageApi;
import org.example.api.gmail.response.MessageDetails;
import org.example.api.gmail.response.MessagesPageResponse;
import org.example.external.gmail.*;
import org.example.mapper.gmail.GmailDateMapper;
import org.example.service.GmailMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class GmailMessageServiceTest {

    @Mock
    private GmailMessageApi gmailMessageApi;

    @InjectMocks
    private GmailMessageService gmailMessageService;

    @Test
    void shouldGetPage() {

        //given
        int expectedPageSize = 10;
        String expectedToken = "token";

        TestHttpResponse expectedResponse = TestHttpResponse.builder()
            .statusCode(200)
            .build();

        MessagesPageResponse expectedPageResponse = new MessagesPageResponse(
            null,
            null,
            null
        );

        //when
        Mockito.when(gmailMessageApi.getPage(any(), any())).thenReturn(expectedResponse);

        try(
            MockedStatic<Api> apiMock = Mockito.mockStatic(Api.class);
        ){

            apiMock.when(() -> Api.extractBody(any(), any())).thenReturn(expectedPageResponse);

            MessagesPageResponse gotPage = gmailMessageService.getPage(expectedPageSize, expectedToken);

            //then
            assertNotNull(gotPage);
            assertEquals(expectedPageResponse, gotPage);

            apiMock.verify(() -> Api.extractBody(expectedResponse, MessagesPageResponse.class));
        }

        Mockito.verify(gmailMessageApi).getPage(expectedPageSize, expectedToken);
    }

    @Test
    void shouldExtractMessages() {


    }

    @Test
    public void shouldGetCompleteMessage(){

        //given
        byte[] rawData = "content".getBytes(StandardCharsets.UTF_8);
        String base64Data = "Y29udGVudA";

        MessageSummary messageSummary = new MessageSummary(
            "id",
            "from",
            "date",
            "subject",
            "attachment-id"
        );

        TestHttpResponse expectedResponse = TestHttpResponse.builder()
            .statusCode(200)
            .build();

        MessageContentPartBody expectedAttachmentResponse = new MessageContentPartBody(
            messageSummary.attachmentId(),
            base64Data
        );

        OffsetDateTime expectedConvertedDate = OffsetDateTime.now();

        //when
        Mockito.when(gmailMessageApi.getMessageAttachment(any(), any())).thenReturn(expectedResponse);

        try(
            MockedStatic<Api> apiMock = Mockito.mockStatic(Api.class);
            MockedStatic<GmailDateMapper> gmailDateMapperMock = Mockito.mockStatic(GmailDateMapper.class);
        ){

            apiMock.when(() -> Api.extractBody(any(), eq(MessageContentPartBody.class))).thenReturn(expectedAttachmentResponse);

            gmailDateMapperMock.when(() -> GmailDateMapper.fromStr(any())).thenReturn(expectedConvertedDate);

            Message gotMessage = gmailMessageService.getCompleteMessage(messageSummary);

            //then
            assertNotNull(gotMessage);
            assertEquals(messageSummary.id(), gotMessage.getId());
            assertEquals(expectedConvertedDate, gotMessage.getDate());
            assertEquals(messageSummary.subject(), gotMessage.getSubject());
            assertEquals(messageSummary.from(), gotMessage.getFrom());
            assertNotNull(gotMessage.getAttachmentData());
            assertArrayEquals(rawData, gotMessage.getAttachmentData());

            apiMock.verify(() -> Api.extractBody(expectedResponse, MessageContentPartBody.class));

            gmailDateMapperMock.verify(() -> GmailDateMapper.fromStr(messageSummary.date()));
        }

        Mockito.verify(gmailMessageApi).getMessageAttachment(messageSummary.id(), messageSummary.attachmentId());
    }

    @Test
    void shouldGetMessageById() {

        //given
        String expectedMessageId = "message-id";

        TestHttpResponse expectedResponse = TestHttpResponse.builder()
            .statusCode(200)
            .build();

        MessagePayloadHeader messagePayloadHeader = new MessagePayloadHeader("From", null);
        MessagePayloadHeader messagePayloadHeader1 = new MessagePayloadHeader("Date", null);
        MessagePayloadHeader messagePayloadHeader2 = new MessagePayloadHeader("Subject", null);

        List<MessagePayloadHeader> messagePayloadHeaders = List.of(messagePayloadHeader, messagePayloadHeader1, messagePayloadHeader2);

        MessageContentPartBody messageContentPartBody = new MessageContentPartBody(null, "data");
        MessageContentPart messageContentPart = new MessageContentPart(null, "file.pdf", messageContentPartBody);

        List<MessageContentPart> messageContentParts = List.of(messageContentPart);

        MessagePayload messagePayload = new MessagePayload(messagePayloadHeaders, messageContentParts);

        MessageDetails expectedMessageDetails = new MessageDetails(
            null,
            null,
            messagePayload
        );

        //when
        Mockito.when(gmailMessageApi.getMessageById(any())).thenReturn(expectedResponse);
        Mockito.when(gmailMessageApi.getMessageAttachment(any(), any())).thenReturn(expectedResponse);

        try(
            MockedStatic<Api> apiMock = Mockito.mockStatic(Api.class);
        ){

            apiMock.when(() -> Api.extractBody(any(), eq(MessageDetails.class))).thenReturn(expectedMessageDetails);
            apiMock.when(() -> Api.extractBody(any(), eq(MessageContentPartBody.class))).thenReturn(messageContentPartBody);

            Message gotMessage = gmailMessageService.getMessageById(expectedMessageId);

            assertNotNull(gotMessage);

            //then
            apiMock.verify(() -> Api.extractBody(expectedResponse, MessageDetails.class));
        }

        Mockito.verify(gmailMessageApi).getMessageById(expectedMessageId);
    }

    @Test
    void shouldGetMessageAttachment() {

        //given
        String expectedMessageId = "message-id";
        String expectedAttachmentId = "attachment-id";

        byte[] rawData = "content".getBytes(StandardCharsets.UTF_8);
        String base64Data = "Y29udGVudA";

        TestHttpResponse expectedResponse = TestHttpResponse.builder()
            .statusCode(200)
            .build();

        MessageContentPartBody expectedAttachmentResponse = new MessageContentPartBody(
            expectedAttachmentId,
            base64Data
        );

        //when
        Mockito.when(gmailMessageApi.getMessageAttachment(any(), any())).thenReturn(expectedResponse);

        try(
            MockedStatic<Api> apiMock = Mockito.mockStatic(Api.class);
        ){

            apiMock.when(() -> Api.extractBody(any(), any())).thenReturn(expectedAttachmentResponse);

            byte[] gotData = gmailMessageService.getMessageAttachment(expectedMessageId, expectedAttachmentId);

            //then
            assertNotNull(gotData);
            assertArrayEquals(rawData, gotData);

            apiMock.verify(() -> Api.extractBody(expectedResponse, MessageContentPartBody.class));
        }

        Mockito.verify(gmailMessageApi).getMessageAttachment(expectedMessageId, expectedAttachmentId);
    }

    @Test
    void shouldRedirectToMessage() {

        //given
        String expectedMessageId = "message-id";

        //when
        gmailMessageService.redirectToMessage(expectedMessageId);

        //then
        Mockito.verify(gmailMessageApi).redirectToMessage(expectedMessageId);
    }

}