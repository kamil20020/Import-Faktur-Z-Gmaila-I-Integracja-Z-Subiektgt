package org.example.template.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateInvoiceItemTest {

    @ParameterizedTest
    @CsvSource(value = {
        "2, true",
        "3, false",
    })
    void shouldGetIsRowValid(int minLength, boolean expectedResult) {

        //given

        //when
        String[] row = new String[]{"1", "bb"};

        boolean gotResult = TemplateInvoiceItem.isRowValid(row, minLength);

        //then
        assertEquals(expectedResult, gotResult);
    }

    @Test
    void shouldExtract() {

        //given
        Map<String, String> expectedValues = new HashMap<>();

        TemplateInvoiceItem expectedTemplateInvoiceItem = new TemplateInvoiceItem(
            "Invoice-item-code-345",
            "Invoice item 123",
            new BigDecimal("22.38"),
            12,
            new BigDecimal("23.00")
        );

        expectedValues.put("name", expectedTemplateInvoiceItem.getName());
        expectedValues.put("code", "   Invoice-item-code-345   ");
        expectedValues.put("price", expectedTemplateInvoiceItem.getPrice().toString());
        expectedValues.put("quantity", expectedTemplateInvoiceItem.getQuantity().toString());
        expectedValues.put("tax", expectedTemplateInvoiceItem.getTax().toString());

        //when
        TemplateInvoiceItem gotTemplateInvoiceItem = TemplateInvoiceItem.extract(expectedValues);

        //then
        assertNotNull(gotTemplateInvoiceItem);
        assertEquals(expectedTemplateInvoiceItem, gotTemplateInvoiceItem);

    }

    @ParameterizedTest
    @CsvSource(value = {
        "22.38, 1, true, 22.38",
        "24.36, 4, true, 97.44",
        "24.38, 1, false, 29.9874",
        "24.36, 4, false, 119.8512",
    })
    void shouldGetTotalPriceWithTax(BigDecimal unitPrice, Integer quantity, boolean isInvoiceTax, BigDecimal expectedResult) {

        //given

        //when
        TemplateInvoiceItem templateInvoiceItem = TemplateInvoiceItem.builder()
            .price(unitPrice)
            .quantity(quantity)
            .tax(new BigDecimal("23"))
            .build();

        BigDecimal gotResult = templateInvoiceItem.getTotalPriceWithTax(isInvoiceTax);

        //then
        assertNotNull(gotResult);
        assertEquals(expectedResult, gotResult);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "22.38, true, 22.38",
        "24.38, false, 29.9874"
    })
    void shouldGetUnitPriceWithTax(BigDecimal unitPrice, boolean isInvoiceTax, BigDecimal expectedResult) {

        //given

        //when
        TemplateInvoiceItem templateInvoiceItem = TemplateInvoiceItem.builder()
            .price(unitPrice)
            .tax(new BigDecimal("23"))
            .build();

        BigDecimal gotResult = templateInvoiceItem.getUnitPriceWithTax(isInvoiceTax);

        //then
        assertNotNull(gotResult);
        assertEquals(expectedResult, gotResult);
    }

}