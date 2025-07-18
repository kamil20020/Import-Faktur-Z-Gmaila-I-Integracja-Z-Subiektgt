package org.example.api.sfera.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.sfera.generated.Customer;
import org.example.model.sfera.generated.Product;

import javax.annotation.processing.Generated;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    "pay_type",
    "pay_date"
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

    @JsonProperty("creation_date")
    private LocalDate creationDate;

    @JsonProperty("delivery_date")
    private LocalDate deliveryDate;

    @JsonProperty("creation_place")
    private String creationPlace;

    @JsonProperty("pay_date")
    private LocalDate payDate;

    @JsonProperty("create_product_if_not_exists")
    private Boolean shouldCreateProductIfNotExists = true;

    @JsonProperty("pay_type")
    private String payType = "credit";

    public CreateOrderRequest(String reference, String externalId, BigDecimal amount, Customer customer, List<Product> products, LocalDate creationDate, LocalDate deliveryDate, String creationPlace, LocalDate payDate) {

        this.reference = reference;
        this.externalId = externalId;
        this.amount = amount;
        this.customer = customer;
        this.products = products;
        this.creationDate = creationDate;
        this.deliveryDate = deliveryDate;
        this.creationPlace = creationPlace;
        this.payDate = payDate;
    }

}
