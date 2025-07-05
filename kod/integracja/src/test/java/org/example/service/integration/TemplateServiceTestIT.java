package org.example.service.integration;

import org.example.loader.FileReader;
import org.example.service.TemplateService;
import org.example.template.data.DataExtractedFromTemplate;
import org.example.template.data.TemplateCreator;
import org.example.template.data.TemplateInvoiceItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TemplateServiceTestIT {

    private static final TemplateService templateService = new TemplateService();

    @Test
    void testForGardenParts() {

        //given
        TemplateInvoiceItem expectedFirstItem = TemplateInvoiceItem.builder()
            .code("08-04024")
            .name("ROZRUSZNIK STIHL TS410, TS420 EVEREST")
            .quantity(2)
            .price(new BigDecimal("55.71"))
            .tax(new BigDecimal("23.00"))
            .build();

        TemplateInvoiceItem expectedSecondItem = TemplateInvoiceItem.builder()
            .code("14-13005")
            .name("NÓŻ KOSIAREK SPALINOWYCH EFCO OLEOMAC VICTUS G48TK G48PK G48PB")
            .quantity(6)
            .price(new BigDecimal("32.46"))
            .tax(new BigDecimal("23.00"))
            .build();

        TemplateInvoiceItem expectedLastItem = TemplateInvoiceItem.builder()
            .code("przesylka vat")
            .name("Opłata za przesyłkę")
            .quantity(1)
            .price(new BigDecimal("19.00"))
            .tax(new BigDecimal("23.00"))
            .build();

        List<TemplateInvoiceItem> expectedFirstSecondLastItems = List.of(expectedFirstItem, expectedSecondItem, expectedLastItem);

        LocalDate date = LocalDate.of(2025, 6, 11);

        TemplateCreator expectedTemplateCreator = new TemplateCreator(
            "\"GARDENPARTS\" SPÓŁKA Z OGRANICZONĄ ODPOWIEDZIALNOŚCIĄ",
            "Nowogrodzka 31",
            "00-511",
            "WARSZAWA",
            "7010349162"
        );

        DataExtractedFromTemplate expectedData = new DataExtractedFromTemplate(
            "WARSZAWA",
            date,
            date,
            "Faktura nr FV/02058/06/25",
            expectedTemplateCreator,
            null,
            false,
            new BigDecimal("1265.46")
        );

        Integer expectedNumberOfItems = 8;

        //when
        //then
        test("invoices/garden parts.pdf", expectedFirstSecondLastItems, expectedData, expectedNumberOfItems);
    }

    @Test
    void testForTechpil() {

        //given
        TemplateInvoiceItem expectedFirstItem = TemplateInvoiceItem.builder()
            .code("11230071030")
            .name("Bęben sprz. MS 230-250/021-025 3/8\" Z7  pływający")
            .quantity(5)
            .price(new BigDecimal("77.24"))
            .tax(new BigDecimal("23"))
            .build();

        TemplateInvoiceItem expectedSecondItem = TemplateInvoiceItem.builder()
            .code("11430071003")
            .name("Bęben sprz. MS 231-251 .325\" Z7 pływający")
            .quantity(4)
            .price(new BigDecimal("78.34"))
            .tax(new BigDecimal("23"))
            .build();

        TemplateInvoiceItem expectedLastItem = TemplateInvoiceItem.builder()
            .code("04640350006")
            .name("Koszulka czarna STIHL rozm. M")
            .quantity(1)
            .price(new BigDecimal("23.78"))
            .tax(new BigDecimal("23"))
            .build();

        List<TemplateInvoiceItem> expectedFirstSecondLastItems = List.of(expectedFirstItem, expectedSecondItem, expectedLastItem);

        LocalDate date = LocalDate.of(2025, 5, 29);

        TemplateCreator expectedTemplateCreator = new TemplateCreator(
            "TECHPIL s.c. ",
            "Kominiarska 42",
            "51-180",
            "Wrocław",
            "895-203-95-39"
        );

        DataExtractedFromTemplate expectedData = new DataExtractedFromTemplate(
            "Wrocław",
            date,
            date,
            "Faktura VAT  779/2025 oryginał",
            expectedTemplateCreator,
            null,
            false,
            new BigDecimal("38899.02")
        );

        Integer expectedNumberOfItems = 53;

        //when
        //then
        test("invoices/techpil.pdf", expectedFirstSecondLastItems, expectedData, expectedNumberOfItems);
    }

    @Test
    void testForRozkwit() {

        //given
        TemplateInvoiceItem expectedFirstItem = TemplateInvoiceItem.builder()
            .code("FDA50S-02101-2")
            .name("NÓŻ KOSIARKI MASTER CUT FD51, M50, M51 - 51CM WYRZUT, MULCZOWANIE")
            .quantity(2)
            .price(new BigDecimal("19.92"))
            .tax(new BigDecimal("23"))
            .build();

        TemplateInvoiceItem expectedSecondItem = TemplateInvoiceItem.builder()
            .code("FDS43S-02101")
            .name("NÓŻ KOSIARKI MASTER CUT FD43PBS300 , FD43PLC123 41CM")
            .quantity(2)
            .price(new BigDecimal("19.92"))
            .tax(new BigDecimal("23"))
            .build();

        TemplateInvoiceItem expectedLastItem = TemplateInvoiceItem.builder()
            .code("506-217")
            .name("NÓŻ DO KOSIARKI JL50 M50 M51 FD51 - ŁOPATA")
            .quantity(1)
            .price(new BigDecimal("45.96"))
            .tax(new BigDecimal("23"))
            .build();

        List<TemplateInvoiceItem> expectedFirstSecondLastItems = List.of(expectedFirstItem, expectedSecondItem, expectedLastItem);

        LocalDate date = LocalDate.of(2025, 5, 30);

        TemplateCreator expectedTemplateCreator = new TemplateCreator(
            "ROZKWIT SPÓŁKA Z OGRANICZONĄ ODPOWIEDZIALNOŚCIĄ",
            "Narutowicza 3",
            "33-100",
            "Tarnów",
            "9930697187"
        );

        DataExtractedFromTemplate expectedData = new DataExtractedFromTemplate(
            "Tarnów",
            date,
            date,
            "FS/01530/2025/B26A",
            expectedTemplateCreator,
            null,
            false,
            new BigDecimal("201.57")
        );

        Integer expectedNumberOfItems = 4;

        //when
        //then
        test("invoices/rozkwit.pdf", expectedFirstSecondLastItems, expectedData, expectedNumberOfItems);
    }

    private void test(String invoiceFilePath, List<TemplateInvoiceItem> expectedFirstSecondLastItems, DataExtractedFromTemplate expectedData, Integer expectedNumberOfItems) {

        //given
        byte[] data = FileReader.getDataFromFileInside(invoiceFilePath);

        //when
        DataExtractedFromTemplate dataExtractedFromTemplate = templateService.applyGoodTemplateForData(data);

        //then
        assertEquals(expectedData.title(), dataExtractedFromTemplate.title());
        assertEquals(expectedData.creationDate(), dataExtractedFromTemplate.creationDate());
        assertEquals(expectedData.receiveDate(), dataExtractedFromTemplate.receiveDate());
        assertEquals(expectedData.place(), dataExtractedFromTemplate.place());
        assertEquals(expectedData.isTaxOriented(), dataExtractedFromTemplate.isTaxOriented());
        assertEquals(expectedData.totalPrice(), dataExtractedFromTemplate.totalPrice());
        assertEquals(expectedNumberOfItems, dataExtractedFromTemplate.invoiceItems().size());

        TemplateInvoiceItem gotFirstTemplateInvoiceItem = dataExtractedFromTemplate.invoiceItems().get(0);
        TemplateInvoiceItem gotSecondTemplateInvoiceItem = dataExtractedFromTemplate.invoiceItems().get(1);
        TemplateInvoiceItem gotLastTemplateInvoiceItem = dataExtractedFromTemplate.invoiceItems().get(dataExtractedFromTemplate.invoiceItems().size() - 1);

        assertEquals(expectedFirstSecondLastItems.get(0), gotFirstTemplateInvoiceItem);
        assertEquals(expectedFirstSecondLastItems.get(1), gotSecondTemplateInvoiceItem);
        assertEquals(expectedFirstSecondLastItems.get(2), gotLastTemplateInvoiceItem);
    }

}