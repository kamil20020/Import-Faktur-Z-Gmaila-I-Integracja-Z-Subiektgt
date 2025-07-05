package org.example.model.sfera.own;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;

public class JavaTimeObjectMapper extends ObjectMapper {

    public JavaTimeObjectMapper(){

        super();

        SimpleModule simpleModule = new SimpleModule();

        simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer());

        registerModule(new JavaTimeModule());
        registerModule(simpleModule);
    }

}
