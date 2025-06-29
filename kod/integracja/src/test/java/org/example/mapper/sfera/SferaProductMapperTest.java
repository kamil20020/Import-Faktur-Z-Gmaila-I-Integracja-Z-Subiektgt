package org.example.mapper.sfera;

import org.example.external.sfera.generated.Product;
import org.example.template.data.TemplateInvoiceItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SferaProductMapperTest {

    @ParameterizedTest
    @CsvSource(value = {
        "true, 89.52",
        "false, 110.1096",
    })
    void shouldMapWithInvoiceTax(boolean invoiceIsTax, BigDecimal expectedTotalPriceWithTax) {

        //given
        TemplateInvoiceItem templateInvoiceItem = TemplateInvoiceItem.builder()
            .code("Code 123")
            .name("Name 123")
            .price(new BigDecimal("22.38"))
            .quantity(4)
            .tax(new BigDecimal("23.00"))
            .build();

        //when
        Product product = SferaProductMapper.map(templateInvoiceItem, invoiceIsTax);

        //then
        assertNotNull(product);
        assertEquals(templateInvoiceItem.getCode(), product.getCode());
        assertEquals(templateInvoiceItem.getName(), product.getName());
        assertEquals(expectedTotalPriceWithTax, product.getPriceWithTax());
        assertEquals(templateInvoiceItem.getQuantity(), product.getQuantity());
        assertEquals(templateInvoiceItem.getTax(), product.getTax());
    }

    @Test
    public void shouldMapWhenTemplateInvoiceItemIsNull(){

        //given

        //when
        Product product = SferaProductMapper.map(null, true);

        //then
        assertNull(product);
    }

}