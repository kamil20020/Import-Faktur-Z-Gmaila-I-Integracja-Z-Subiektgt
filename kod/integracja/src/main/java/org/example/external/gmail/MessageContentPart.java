package org.example.external.gmail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "partId",
    "filename",
    "body"
})
public record MessageContentPart(

    @JsonProperty("partId")
    String partId,

    @JsonProperty("filename")
    String filename,

    @JsonProperty("body")
    MessageContentPartBody body
){}
