package org.example.mapper.sfera;

import org.example.model.sfera.generated.Customer;
import org.example.template.data.TemplateCreator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SferaCustomerMapperTest {

    @Test
    void shouldMap() {

        //given
        TemplateCreator templateCreator = new TemplateCreator(
            "Adam Nowak",
            "street 123",
            "12-345",
            "city 123",
            "123456"
        );

        //when
        Customer gotCustomer = SferaCustomerMapper.map(templateCreator);

        //then
        assertNotNull(gotCustomer);
        assertEquals(templateCreator.name(), gotCustomer.getCompanyName());
        assertEquals(templateCreator.street(), gotCustomer.getStreet());
        assertEquals(templateCreator.city(), gotCustomer.getCity());
        assertEquals(templateCreator.postCode(), gotCustomer.getPostCode());
        assertEquals(templateCreator.nip(), gotCustomer.getNip());
        assertTrue(gotCustomer.getIsCompany());
    }

    @Test
    public void shouldMapWhenTemplateCreatorIsNull(){

        //given

        //when
        Customer gotCustomer = SferaCustomerMapper.map(null);

        //then
        assertNull(gotCustomer);
    }

}