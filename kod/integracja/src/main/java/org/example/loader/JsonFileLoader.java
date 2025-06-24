package org.example.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.exception.FileReadException;

import java.io.File;
import java.io.IOException;

public class JsonFileLoader {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T loadFromFileInside(String filePath, Class<T> type) throws FileReadException {

        File gotFile = FileReader.getFileFromInside(filePath);

        return loadStrFromFile(gotFile, type);
    }

    public static <T> T loadFromFileOutside(String filePath, Class<T> type) throws FileReadException {

        File gotFile = FileReader.getFileFromOutside(filePath);

        return loadStrFromFile(gotFile, type);
    }

    private static <T> T loadStrFromFile(File file, Class<T> type) throws FileReadException{

        try {
            return objectMapper.readValue(file, type);
        }
        catch (IOException e) {

            e.printStackTrace();

            throw new FileReadException(e.getMessage());
        }
    }

    public static <T> T loadFromStr(String value, Class<T> type) throws FileReadException{

        try {
            return objectMapper.readValue(value, type);
        }
        catch (IOException e) {

            e.printStackTrace();

            throw new FileReadException(e.getMessage());
        }
    }

    public static <T> T loadFromStr(String value, TypeReference<T> typeReference) throws FileReadException{

        try {
            return objectMapper.readValue(value, typeReference);
        }
        catch (IOException e) {

            e.printStackTrace();

            throw new FileReadException(e.getMessage());
        }
    }

}
