package org.example.template.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TemplateConverter {

    private static final List<String> toCheckDateFormats;

    static {

        toCheckDateFormats = List.of(
            "yyy-MM-dd",
            "yyy.MM.dd",
            "yyy/MM/dd",
            "dd-MM-yyyy",
            "dd.MM.yyyy",
            "dd/MM/yyyy"
        );
    }

    private TemplateConverter(){


    }

    public static Integer convertToInteger(String value){

        if(isEmpty(value)){

            return null;
        }

        value = value.replaceAll(",", "\\.")
            .replaceAll("\\s", "")
            .split("\\.")[0];

        try{

            return Integer.valueOf(value);
        }
        catch (NumberFormatException e){

            e.printStackTrace();
        }

        return null;
    }

    public static BigDecimal convertToBigDecimal(String value){

        if(isEmpty(value)){

            return null;
        }

        value = value
            .replaceAll("\\s", "")
            .replaceAll(",", "\\.")
            .replaceAll("%", "")
            .replaceAll("PLN", "");

        int numberOfDotsOccurences = getNumberOfOccurencesInStr("\\.", value);

        if(numberOfDotsOccurences > 1){

            value = removeFromStr('.', value, numberOfDotsOccurences - 1);
        }

        try {
            return new BigDecimal(value);
        }
        catch (NumberFormatException e){

            e.printStackTrace();
        }

        return null;
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

    public static int getNumberOfOccurencesInStr(String searchStr, String value){

        int valueLengthWithoutSearchStrs = value.replaceAll(searchStr, "").length();

        return value.length() - valueLengthWithoutSearchStrs;
    }

    public static LocalDate tryToParseLocalDate(String input){

        if(isEmpty(input)){

            return null;
        }

        LocalDate gotDate = null;

        for (String format : toCheckDateFormats){

            gotDate = TemplateConverter.tryToParseLocalDate(format, input);

            if(gotDate != null){

                break;
            }
        }

        return gotDate;
    }

    public static LocalDate tryToParseLocalDate(String format, String input) throws DateTimeParseException {

        if(isEmpty(input)){

            return null;
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);

        try{

            return LocalDate.parse(input, dateTimeFormatter);
        }
        catch (DateTimeParseException e){

            //:D
        }

        return null;
    }

    private static boolean isEmpty(String value){

        return value == null || value.isEmpty();
    }

}
