package org.example.model.sfera.generated;

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
    "name",
    "total_price",
    "unit_price_without_tax",
    "qty",
    "vat",
})
@Generated("jsonschema2pojo")
public class Product {

    @JsonProperty("code")
    private String code;

    @JsonProperty("ean")
    private String ean;

    @JsonProperty("name")
    private String name;

    @JsonProperty("unit_price_without_tax")
    private BigDecimal unitPriceWithoutTax;

    @JsonProperty("qty")
    private Integer quantity;

    @JsonProperty("vat")
    private BigDecimal tax;

}
