package org.example.template.data;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TemplateBasicData {

    private String place = "";
    private LocalDate creationDate;
    private LocalDate receiveDate;
    private String title = "";

    public static TemplateBasicData extract(Map<String, String> gotValues){

        if(gotValues == null || gotValues.isEmpty()){

            return new TemplateBasicData();
        }

        return TemplateBasicData.builder()
            .place(extractPlace(gotValues))
            .creationDate(extractCreationDate(gotValues))
            .receiveDate(extractReceiveDate(gotValues))
            .title(extractTitle(gotValues))
            .build();
    }

    private static String extractPlace(Map<String, String> gotValues){

        String gotValue = gotValues.get("place");

        return TemplateConverter.clearValue(gotValue);
    }

    private static LocalDate extractCreationDate(Map<String, String> gotValues){

        String rawValue = gotValues.get("creationDate");

        return TemplateConverter.tryToParseLocalDate(rawValue);
    }

    private static LocalDate extractReceiveDate(Map<String, String> gotValues){

        String rawValue = gotValues.get("receiveDate");

        return TemplateConverter.tryToParseLocalDate(rawValue);
    }

    private static String extractTitle(Map<String, String> gotValues){

        String gotValue = gotValues.get("title");

        return TemplateConverter.clearValue(gotValue);
    }

}
