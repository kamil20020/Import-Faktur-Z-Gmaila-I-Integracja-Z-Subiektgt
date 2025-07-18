package org.example.template.field;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = TemplateRowField.class, name = "NO_CORDS"),
    @JsonSubTypes.Type(value = AreaTemplateRowField.class, name = "AREA"),
    @JsonSubTypes.Type(value = HorizontalTemplateRowField.class, name = "HORIZONTAL")
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TemplateRowField{

    private String name;

    private String type;

    @JsonProperty(value = "isHidden")
    private boolean isHidden;

    private String defaultValue;
    private Integer index;
    private String separator;

    @JsonIgnore
    public void copy(TemplateRowField field){

        this.name = field.name;
        this.type = field.type;
        this.isHidden = field.isHidden;
        this.defaultValue = field.defaultValue;
        this.index = field.index;
        this.separator = field.separator;
    }

    @JsonIgnore
    public String extract(String[] values){

        if(isHidden){

            return defaultValue;
        }

        if(values == null || values.length == 0){

            return null;
        }

        String value = String.join(" ", values);

        if(index == null){

            return value;
        }

        return extractWithIndex(value);
    }

    @JsonIgnore
    private String extractWithIndex(String value){

        if(separator == null || separator.isEmpty()){

            throw new IllegalArgumentException("Separator of template row field is required when index is given" + name);
        }

        String[] values = value
            .split(separator);

        if(index > values.length - 1){

            return null;
        }

        String gotValue = values[index]
            .stripIndent();

        return gotValue;
    }

    @JsonIgnore
    public void handleRect(Rectangle2D.Float rect) throws IllegalStateException{

        throw new IllegalStateException("Standard template row field doesnt support cords");
    }

}
