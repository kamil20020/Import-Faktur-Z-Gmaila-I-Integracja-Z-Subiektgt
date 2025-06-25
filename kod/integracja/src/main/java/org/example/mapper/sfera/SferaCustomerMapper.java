package org.example.mapper.sfera;

import org.example.external.sfera.generated.Customer;
import org.example.template.data.TemplateCreator;

public interface SferaCustomerMapper {

    public static Customer map(TemplateCreator templateCreator){

        return Customer.builder()
            .name(templateCreator.name())
            .nip(templateCreator.nip())
            .street(templateCreator.street())
            .postCode(templateCreator.postCode())
            .city(templateCreator.city())
            .build();
    }

}
