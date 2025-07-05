package org.example.model.gmail;

import org.example.model.gmail.own.Message;
import org.example.model.gmail.own.MessageAttachment;
import org.example.model.gmail.own.MessageAttachmentCombinedId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class MessageAttachmentTest {

    @ParameterizedTest
    @CsvSource(value = {
        "external-id, external-id",
        "\"external-id\", external-id",
        ","
    })
    void shouldSetExternalIdWithCites(String inputExternalId, String expectedExternalId) {

        //given
        MessageAttachment messageAttachment = new MessageAttachment();

        //when
        messageAttachment.setExternalId(inputExternalId);

        //then
        assertEquals(expectedExternalId, messageAttachment.getExternalId());
    }

    @Test
    public void shouldCreate(){

        //given
        String messageId = "message-id";
        String attachmentId = "attachment-id";
        Integer index = 1;

        Message message = Message.builder()
            .id(messageId)
            .build();

        //when
        MessageAttachment messageAttachment = new MessageAttachment(message, attachmentId, index);

        //then
        assertEquals(attachmentId, messageAttachment.getId());

        MessageAttachmentCombinedId combinedId = messageAttachment.getCombinedId();

        assertNotNull(combinedId);
        assertEquals(messageId, combinedId.getMessageId());
        assertEquals(index, combinedId.getAttachmentIndex());

        assertEquals(index, messageAttachment.getIndex());
        assertNull(messageAttachment.getData());
        assertNull(messageAttachment.getExternalId());
    }

    @Test
    void shouldGetMessageId() {

        //given
        String messageId = "message-id";
        Integer attachmentIndex = 1;

        MessageAttachmentCombinedId messageAttachmentCombinedId = new MessageAttachmentCombinedId(messageId, attachmentIndex);

        MessageAttachment messageAttachment = MessageAttachment.builder()
            .combinedId(messageAttachmentCombinedId)
            .build();

        //when
        String gotMessageId = messageAttachment.getMessageId();

        //then
        assertNotNull(gotMessageId);
        assertEquals(messageId, gotMessageId);
    }

    @Test
    public void shouldGetMessageIdForNullId(){

        //given
        MessageAttachment messageAttachment = new MessageAttachment();

        //when
        String gotMessageId = messageAttachment.getMessageId();

        //then
        assertNull(gotMessageId);
    }

    @Test
    void shouldGetAttachmentIndex() {

        //given
        String messageId = "message-id";
        Integer attachmentIndex = 1;

        MessageAttachmentCombinedId messageAttachmentCombinedId = new MessageAttachmentCombinedId(messageId, attachmentIndex);

        MessageAttachment messageAttachment = MessageAttachment.builder()
            .combinedId(messageAttachmentCombinedId)
            .build();

        //when
        Integer gotAttachmentIndex = messageAttachment.getAttachmentIndex();

        //then
        assertNotNull(gotAttachmentIndex);
        assertEquals(gotAttachmentIndex, gotAttachmentIndex);
    }

    @Test
    public void shouldGetAttachmentIdForNullId(){

        //given
        MessageAttachment messageAttachment = new MessageAttachment();

        //when
        Integer gotAttachmentIndex = messageAttachment.getAttachmentIndex();

        //then
        assertNull(gotAttachmentIndex);
    }

}