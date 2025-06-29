package org.example.mapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public interface Base64Mapper {

    static String mapToBase64(String content){

        if(content == null){

            return null;
        }

        byte[] contentByte = content.getBytes(StandardCharsets.UTF_8);
        Base64.Encoder base64Encoder = Base64.getEncoder();

        return base64Encoder.encodeToString(contentByte);
    }

    static String mapFromBase64(String base64Content){

        if(base64Content == null){

            return null;
        }

        Base64.Decoder base64Decoder = Base64.getDecoder();
        byte[] content = base64Decoder.decode(base64Content);

        return new String(content, StandardCharsets.UTF_8);
    }

}
