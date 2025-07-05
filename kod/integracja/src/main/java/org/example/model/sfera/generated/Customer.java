package org.example.model.sfera.generated;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "firstname",
    "surname",
    "companyName",
    "street",
    "nip",
    "city",
    "post_code"
})
@Generated("jsonschema2pojo")
public class Customer{

    @JsonProperty("firstname")
    private String firstname;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("street")
    private String street;

    @JsonProperty("nip")
    private String nip;

    @JsonProperty("city")
    private String city;

    @JsonProperty("post_code")
    private String postCode;

    @JsonProperty("is_company")
    private Boolean isCompany;

}
