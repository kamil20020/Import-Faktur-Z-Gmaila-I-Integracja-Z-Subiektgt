package org.example.template;

public record TemplateRowField(

    String name,
    float xMinCord,
    float xMaxCord,
    Float yMinCord,
    Float yMaxCord,
    Integer skipSpace,
    Integer index,
    String separator
){}
