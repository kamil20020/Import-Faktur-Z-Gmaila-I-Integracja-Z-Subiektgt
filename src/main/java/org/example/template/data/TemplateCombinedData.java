package org.example.template.data;

import java.time.LocalDate;
import java.util.List;

public record TemplateCombinedData(

    String place,
    LocalDate creationDate,
    LocalDate receiveDate,
    String title,
    TemplateCreator creator,
    List<TemplateInvoiceItem> invoiceItems,
    boolean isTaxOriented
){}
