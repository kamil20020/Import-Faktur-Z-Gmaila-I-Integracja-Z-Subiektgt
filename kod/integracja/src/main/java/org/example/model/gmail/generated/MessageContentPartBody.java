package org.example.model.gmail.generated;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "attachmentId",
    "data"
})
public class MessageContentPartBody{

    @JsonProperty("attachmentId")
    private String attachmentId;

    @ToString.Exclude
    @JsonProperty("data")
    private String data;

}
