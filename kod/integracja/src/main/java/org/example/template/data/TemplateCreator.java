package org.example.template.data;

import java.util.HashMap;
import java.util.Map;

public record TemplateCreator(

    String name,
    String street,
    String postCode,
    String city,
    String nip
){

    public static TemplateCreator extract(String[] creatorLines, Map<String, Integer> fieldsSkipSpaceMappings, Integer creatorMaxSize){

        Integer nameSkipSpace = fieldsSkipSpaceMappings.get("name");
        Integer streetSkipSpace = fieldsSkipSpaceMappings.get("street");
        Integer citySkipSpace = fieldsSkipSpaceMappings.get("city");
        Integer nipSkipSpace = fieldsSkipSpaceMappings.get("nip");

        int nextIndex = 1;

        if(doesNameHasMoreThanOneLine(creatorLines, creatorMaxSize)){

            nextIndex = 2;
        }

        String name = extractCreatorName(creatorLines, creatorMaxSize);
        String street = extractCreatorStreet(creatorLines, nextIndex);
        String postCode = extractCreatorPostCode(creatorLines, nextIndex + 1);
        String city = extractCreatorCity(creatorLines, nextIndex + 1);
        String nip = extractCreatorNip(creatorLines, nextIndex + 2, nipSkipSpace);

        return new TemplateCreator(
            name,
            street,
            postCode,
            city,
            nip
        );
    }

    private static boolean doesNameHasMoreThanOneLine(String[] creatorLines, Integer creatorMaxSize){

        return creatorLines.length == creatorMaxSize;
    }

    private static String extractCreatorName(String[] creatorLines, Integer creatorMaxSize){

        String gotName = null;

        if(doesNameHasMoreThanOneLine(creatorLines, creatorMaxSize)){

            gotName = creatorLines[0] + creatorLines[1];
        }
        else{

            gotName = creatorLines[0];
        }

        return gotName
                .stripIndent();
    }

    private static String extractCreatorStreet(String[] creatorLines, int index){

        return creatorLines[index]
                .stripIndent()
                .replaceAll("ul. ", "");
    }

    private static String extractCreatorCity(String[] creatorLines, int index){

        StringBuilder stringBuilder = new StringBuilder();

        String[] gotWords = creatorLines[index]
                .stripIndent()
                .replaceAll("ul. ", "")
                .split("\\s");

        for(int i = 1; i < gotWords.length; i++){

            String value = gotWords[i];

            stringBuilder.append(value);
        }

        return stringBuilder.toString()
                .stripIndent();
    }

    private static String extractCreatorPostCode(String[] creatorLines, int index){

        return creatorLines[index]
                .stripIndent()
                .replaceAll("ul. ", "")
                .split("\\s")[1];
    }

    public static String extractCreatorNip(String[] creatorLines, int index, Integer skipSpace){

        if(skipSpace != null){

            index += skipSpace;
        }

        return creatorLines[index]
                .strip()
                .replaceAll("NIP: ", "")
                .replaceAll("PL", "");
    }

}
