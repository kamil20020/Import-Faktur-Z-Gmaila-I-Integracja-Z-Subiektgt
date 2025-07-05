package org.example.template;

public record TemplateRowField(

    String name,
    float xMinCord,
    float xMaxCord,
    Integer skipSpace,
    Integer index,
    String separator
){}
