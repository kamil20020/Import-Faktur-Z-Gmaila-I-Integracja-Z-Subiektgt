package org.example.loader;

import org.example.exception.FileReadException;
import org.example.loader.pdf.PdfFileReader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReader {

    static String loadStrFromFileFromOutside(String filePath) throws URISyntaxException{

        File file = getFileFromOutside(filePath);

        return loadStrFromFile(file, StandardCharsets.UTF_8);
    }

    static String loadStrFromFileInside(String filePath) throws URISyntaxException{

        File file = getFileFromInside(filePath);

        return loadStrFromFile(file, StandardCharsets.UTF_8);
    }

    public static File getFileFromInside(String filePath) throws FileReadException{

        URL foundResourceURL = PdfFileReader.class.getClassLoader().getResource(filePath);

        if(foundResourceURL == null){

            throw new FileReadException("Did not find file inside " + filePath);
        }

        URI foundResourceURI;

        try{

            foundResourceURI = foundResourceURL.toURI();
        }
        catch (URISyntaxException e){

            e.printStackTrace();

            throw new FileReadException(e.getMessage());
        }

        return new File(foundResourceURI);
    }

    public static File getFileFromOutside(String filePath){

        Path foundFilePath = Paths.get(filePath);

        return foundFilePath.toFile();
    }

    public static String loadStrFromFile(File gotFile, Charset charset) throws URISyntaxException{

        try {
            return Files.readString(gotFile.toPath(), charset);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
