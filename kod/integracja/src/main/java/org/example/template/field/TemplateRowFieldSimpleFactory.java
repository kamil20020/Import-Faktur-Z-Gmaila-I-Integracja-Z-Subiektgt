package org.example.template.field;

public interface TemplateRowFieldSimpleFactory {

    public static TemplateRowField create(TemplateRowFieldType type){

        return switch (type){

            case HORIZONTAL -> new HorizontalTemplateRowField();
            case AREA -> new AreaTemplateRowField();
            default -> new TemplateRowField();
        };
    }

}
