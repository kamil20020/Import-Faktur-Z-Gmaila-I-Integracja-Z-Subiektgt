package org.example.external.gmail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "attachmentId",
    "data"
})
public record MessageContentPartBody(

    @JsonProperty("attachmentId")
    String attachmentId,

    @JsonProperty("data")
    String data
){}
