package org.example.api.sfera.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Generated;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
    "external_id"
})
@Generated("jsonschema2pojo")
public class GetDocumentByExternalIdRequest {

    @JsonProperty("external_id")
    private String externalId;

}
