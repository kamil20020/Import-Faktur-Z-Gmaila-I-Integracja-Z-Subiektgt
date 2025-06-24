package org.example.api.gmail.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.example.external.gmail.MessageHeader;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(value = {
    "messages",
    "nextPageToken",
    "resultSizeEstimate",
})
public record MessagesPageResponse (

    @JsonProperty("messages")
    List<MessageHeader> messageHeaders,

    @JsonProperty("nextPageToken")
    String nextPageToken,

    @JsonProperty("resultSizeEstimate")
    Integer totalNumberOfElements
){}
