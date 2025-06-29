package org.example.mapper.sfera;

import org.example.api.sfera.request.CreateOrderRequest;
import org.example.external.sfera.generated.Customer;
import org.example.external.sfera.generated.Product;
import org.example.template.data.DataExtractedFromTemplate;
import org.example.template.data.TemplateCreator;
import org.example.template.data.TemplateInvoiceItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class SferaOrderMapperTestTest {

    @Test
    void shouldMap() {

        //given
        TemplateCreator templateCreator = new TemplateCreator(
            null,
            null,
            null,
            null,
            null
        );

        List<TemplateInvoiceItem> templateInvoiceItems = List.of(new TemplateInvoiceItem(), new TemplateInvoiceItem());

        DataExtractedFromTemplate dataExtractedFromTemplate = new DataExtractedFromTemplate(
            "Wrocław",
            LocalDate.now(),
            LocalDate.now(),
            "Invoice 123",
            templateCreator,
            templateInvoiceItems,
            true,
            new BigDecimal("22.38")
        );

        Customer expectedCustomer = new Customer();

        List<Product> expectedProducts = List.of(new Product(), new Product());

        //when
        try(
            MockedStatic<SferaCustomerMapper> sferaCustomerMapperMock = Mockito.mockStatic(SferaCustomerMapper.class);
            MockedStatic<SferaProductMapper> sferaProductMapperMock = Mockito.mockStatic(SferaProductMapper.class)
        ){

            sferaCustomerMapperMock.when(() -> SferaCustomerMapper.map(any())).thenReturn(expectedCustomer);

            for(int i = 0; i < templateInvoiceItems.size(); i++){

                TemplateInvoiceItem templateInvoiceItem = templateInvoiceItems.get(i);
                Product expectedProduct = expectedProducts.get(i);

                sferaProductMapperMock.when(() -> SferaProductMapper.map(eq(templateInvoiceItem), anyBoolean())).thenReturn(expectedProduct);
            }

            CreateOrderRequest request = SferaOrderMapper.map(dataExtractedFromTemplate);

            //then
            assertNotNull(request);
            assertEquals(dataExtractedFromTemplate.place(), request.getCreationPlace());
            assertEquals(dataExtractedFromTemplate.creationDate(), request.getCreationDate());
            assertEquals(dataExtractedFromTemplate.receiveDate(), request.getDeliveryDate());
            assertEquals(dataExtractedFromTemplate.title(), request.getReference());
            assertEquals(dataExtractedFromTemplate.title(), request.getExternalId());
            assertEquals(expectedCustomer, request.getCustomer());
            assertTrue(request.getProducts().containsAll(expectedProducts));
            assertEquals(dataExtractedFromTemplate.isTaxOriented(), request.isInvoiceRequired());
            assertEquals(dataExtractedFromTemplate.totalPrice(), request.getAmount());

            sferaCustomerMapperMock.verify(() -> SferaCustomerMapper.map(templateCreator));

            for (TemplateInvoiceItem templateInvoiceItem : templateInvoiceItems) {

                sferaProductMapperMock.verify(() -> SferaProductMapper.map(templateInvoiceItem, true), times(templateInvoiceItems.size()));
            }
        }
    }

}