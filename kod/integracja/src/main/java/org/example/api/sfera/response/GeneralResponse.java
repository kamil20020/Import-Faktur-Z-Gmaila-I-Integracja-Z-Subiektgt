package org.example.api.sfera.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Generated;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "state",
    "data",
})
@Generated("jsonschema2pojo")
public class GeneralResponse {

    @JsonProperty("state")
    private String status;

    @JsonProperty("data")
    private String data;

    @JsonProperty("data")
    public void setData(JsonNode dataNode) {

        try {
            ObjectMapper mapper = new ObjectMapper();

            this.data = mapper.writeValueAsString(dataNode);
        }
        catch (Exception e) {
            this.data = null;
        }
    }

}
