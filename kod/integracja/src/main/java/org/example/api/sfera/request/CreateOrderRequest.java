package org.example.api.sfera.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.external.sfera.generated.Customer;
import org.example.external.sfera.generated.Product;

import javax.annotation.processing.Generated;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
    "reference",
    "comments",
    "external_id",
    "amount",
    "customer",
    "products",
})
@Generated("jsonschema2pojo")
public class CreateOrderRequest{

    @JsonProperty("reference")
    private String reference = "";

    @JsonProperty("comments")
    private String comments = "";

    @JsonProperty("external_id")
    private String externalId;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("is_invoice_required")
    private boolean isInvoiceRequired = true;

    @JsonProperty("is_receiver_invoice")
    private boolean isReceiverInvoice = true;

    @JsonProperty("customer")
    private Customer customer;

    @JsonProperty("products")
    private List<Product> products;

    public CreateOrderRequest(String reference, String externalId, BigDecimal amount, Customer customer, List<Product> products) {

        this.reference = reference;
        this.externalId = externalId;
        this.amount = amount;
        this.customer = customer;
        this.products = products;
    }

}
