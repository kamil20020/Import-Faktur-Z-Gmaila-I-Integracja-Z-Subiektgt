package org.example.api.gmail.response;

import org.example.model.gmail.generated.MessageContentPart;
import org.example.model.gmail.generated.MessageContentPartBody;
import org.example.model.gmail.generated.MessagePayload;
import org.example.model.gmail.generated.MessagePayloadHeader;
import org.example.model.gmail.own.MessageSummary;
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
        List<String> expectedAttachmentsIds = List.of("345", "12");

        MessagePayloadHeader authorMessagePayloadHeader = new MessagePayloadHeader("From", expectedFrom);
        MessagePayloadHeader dateMessagePayloadHeader = new MessagePayloadHeader("Date", expectedDate);
        MessagePayloadHeader subjectMessagePayloadHeader = new MessagePayloadHeader("Subject", expectedSubject);

        List<MessagePayloadHeader> messagePayloadHeaders = List.of(authorMessagePayloadHeader, dateMessagePayloadHeader, subjectMessagePayloadHeader);

        MessageContentPartBody messageContentPartBody = new MessageContentPartBody(expectedAttachmentsIds.get(0), "data");
        MessageContentPart messageContentPart = new MessageContentPart(null, "file.pdf", "", messageContentPartBody, null);

        MessageContentPartBody messageContentPartBody1 = new MessageContentPartBody(expectedAttachmentsIds.get(1), "data1");
        MessageContentPart messageContentPart1 = new MessageContentPart(null, "file1.pdf", "", messageContentPartBody1, null);

        List<MessageContentPart> messageContentParts = List.of(messageContentPart, messageContentPart1);

        MessagePayload messagePayload = new MessagePayload(messagePayloadHeaders, messageContentParts);

        MessageDetails messageDetails = new MessageDetails(expectedId, null, messagePayload);

        //when
        MessageSummary messageSummary = messageDetails.getSummary();

        assertNotNull(messageDetails);
        assertEquals(expectedId, messageSummary.id());
        assertEquals(expectedFrom, messageSummary.from());
        assertEquals(expectedDate, messageSummary.date());
        assertEquals(expectedSubject, messageSummary.subject());

        List<String> gotAttachmentsIds = messageSummary.attachmentsIds();

        assertNotNull(gotAttachmentsIds);
        assertFalse(gotAttachmentsIds.isEmpty());
        assertEquals(expectedAttachmentsIds.size(), gotAttachmentsIds.size());
        assertTrue(gotAttachmentsIds.containsAll(expectedAttachmentsIds));
    }

}