package org.example.api.gmail.response;

import org.example.external.gmail.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageDetailsTest {

    @Test
    void shouldGetSummary() {

        //given
        String expectedId = "123";
        String expectedFrom = "Author 123";
        String expectedDate = "21-08-2024";
        String expectedSubject = "Subject 123";
        String expectedAttachmentId = "345";

        MessagePayloadHeader authorMessagePayloadHeader = new MessagePayloadHeader("From", expectedFrom);
        MessagePayloadHeader dateMessagePayloadHeader = new MessagePayloadHeader("Date", expectedDate);
        MessagePayloadHeader subjectMessagePayloadHeader = new MessagePayloadHeader("Subject", expectedSubject);

        List<MessagePayloadHeader> messagePayloadHeaders = List.of(authorMessagePayloadHeader, dateMessagePayloadHeader, subjectMessagePayloadHeader);

        MessageContentPartBody messageContentPartBody = new MessageContentPartBody(expectedAttachmentId, "data");
        MessageContentPart messageContentPart = new MessageContentPart(null, "file.pdf", messageContentPartBody);

        List<MessageContentPart> messageContentParts = List.of(messageContentPart);

        MessagePayload messagePayload = new MessagePayload(messagePayloadHeaders, messageContentParts);

        MessageDetails messageDetails = new MessageDetails(expectedId, null, messagePayload);

        //when
        MessageSummary messageSummary = messageDetails.getSummary();

        assertNotNull(messageDetails);
        assertEquals(expectedId, messageSummary.id());
        assertEquals(expectedFrom, messageSummary.from());
        assertEquals(expectedDate, messageSummary.date());
        assertEquals(expectedSubject, messageSummary.subject());
        assertEquals(expectedAttachmentId, messageSummary.attachmentId());
    }

}