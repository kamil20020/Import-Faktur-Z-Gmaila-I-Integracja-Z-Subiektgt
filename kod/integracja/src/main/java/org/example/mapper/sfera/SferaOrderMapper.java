package org.example.mapper.sfera;

import org.example.api.sfera.request.CreateOrderRequest;
import org.example.external.sfera.generated.Customer;
import org.example.external.sfera.generated.Product;
import org.example.template.data.TemplateCombinedData;
import org.example.template.data.TemplateCreator;
import org.example.template.data.TemplateInvoiceItem;

import java.util.List;
import java.util.stream.Collectors;

public interface SferaOrderMapper {

    public static CreateOrderRequest map(TemplateCombinedData templateCombinedData){

        boolean isInvoiceTaxOriented = templateCombinedData.isTaxOriented();

        List<TemplateInvoiceItem> templateInvoiceItems = templateCombinedData.invoiceItems();

        List<Product> products = templateInvoiceItems.stream()
            .map(templateInvoiceItem -> SferaProductMapper.map(templateInvoiceItem, isInvoiceTaxOriented))
            .collect(Collectors.toList());

        TemplateCreator templateCreator = templateCombinedData.creator();

        Customer customer = SferaCustomerMapper.map(templateCreator);

        return new CreateOrderRequest(
            "Faktura zakupu",
            templateCombinedData.title(),
            templateCombinedData.totalPrice(),
            customer,
            products
        );
    }

}
