package org.example.template.data;

import lombok.*;
import org.example.template.TemplateRowField;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TemplateInvoiceItem {

    private String code;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal tax;

    public static boolean isRowValid(String[] invoiceLineWords, int minLength){

        if(invoiceLineWords.length < minLength){

            return false;
        }

        try{

            Integer.valueOf(invoiceLineWords[0]);
        }
        catch(NumberFormatException e){

            return false;
        }

        return true;
    }

    public static TemplateInvoiceItem extract(Map<String, String> gotValues){

        String name = gotValues.get("name")
            .stripIndent();

        String code = gotValues.get("code")
            .stripIndent();

        String rawPrice = gotValues.get("price");
        BigDecimal price = TemplateConverter.convertToBigDecimal(rawPrice);

        String rawQuantity = gotValues.get("quantity");
        Integer quantity = TemplateConverter.convertToInteger(rawQuantity);

        String rawTax = gotValues.get("tax");
        BigDecimal tax = TemplateConverter.convertToBigDecimal(rawTax);

        return TemplateInvoiceItem.builder()
            .name(name)
            .code(code)
            .price(price)
            .quantity(quantity)
            .tax(tax)
            .build();
    }

    public BigDecimal getTotalPriceWithTax(boolean isInvoiceTaxOriented){

        BigDecimal unitPriceWithTax = getUnitPriceWithTax(isInvoiceTaxOriented);

        BigDecimal quantityConverted = BigDecimal.valueOf(quantity);

        return quantityConverted.multiply(unitPriceWithTax);
    }

    public BigDecimal getUnitPriceWithTax(boolean isInvoiceTaxOriented){

        if(isInvoiceTaxOriented){

            return price;
        }

        BigDecimal hundred = BigDecimal.valueOf(100);

        BigDecimal taxFraction = tax.divide(hundred);

        BigDecimal taxValue = price.multiply(taxFraction);

        return price.add(taxValue);
    }

}
