package org.example.template.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DataExtractedFromTemplate(

    String place,
    LocalDate creationDate,
    LocalDate receiveDate,
    String title,
    TemplateCreator creator,
    List<TemplateInvoiceItem> invoiceItems,
    boolean isTaxOriented,
    BigDecimal totalPrice,
    LocalDate payDate
){}
