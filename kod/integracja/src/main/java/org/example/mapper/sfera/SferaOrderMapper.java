package org.example.mapper.sfera;

import org.example.api.sfera.request.CreateOrderRequest;
import org.example.model.sfera.generated.Customer;
import org.example.model.sfera.generated.Product;
import org.example.template.data.DataExtractedFromTemplate;
import org.example.template.data.TemplateCreator;
import org.example.template.data.TemplateInvoiceItem;

import java.util.List;
import java.util.stream.Collectors;

public interface SferaOrderMapper {

    public static CreateOrderRequest map(DataExtractedFromTemplate dataExtractedFromTemplate, String externalId){

        boolean isInvoiceTaxOriented = dataExtractedFromTemplate.isTaxOriented();

        List<TemplateInvoiceItem> templateInvoiceItems = dataExtractedFromTemplate.invoiceItems();

        List<Product> products = templateInvoiceItems.stream()
            .map(templateInvoiceItem -> SferaProductMapper.map(templateInvoiceItem, isInvoiceTaxOriented))
            .collect(Collectors.toList());

        TemplateCreator templateCreator = dataExtractedFromTemplate.creator();

        Customer customer = SferaCustomerMapper.map(templateCreator);

        return new CreateOrderRequest(
            dataExtractedFromTemplate.title(),
            externalId,
            dataExtractedFromTemplate.totalPrice(),
            customer,
            products,
            dataExtractedFromTemplate.creationDate(),
            dataExtractedFromTemplate.receiveDate(),
            dataExtractedFromTemplate.place(),
            dataExtractedFromTemplate.payDate()
        );
    }

}
