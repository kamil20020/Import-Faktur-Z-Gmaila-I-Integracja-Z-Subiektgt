package org.example.template.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public interface TemplateConverter {

    public static Integer convertToInteger(String value){

        value = value.replaceAll(",", "\\.")
                .replaceAll("\\s", "")
                .split("\\.")[0];

        return Integer.valueOf(value);
    }

    public static BigDecimal convertToBigDecimal(String value){

        value = value
                .replaceAll("\\s", "")
                .replaceAll(",", "\\.")
                .replaceAll("%", "")
                .replaceAll("PLN", "");

        int numberOfDotsOccurences = getNumberOfOccurencesInStr("\\.", value);

        if(numberOfDotsOccurences > 1){

            value = removeFromStr('.', value, numberOfDotsOccurences - 1);
        }

        return new BigDecimal(value);
    }

    private static String removeFromStr(char searchValue, String value, int numberOfRemoves){

        StringBuilder stringBuilder = new StringBuilder();

        int doneRemovesNumberOf = 0;

        for(char c : value.toCharArray()){

            if(searchValue == c && doneRemovesNumberOf < numberOfRemoves){

                doneRemovesNumberOf++;

                continue;
            }

            stringBuilder.append(c);
        }

        return stringBuilder.toString();
    }

    private static int getNumberOfOccurencesInStr(String searchStr, String value){

        int valueLengthWithoutSearchStrs = value.replaceAll(searchStr, "").length();

        return value.length() - valueLengthWithoutSearchStrs;
    }

    public static LocalDate tryToParseLocalDate(String format, String input) throws DateTimeParseException {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);

        try{

            return LocalDate.parse(input, dateTimeFormatter);
        }
        catch (DateTimeParseException e){

            //:D
        }

        return null;
    }

}
