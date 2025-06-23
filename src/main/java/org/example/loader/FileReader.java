package org.example.loader;

import org.example.exception.FileReadException;
import org.example.loader.pdf.PdfFileReader;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class FileReader {

    public static File getFileFromInside(String filePath) {

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
}
