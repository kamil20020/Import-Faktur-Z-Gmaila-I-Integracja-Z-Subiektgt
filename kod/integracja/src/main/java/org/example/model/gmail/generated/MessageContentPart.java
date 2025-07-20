package org.example.model.gmail.generated;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "partId",
    "filename",
    "mimeType",
    "body",
    "parts"
})
public record MessageContentPart(

    @JsonProperty("partId")
    String partId,

    @JsonProperty("filename")
    String filename,

    @JsonProperty("mimeType")
    String mimeType,

    @JsonProperty("body")
    MessageContentPartBody body,

    @JsonProperty("parts")
    List<MessageContentPart> parts

){}
