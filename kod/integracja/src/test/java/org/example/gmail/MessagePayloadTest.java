package org.example.gmail;

import org.example.model.gmail.generated.MessageContentPart;
import org.example.model.gmail.generated.MessageContentPartBody;
import org.example.model.gmail.generated.MessagePayload;
import org.example.model.gmail.generated.MessagePayloadHeader;
import org.junit.jupiter.api.Test;
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

    @Test
    void shouldGetAttachmentsIds() {

        //given
        List<String> expectedAttachmentsIds = List.of("attachment-345", "attachment-12");

        MessageContentPartBody messageContentPartBody = new MessageContentPartBody(expectedAttachmentsIds.get(0), "data");
        MessageContentPartBody messageContentPartBody1 = new MessageContentPartBody(expectedAttachmentsIds.get(1), "data1");
        MessageContentPartBody nullAttachmentIdMessageContentPartBody1 = new MessageContentPartBody(null, "data2");

        MessageContentPart messageContentPart1 = new MessageContentPart("345", null, messageContentPartBody);
        MessageContentPart messageContentPart2 = new MessageContentPart("345", "file.pdf", null);
        MessageContentPart messageContentPart3 = new MessageContentPart("345", "filename.jpg", messageContentPartBody);
        MessageContentPart messageContentPart4 = new MessageContentPart("345", "file.pdf", nullAttachmentIdMessageContentPartBody1);
        MessageContentPart messageContentPart5 = new MessageContentPart("345", "file.pdf", messageContentPartBody);
        MessageContentPart messageContentPart6 = new MessageContentPart("345", "file1.pdf", messageContentPartBody1);

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

}