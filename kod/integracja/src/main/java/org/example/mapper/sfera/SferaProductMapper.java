package org.example.mapper.sfera;

import org.example.external.sfera.generated.Product;
import org.example.template.data.TemplateInvoiceItem;

import java.math.BigDecimal;

public interface SferaProductMapper {

    public static Product map(TemplateInvoiceItem templateInvoiceItem, boolean isInvoiceTaxOriented){

        Integer quantity = templateInvoiceItem.getQuantity();

        BigDecimal totalPriceWithTax = templateInvoiceItem.getTotalPriceWithTax(isInvoiceTaxOriented);

        return Product.builder()
            .code(templateInvoiceItem.getCode())
            .name(templateInvoiceItem.getName())
            .priceWithTax(totalPriceWithTax)
            .quantity(quantity)
            .build();
    }

}
