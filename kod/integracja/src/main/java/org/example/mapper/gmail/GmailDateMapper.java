package org.example.mapper.gmail;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.Locale;

public class GmailDateMapper {

    static final DateTimeFormatter formatter;

    private GmailDateMapper(){


    }

    static {

        formatter = new DateTimeFormatterBuilder()
            .optionalStart()
                .appendPattern("EEE, ")
            .optionalEnd()
            .appendPattern("d MMM yyyy HH:mm:ss Z")
            .optionalStart()
                .appendLiteral(" (")
                .appendZoneText(TextStyle.SHORT)
                .appendLiteral(")")
            .optionalEnd()
            .toFormatter(Locale.ENGLISH);
    }

    public static OffsetDateTime fromStr(String value){

        if(value == null){

            return null;
        }

        return OffsetDateTime.parse(value, formatter);
    }

    public static String toStr(OffsetDateTime date){

        if(date == null){

            return null;
        }

        return formatter.format(date);
    }

}
