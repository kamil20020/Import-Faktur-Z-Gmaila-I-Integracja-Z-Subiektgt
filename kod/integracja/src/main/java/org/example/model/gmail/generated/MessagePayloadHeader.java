package org.example.model.gmail.generated;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "name",
    "value"
})
public record MessagePayloadHeader(

    @JsonProperty("name")
    String name,

    @JsonProperty("value")
    String value
){}
