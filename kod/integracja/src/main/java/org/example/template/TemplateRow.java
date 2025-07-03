package org.example.template;

import java.util.List;

public record TemplateRow(

    TemplateRectCords coords,
    Integer skipSpace,
    Integer maxSize,
    List<TemplateRowField> fields,
    String startStr,
    Integer skipStart,
    Integer minLength,
    float rowHeight,
    float startOffset,
    String endStr
){}
