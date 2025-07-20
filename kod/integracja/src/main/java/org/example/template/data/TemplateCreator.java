package org.example.template.data;

import java.util.Map;

public record TemplateCreator(

    String name,
    String street,
    String postCode,
    String city,
    String nip
){

    public static TemplateCreator extract(Map<String, String> fieldsValuesMappings){

        if(fieldsValuesMappings == null || fieldsValuesMappings.isEmpty()){

            return new TemplateCreator(null, null, null, null, null);
        }

        String rawName = fieldsValuesMappings.get("name");
        String rawStreet = fieldsValuesMappings.get("street");
        String rawCity = fieldsValuesMappings.get("city");
        String rawPostCode = fieldsValuesMappings.get("post-code");
        String rawNip = fieldsValuesMappings.get("nip");

        String name = extractName(rawName);
        String street = extractStreet(rawStreet);
        String postCode = extractPostCode(rawPostCode);
        String city = extractCity(rawCity);
        String nip = extractNip(rawNip);

        return new TemplateCreator(
            name,
            street,
            postCode,
            city,
            nip
        );
    }

    public static String extractName(String value){

        if(value == null){

            return null;
        }

        return value
            .stripIndent();
    }

    public static String extractStreet(String value){

        if(value == null){

            return null;
        }

        return value
            .stripIndent()
            .replaceAll("ul. ", "");
    }

    public static String extractCity(String value){

        if(value == null){

            return null;
        }

        return value
            .stripIndent();
    }

    public static String extractPostCode(String value){

        if(value == null){

            return null;
        }

        return value
            .stripIndent();
    }

    public static String extractNip(String value){

        if(value == null){

            return null;
        }

        return value
            .replaceAll("NIP:", "")
            .replaceAll("PL", "")
            .stripIndent();
    }

}
