package org.example.mapper.sfera;

import org.example.model.sfera.generated.Product;
import org.example.template.data.TemplateInvoiceItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SferaProductMapperTest {

    @ParameterizedTest
    @CsvSource(value = {
        "true, 16.2601626016",
        "false, 20",
    })
    void shouldMapForOrder(boolean invoiceIsTax, BigDecimal expectedUnitPriceWithoutTax) {

        //given
        TemplateInvoiceItem templateInvoiceItem = TemplateInvoiceItem.builder()
            .code("Code 123")
            .name("Name 123")
            .price(new BigDecimal("20"))
            .quantity(4)
            .tax(new BigDecimal("23.00"))
            .build();

        //when
        Product product = SferaProductMapper.map(templateInvoiceItem, invoiceIsTax);

        //then
        assertNotNull(product);
        assertEquals(templateInvoiceItem.getCode(), product.getCode());
        assertEquals(templateInvoiceItem.getName(), product.getName());
        assertEquals(expectedUnitPriceWithoutTax, product.getUnitPriceWithoutTax());
        assertEquals(templateInvoiceItem.getQuantity(), product.getQuantity());
        assertEquals(templateInvoiceItem.getTax(), product.getTax());
    }

    @Test
    public void shouldMapForOrderWhenTemplateInvoiceItemIsNull(){

        //given
        //when
        Product product = SferaProductMapper.map(null, true);

        //then
        assertNull(product);
    }

}