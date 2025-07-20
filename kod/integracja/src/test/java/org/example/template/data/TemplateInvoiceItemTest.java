package org.example.template.data;

import org.example.template.Template;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateInvoiceItemTest {

    @Test
    public void shouldGetHasAllValuesWithoutCode(){

        //given
        TemplateInvoiceItem item = TemplateInvoiceItem.builder()
            .name("item")
            .price(new BigDecimal("24.48"))
            .quantity(12)
            .tax(new BigDecimal("8"))
            .build();

        //when
        boolean result = item.hasAllValuesWithoutCode();

        //then
        assertTrue(result);
    }

    @Test
    public void shouldGetHasNotAllValuesWithoutCode(){

        //given
        TemplateInvoiceItem item = new TemplateInvoiceItem();

        //when
        boolean result = item.hasAllValuesWithoutCode();

        //then
        assertFalse(result);
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

    @ParameterizedTest
    @CsvSource(value = {
        "20, true, 16.2601626016",
        "20, false, 20"
    })
    void shouldGetUnitPriceWithoutTax(BigDecimal unitPrice, boolean isInvoiceTax, BigDecimal expectedResult) {

        //given
        //when
        TemplateInvoiceItem templateInvoiceItem = TemplateInvoiceItem.builder()
            .price(unitPrice)
            .tax(new BigDecimal("23"))
            .build();

        BigDecimal gotResult = templateInvoiceItem.getUnitPriceWithoutTax(isInvoiceTax);

        //then
        assertNotNull(gotResult);
        assertEquals(expectedResult, gotResult);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "20, true, 32.5203252032",
        "20, false, 40"
    })
    void shouldGeTotalPriceWithoutTax(BigDecimal unitPrice, boolean isInvoiceTax, BigDecimal expectedResult) {

        //given
        //when
        TemplateInvoiceItem templateInvoiceItem = TemplateInvoiceItem.builder()
            .price(unitPrice)
            .tax(new BigDecimal("23"))
            .quantity(2)
            .build();

        BigDecimal gotResult = templateInvoiceItem.getTotalPriceWithoutTax(isInvoiceTax);

        //then
        assertNotNull(gotResult);
        assertEquals(expectedResult, gotResult);
    }

    @Test
    void shouldExtractWithNoValues() {

        //given
        //when
        TemplateInvoiceItem gotData = TemplateInvoiceItem.extract(null);

        //then
        assertNotNull(gotData);
        assertEquals("", gotData.getName());
        assertEquals("", gotData.getCode());
        assertNull(gotData.getPrice());
        assertNull(gotData.getQuantity());
        assertNull(gotData.getTax());
    }

    @Test
    void shouldExtractWithEmptyValues() {

        //given
        //when
        TemplateInvoiceItem gotData = TemplateInvoiceItem.extract(new HashMap<>());

        //then
        assertNotNull(gotData);
        assertEquals("", gotData.getName());
        assertEquals("", gotData.getCode());
        assertNull(gotData.getPrice());
        assertNull(gotData.getQuantity());
        assertNull(gotData.getTax());
    }

    @Test
    public void shouldAppend(){

        //given
        TemplateInvoiceItem item = TemplateInvoiceItem.builder()
            .name("name 123")
            .code("code 123")
            .price(new BigDecimal("26.48"))
            .quantity(12)
            .tax(new BigDecimal("23"))
            .build();

        TemplateInvoiceItem newData = TemplateInvoiceItem.builder()
            .name("4")
            .code("5")
            .price(new BigDecimal("28.52"))
            .quantity(16)
            .tax(new BigDecimal("8"))
            .build();

        //when
        item.appendData(newData);

        //then
        assertEquals("name 123 4", item.getName());
        assertEquals(newData.getCode(), item.getCode());
        assertEquals(newData.getPrice(), item.getPrice());
        assertEquals(newData.getQuantity(), item.getQuantity());
        assertEquals(newData.getTax(), item.getTax());
    }

    @Test
    public void shouldNotAppendForNullData(){

        //given
        TemplateInvoiceItem expectedItem = TemplateInvoiceItem.builder()
            .name("name 123")
            .code("code 123")
            .price(new BigDecimal("26.48"))
            .quantity(12)
            .tax(new BigDecimal("23"))
            .build();

        TemplateInvoiceItem toChangeItem = TemplateInvoiceItem.builder()
            .name(expectedItem.getName())
            .code(expectedItem.getCode())
            .price(expectedItem.getPrice())
            .quantity(expectedItem.getQuantity())
            .tax(expectedItem.getTax())
            .build();

        TemplateInvoiceItem newData = new TemplateInvoiceItem();

        //when
        toChangeItem.appendData(newData);

        //then
        assertEquals(expectedItem.getName(), toChangeItem.getName());
        assertEquals(expectedItem.getCode(), toChangeItem.getCode());
        assertEquals(expectedItem.getPrice(), toChangeItem.getPrice());
        assertEquals(expectedItem.getQuantity(), toChangeItem.getQuantity());
        assertEquals(expectedItem.getTax(), toChangeItem.getTax());
    }

    @Test
    public void shouldNotAppendForEmptyData(){

        //given
        TemplateInvoiceItem expectedItem = TemplateInvoiceItem.builder()
            .name("name 123")
            .code("code 123")
            .build();

        TemplateInvoiceItem toChangeItem = TemplateInvoiceItem.builder()
            .name(expectedItem.getName())
            .code(expectedItem.getCode())
            .build();

        TemplateInvoiceItem newData = TemplateInvoiceItem.builder()
            .name("")
            .code("")
            .build();

        //when
        toChangeItem.appendData(newData);

        //then
        assertEquals(expectedItem.getName(), toChangeItem.getName());
        assertEquals(expectedItem.getCode(), toChangeItem.getCode());
    }

    @Test
    public void shouldNotAppendForNoData(){

        //given
        TemplateInvoiceItem item = new TemplateInvoiceItem();

        //when
        //then
        item.appendData(null);
    }

}