package org.example.mapper.gmail;

import java.util.Base64;

public interface GmailDataMapper {

    public static byte[] decode(String content){

        if(content == null || content.isEmpty()){

            return null;
        }

        content = content.replaceAll("\\s", "");

        int padding = 4 - (content.length() % 4);

        if (padding < 4) {

            content += "=".repeat(padding);
        }

        return Base64.getUrlDecoder().decode(content);
    }

}
