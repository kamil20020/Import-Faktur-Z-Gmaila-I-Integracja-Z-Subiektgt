package org.example.gmail;

import org.example.mapper.gmail.GmailDataMapper;
import org.example.model.gmail.generated.MessageContentPart;
import org.example.model.gmail.generated.MessageContentPartBody;
import org.example.model.gmail.generated.MessagePayload;
import org.example.model.gmail.generated.MessagePayloadHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class MessagePayloadTest {

    @ParameterizedTest
    @CsvSource(value = {
        "abc, abc value",
        "123, 123 value",
        "def, def value",
        "1234, ''"
    })
    void shouldGetHeaderValue(String expectedHeaderName, String expectedValue) {

        //given
        MessagePayloadHeader messagePayloadHeader = new MessagePayloadHeader("abc", "abc value");
        MessagePayloadHeader messagePayloadHeader1 = new MessagePayloadHeader("123", "123 value");
        MessagePayloadHeader messagePayloadHeader2 = new MessagePayloadHeader("def", "def value");

        List<MessagePayloadHeader> messagePayloadHeaders = List.of(messagePayloadHeader, messagePayloadHeader1, messagePayloadHeader2);

        MessagePayload messagePayload = new MessagePayload(messagePayloadHeaders, null);

        //when
        String gotValue = messagePayload.getHeaderValue(expectedHeaderName);

        //then
        assertEquals(expectedValue, gotValue);
    }

    @Test
    void shouldGetAttachmentsIds() {

        //given
        List<String> expectedAttachmentsIds = List.of("attachment-345", "attachment-12");

        MessageContentPartBody messageContentPartBody = new MessageContentPartBody(expectedAttachmentsIds.get(0), "data");
        MessageContentPartBody messageContentPartBody1 = new MessageContentPartBody(expectedAttachmentsIds.get(1), "data1");
        MessageContentPartBody nullAttachmentIdMessageContentPartBody1 = new MessageContentPartBody(null, "data2");

        MessageContentPart messageContentPart1 = new MessageContentPart("345", null, "", messageContentPartBody, null);
        MessageContentPart messageContentPart2 = new MessageContentPart("345", "file.pdf", "", null, null);
        MessageContentPart messageContentPart3 = new MessageContentPart("345", "filename.jpg", "", messageContentPartBody, null);
        MessageContentPart messageContentPart4 = new MessageContentPart("345", "file.pdf", "", nullAttachmentIdMessageContentPartBody1, null);
        MessageContentPart messageContentPart5 = new MessageContentPart("345", "file.pdf", "", messageContentPartBody, null);
        MessageContentPart messageContentPart6 = new MessageContentPart("345", "file1.pdf", "", messageContentPartBody1, null);

        List<MessageContentPart> messageContentParts = List.of(messageContentPart1, messageContentPart2, messageContentPart3, messageContentPart4, messageContentPart5, messageContentPart6);

        MessagePayload messagePayload = new MessagePayload(null, messageContentParts);

        //when
        List<String> gotAttachmentsIds = messagePayload.getAttachmentsIds("pdf");

        //then
        assertNotNull(gotAttachmentsIds);
        assertFalse(gotAttachmentsIds.isEmpty());
        assertEquals(expectedAttachmentsIds.size(), gotAttachmentsIds.size());
        assertTrue(gotAttachmentsIds.containsAll(expectedAttachmentsIds));
    }

    @Test
    public void shouldGetContent(){

        //given
        String expectedEncodedContent = "content";
        String expectedDecodedContentStr = "decoded content";
        byte[] expectedDecodedContent = expectedDecodedContentStr.getBytes(StandardCharsets.UTF_8);

        MessageContentPart messageContentPart = new MessageContentPart(
            "id",
            null,
            null,
            null,
            null
        );

        MessageContentPart messageContentPart1 = new MessageContentPart(
            "id",
            null,
            "text1/plain",
            null,
            null
        );

        MessageContentPart messageContentPart2 = new MessageContentPart(
            "id",
            null,
            "text/plain",
            null,
            null
        );

        MessageContentPartBody body = new MessageContentPartBody(
            "attachment id",
            null
        );

        MessageContentPart messageContentPart3 = new MessageContentPart(
            "id",
            null,
            "text/plain",
            body,
            null
        );

        MessageContentPartBody body1 = new MessageContentPartBody(
            "attachment id",
            expectedEncodedContent
        );

        MessageContentPart messageContentPart4 = new MessageContentPart(
            "id",
            null,
            "text/plain",
            body1,
            null
        );

        MessageContentPartBody body2 = new MessageContentPartBody(
            "attachment id",
            "content 1"
        );

        MessageContentPart messageContentPart5 = new MessageContentPart(
            "id",
            null,
            "text/plain",
            body2,
            null
        );

        MessagePayload messagePayload = new MessagePayload(
            null,
            List.of(messageContentPart, messageContentPart1, messageContentPart2, messageContentPart3, messageContentPart4, messageContentPart5)
        );

        //when
        try(
            MockedStatic<GmailDataMapper> gmailDataMapper = Mockito.mockStatic(GmailDataMapper.class)
        ){

            gmailDataMapper.when(() -> GmailDataMapper.decode(any())).thenReturn(expectedDecodedContent);

            String gotContent = messagePayload.getContent();

            //then
            assertNotNull(gotContent);
            assertEquals(expectedDecodedContentStr, gotContent);

            gmailDataMapper.verify(() -> GmailDataMapper.decode(expectedEncodedContent));
        }
    }

    @Test
    public void shouldNotGetContentForNotMatchingSubParts(){

        //given
        String expectedDecodedContentStr = "decoded content";

        MessageContentPart messageContentPart = new MessageContentPart(
            "id",
            null,
            null,
            null,
            null
        );

        MessageContentPart messageContentPart1 = new MessageContentPart(
            "id",
            null,
            "text1/plain",
            null,
            null
        );

        MessagePayload messagePayload = new MessagePayload(
                null,
                List.of(messageContentPart, messageContentPart1)
        );

        //when
        String gotContent = messagePayload.getContent();

        //then
        assertNull(gotContent);
    }

    @Test
    public void shouldGetContentForSecondLevel(){

        //given
        String expectedEncodedContent = "content";
        String expectedDecodedContentStr = "decoded content";
        byte[] expectedDecodedContent = expectedDecodedContentStr.getBytes(StandardCharsets.UTF_8);

        MessageContentPartBody body = new MessageContentPartBody(
            "attachment id",
            expectedEncodedContent
        );

        MessageContentPart messageContentPart = new MessageContentPart(
            "id",
            null,
            "text/plain",
            body,
            null
        );

        //when
        try(
            MockedStatic<GmailDataMapper> gmailDataMapper = Mockito.mockStatic(GmailDataMapper.class)
        ){

            gmailDataMapper.when(() -> GmailDataMapper.decode(any())).thenReturn(expectedDecodedContent);

            String gotContent = MessagePayload.getContent(messageContentPart);

            //then
            assertNotNull(gotContent);
            assertEquals(expectedDecodedContentStr, gotContent);

            gmailDataMapper.verify(() -> GmailDataMapper.decode(expectedEncodedContent));
        }
    }

    @Test
    public void shouldGetContentForSecondLevelInSubParts(){

        //given
        String expectedEncodedContent = "sub part content";
        String expectedDecodedContentStr = "decoded content";
        byte[] expectedDecodedContent = expectedDecodedContentStr.getBytes(StandardCharsets.UTF_8);

        MessageContentPart subPart = new MessageContentPart(
            null,
            null,
            null,
            null,
            null
        );

        MessageContentPart subPart1 = new MessageContentPart(
            null,
            null,
            "text1/plain",
            null,
            null
        );

        MessageContentPart subPart2 = new MessageContentPart(
            null,
            null,
            "text/plain",
            null,
            null
        );

        MessageContentPartBody subPartBody = new MessageContentPartBody(
            null,
            null
        );

        MessageContentPart subPart3 = new MessageContentPart(
            null,
            null,
            "text/plain",
            subPartBody,
            null
        );

        MessageContentPartBody subPartBody1 = new MessageContentPartBody(
            null,
            expectedEncodedContent
        );

        MessageContentPart subPart4 = new MessageContentPart(
            null,
            null,
            "text/plain",
            subPartBody1,
            null
        );

        MessageContentPartBody subPartBody2 = new MessageContentPartBody(
            null,
            "sub part content1"
        );

        MessageContentPart subPart5 = new MessageContentPart(
            null,
            null,
            "text/plain",
            subPartBody2,
            null
        );

        List<MessageContentPart> subParts = List.of(subPart, subPart1, subPart2, subPart3, subPart4, subPart5);

        MessageContentPart messageContentPart = new MessageContentPart(
            "id",
            null,
            "text/plain",
            null,
            subParts
        );

        //when
        try(
            MockedStatic<GmailDataMapper> gmailDataMapper = Mockito.mockStatic(GmailDataMapper.class)
        ){

            gmailDataMapper.when(() -> GmailDataMapper.decode(expectedEncodedContent)).thenReturn(expectedDecodedContent);

            String gotContent = MessagePayload.getContent(messageContentPart);

            //then
            assertNotNull(gotContent);
            assertEquals(expectedDecodedContentStr, gotContent);

            gmailDataMapper.verify(() -> GmailDataMapper.decode(expectedEncodedContent));
        }
    }

    @Test
    public void shouldNotGetContentForSecondLevelInSubPartsForNotMatching(){

        //given
        MessageContentPart subPart = new MessageContentPart(
            null,
            null,
            null,
            null,
            null
        );

        MessageContentPart subPart1 = new MessageContentPart(
            null,
            null,
            "text/plain",
            null,
            null
        );

        MessageContentPartBody subPartBody = new MessageContentPartBody(
            null,
            null
        );

        MessageContentPart subPart2 = new MessageContentPart(
            null,
            null,
            "text/plain",
            subPartBody,
            null
        );

        List<MessageContentPart> subParts = List.of(subPart, subPart1, subPart2);

        MessageContentPart messageContentPart = new MessageContentPart(
            "id",
            null,
            "text/plain",
            null,
            subParts
        );

        //when
        String gotContent = MessagePayload.getContent(messageContentPart);

        //then
        assertNull(gotContent);
    }

    @Test
    public void shouldNotGetContentForSecondLevelWhenInputIsNull(){

        //given
        //when
        String gotContent = MessagePayload.getContent(null);

        //then
        assertNull(gotContent);
    }

    @Test
    public void shouldNotGetContentForSecondLevelWhenThereIsNoSubParts(){

        //given
        //when
        MessageContentPart messageContentPart = new MessageContentPart(
            "id",
            null,
            "text/plain",
            null,
            null
        );

        String gotContent = MessagePayload.getContent(messageContentPart);

        //then
        assertNull(gotContent);
    }

    @Test
    public void shouldNotGetContentForSecondLevelWhenThereIsEmptySubParts(){

        //given
        //when
        MessageContentPart messageContentPart = new MessageContentPart(
            "id",
            null,
            "text/plain",
            null,
            new ArrayList<>()
        );

        String gotContent = MessagePayload.getContent(messageContentPart);

        //then
        assertNull(gotContent);
    }

    @Test
    public void shouldGetMessageContentPartContent(){

        //given
        String expectedEncodedContent = "content";
        String expectedDecodedContentStr = "decoded content";
        byte[] expectedDecodedContent = expectedDecodedContentStr.getBytes(StandardCharsets.UTF_8);

        MessageContentPartBody body = new MessageContentPartBody(
            "attachment id",
            expectedEncodedContent
        );

        MessageContentPart messageContentPart = new MessageContentPart(
            "id",
            null,
            "text/plain",
            body,
            null
        );

        //when
        try(
            MockedStatic<GmailDataMapper> gmailDataMapper = Mockito.mockStatic(GmailDataMapper.class)
        ){

            gmailDataMapper.when(() -> GmailDataMapper.decode(any())).thenReturn(expectedDecodedContent);

            String gotContent = MessagePayload.getMessageContentPartContent(messageContentPart);

            //then
            assertNotNull(gotContent);
            assertEquals(expectedDecodedContentStr, gotContent);

            gmailDataMapper.verify(() -> GmailDataMapper.decode(expectedEncodedContent));
        }
    }

    @Test
    public void shouldNotGetMessageContentPartContentForEmptyInput(){

        //given
        //when
        String gotContent = MessagePayload.getMessageContentPartContent(null);

        //then
       assertNull(gotContent);
    }

    @Test
    public void shouldNotGetMessageContentPartContentForInvalidMimeType(){

        //given
        String expectedEncodedContent = "content";

        MessageContentPartBody body = new MessageContentPartBody(
            "attachment id",
            expectedEncodedContent
        );

        MessageContentPart messageContentPart = new MessageContentPart(
            "id",
            null,
            "text1/plain",
            body,
            null
        );

        //when
        String gotContent = MessagePayload.getMessageContentPartContent(messageContentPart);

        //then
        assertNull(gotContent);
    }

    @Test
    public void shouldNotGetMessageContentPartContentForNoBody(){

        //given
        MessageContentPart messageContentPart = new MessageContentPart(
                "id",
                null,
                "text/plain",
                null,
                null
        );

        //when
        String gotContent = MessagePayload.getMessageContentPartContent(messageContentPart);

        //then
        assertNull(gotContent);
    }

    @Test
    public void shouldNotGetMessageContentPartContentForNoBodyContent(){

        //given
        MessageContentPartBody body = new MessageContentPartBody(
                "attachment id",
                null
        );

        MessageContentPart messageContentPart = new MessageContentPart(
                "id",
                null,
                "text/plain",
                body,
                null
        );

        //when
        String gotContent = MessagePayload.getMessageContentPartContent(messageContentPart);

        //then
        assertNull(gotContent);
    }

    @Test
    public void shouldNotGetMessageContentPartContentForEmptyContent(){

        //given
        MessageContentPartBody body = new MessageContentPartBody(
            "attachment id",
            ""
        );

        MessageContentPart messageContentPart = new MessageContentPart(
            "id",
            null,
            "text/plain",
            body,
            null
        );

        //when
        String gotContent = MessagePayload.getMessageContentPartContent(messageContentPart);

        //then
        assertNull(gotContent);
    }

}