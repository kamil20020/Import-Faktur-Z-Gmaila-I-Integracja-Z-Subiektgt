package org.example.external.sfera.generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Generated;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
    "code",
    "ean",
    "qty",
    "price",
    "name",
})
@Generated("jsonschema2pojo")
public class Product {

    @JsonProperty("code")
    private String code;

    @JsonProperty("ean")
    private String ean;

    @JsonProperty("name")
    private String name;

    @JsonProperty("price")
    private BigDecimal priceWithTax;

    @JsonProperty("qty")
    private Integer quantity;

    @JsonProperty("tax")
    private BigDecimal tax;

}
