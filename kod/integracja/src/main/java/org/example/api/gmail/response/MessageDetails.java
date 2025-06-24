package org.example.api.gmail.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.example.external.gmail.MessagePayload;
import org.example.external.gmail.MessageSummary;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "id",
    "threadId",
    "payload",
})
public record MessageDetails(

    @JsonProperty("id")
    String id,

    @JsonProperty("threadId")
    String threadId,

    @JsonProperty("payload")
    MessagePayload messagePayload
){

    public MessageSummary getSummary(){

        return new MessageSummary(
            id,
            messagePayload.getHeaderValue("From"),
            messagePayload.getHeaderValue("Date"),
            messagePayload.getHeaderValue("Subject"),
            messagePayload.getAttachmentId(".pdf")
        );
    }

}
