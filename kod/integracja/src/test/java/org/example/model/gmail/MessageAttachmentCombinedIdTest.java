package org.example.model.gmail;

import org.example.model.gmail.own.MessageAttachmentCombinedId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageAttachmentCombinedIdTest {

    @Test
    void shouldCreateFromStr() {

        //given
        String messageId = "message-id";
        Integer attachmentIndex = 1;
        String idStr = messageId + "," + attachmentIndex;

        //when
        MessageAttachmentCombinedId messageAttachmentCombinedId = MessageAttachmentCombinedId.create(idStr);

        //then
        assertNotNull(messageAttachmentCombinedId);
        assertEquals(messageId, messageAttachmentCombinedId.getMessageId());
        assertEquals(attachmentIndex, messageAttachmentCombinedId.getAttachmentIndex());
    }

    @Test
    void shouldCreateForNullStr() {

        //given
        //when
        MessageAttachmentCombinedId gotMessageAttachmentCombinedId = MessageAttachmentCombinedId.create(null);

        //then
        assertNull(gotMessageAttachmentCombinedId);
    }

    @Test
    void shouldCreateForEmptyStr() {

        //given
        //when
        MessageAttachmentCombinedId gotMessageAttachmentCombinedId = MessageAttachmentCombinedId.create("");

        //then
        assertNull(gotMessageAttachmentCombinedId);
    }

    @Test
    void shouldCreateForToShortStr() {

        //given
        //when
        MessageAttachmentCombinedId gotMessageAttachmentCombinedId = MessageAttachmentCombinedId.create("message-id");

        //then
        assertNull(gotMessageAttachmentCombinedId);
    }

    @Test
    void shouldGetToString() {

        //given
        String messageId = "message-id";
        Integer attachmentIndex = 1;
        String expectedIdStr = messageId + "," + attachmentIndex;

        MessageAttachmentCombinedId id = new MessageAttachmentCombinedId(messageId, attachmentIndex);

        //when
        String gotIdStr = id.toString();

        //then
        assertNotNull(gotIdStr);
        assertEquals(expectedIdStr, gotIdStr);
    }

}