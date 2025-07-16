package org.example.template.row;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import org.example.template.field.AreaTemplateRowField;
import org.example.template.field.TemplateRowField;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = TemplateRow.class, name = "AREA"),
    @JsonSubTypes.Type(value = HeightTemplateRow.class, name = "HORIZONTAL")
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TemplateRow{

    private String type;
    protected List<TemplateRowField> fields;

    public Map<String, String> getValues(Function<AreaTemplateRowField, String[]> extractValues){

        Map<String, String> values = new HashMap<>();

        if(fields == null || fields.isEmpty()){

            return values;
        }

        for(TemplateRowField field : fields){

            String fieldName = field.getName();

            if(field.isHidden()){

                String defaultValue = field.getDefaultValue();

                values.put(fieldName, defaultValue);

                continue;
            }

            AreaTemplateRowField areaField = (AreaTemplateRowField) field;

            String[] gotValues = extractValues.apply(areaField);

            String extractedValue = areaField.extract(gotValues);

            values.put(fieldName, extractedValue);
        }

        return values;
    }

}
