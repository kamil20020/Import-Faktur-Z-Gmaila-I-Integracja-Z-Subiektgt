package org.example.external.gmail;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void shouldSetExternalId() {

        //given
        Message message = new Message();

        String expectedExternalId = "external-id";

        //when
        message.setExternalId(expectedExternalId);

        //then
        assertNotNull(message.getExternalId());
        assertEquals(expectedExternalId, message.getExternalId());
    }

    @Test
    void shouldSetExternalIdWithCommas() {

        //given
        Message message = new Message();

        String expectedExternalIdInput = "\"external-id\"";
        String expectedExternalId = "external-id";

        //when
        message.setExternalId(expectedExternalIdInput);

        //then
        assertNotNull(message.getExternalId());
        assertEquals(expectedExternalId, message.getExternalId());
    }

    @Test
    void shouldSetExternalIdWithNull() {

        //given
        Message message = new Message();

        //when
        message.setExternalId(null);

        //then
        assertNull(message.getExternalId());
    }

}