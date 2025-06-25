package org.example.mapper.sfera;

import org.example.external.sfera.generated.Product;
import org.example.template.data.TemplateInvoiceItem;

import java.math.BigDecimal;

public interface SferaProductMapper {

    public static Product map(TemplateInvoiceItem templateInvoiceItem, boolean isInvoiceTaxOriented){

        BigDecimal price = templateInvoiceItem.getPrice();
        Integer quantity = templateInvoiceItem.getQuantity();

        BigDecimal quantityConverted = BigDecimal.valueOf(quantity);

        BigDecimal unitPriceWithTax = price;

        if(!isInvoiceTaxOriented){

            BigDecimal taxPercentage = templateInvoiceItem.getTax();

            unitPriceWithTax = getUnitPriceWithTax(price, taxPercentage);
        }

        BigDecimal totalPriceWithTax = unitPriceWithTax.multiply(quantityConverted);

        return Product.builder()
            .code(templateInvoiceItem.getCode())
            .name(templateInvoiceItem.getName())
            .priceWithTax(totalPriceWithTax)
            .quantity(quantity)
            .build();
    }

    private static BigDecimal getUnitPriceWithTax(BigDecimal unitPriceWithoutTax, BigDecimal taxPercentage){

        BigDecimal hundred = BigDecimal.valueOf(100);

        BigDecimal taxFraction = taxPercentage.divide(hundred);

        BigDecimal taxValue = unitPriceWithoutTax.multiply(taxFraction);

        return unitPriceWithoutTax.add(taxValue);
    }

}
