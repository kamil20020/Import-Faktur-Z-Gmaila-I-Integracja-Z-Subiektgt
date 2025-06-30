package org.example.external.gmail;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @ParameterizedTest
    @CsvSource(value = {
        ".pdf, attachment-345",
        ".png,"
    })
    void shouldGetAttachmentId(String expectedSearchExtension, String expectedAttachmentId) {

        //given
        MessageContentPartBody messageContentPartBody = new MessageContentPartBody("attachment-345", "data");
        MessageContentPartBody nullAttachmentIdMessageContentPartBody1 = new MessageContentPartBody(null, "data1");

        MessageContentPart messageContentPart1 = new MessageContentPart("345", null, messageContentPartBody);
        MessageContentPart messageContentPart2 = new MessageContentPart("345", "file.pdf", null);
        MessageContentPart messageContentPart3 = new MessageContentPart("345", "filename.jpg", messageContentPartBody);
        MessageContentPart messageContentPart4 = new MessageContentPart("345", "file.pdf", nullAttachmentIdMessageContentPartBody1);
        MessageContentPart messageContentPart5 = new MessageContentPart("345", "file.pdf", messageContentPartBody);

        List<MessageContentPart> messageContentParts = List.of(messageContentPart1, messageContentPart2, messageContentPart3, messageContentPart4, messageContentPart5);

        MessagePayload messagePayload = new MessagePayload(null, messageContentParts);

        //when
        String gotAttachmentId = messagePayload.getAttachmentId(expectedSearchExtension);

        //then
        assertEquals(expectedAttachmentId, gotAttachmentId);
    }

}