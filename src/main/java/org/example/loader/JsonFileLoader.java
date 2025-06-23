package org.example.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.exception.FileReadException;

import java.io.File;
import java.io.IOException;

public class JsonFileLoader {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T loadFromFileInside(String filePath, Class<T> type) throws FileReadException {

        File gotFile = FileReader.getFileFromInside(filePath);

        try {
            return objectMapper.readValue(gotFile, type);
        }
        catch (IOException e) {

            e.printStackTrace();

            throw new FileReadException(e.getMessage());
        }
    }
}
