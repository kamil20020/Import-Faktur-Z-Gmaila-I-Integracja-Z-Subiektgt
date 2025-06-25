package org.example.external.sfera.generated;

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
    "code",
    "ean",
    "qty"
})
@Generated("jsonschema2pojo")
public class ProductSetProduct {

    @JsonProperty("code")
    private String code;

    @JsonProperty("ean")
    private String ean;

    @JsonProperty("qty")
    private Integer quantity;

}
