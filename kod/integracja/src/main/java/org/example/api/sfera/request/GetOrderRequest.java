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
    "order_ref"
})
@Generated("jsonschema2pojo")
public class GetOrderRequest {

    @JsonProperty("order_ref")
    private String orderExternalId;

}
