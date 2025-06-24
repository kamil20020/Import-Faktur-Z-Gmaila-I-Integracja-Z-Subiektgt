package org.example.gui.manage_pdf;

import org.apache.pdfbox.rendering.PDFRenderer;
import org.example.exception.FileReadException;
import org.example.loader.pdf.PdfFileReader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PdfFileRenderer {

    public static PdfFileDetails getDetails(File pdfFile, int pageIndex){

        AtomicReference<BufferedImage> image = new AtomicReference<>();

        AtomicInteger numberOfPages = new AtomicInteger();

        PdfFileReader.handlePdfFileDocument(pdfFile, pdDocument -> {

            PDFRenderer renderer = new PDFRenderer(pdDocument);

            try {
                image.set(renderer.renderImageWithDPI(pageIndex, 150));

                numberOfPages.set(pdDocument.getPages().getCount());
            }
            catch (IOException e) {

                e.printStackTrace();

                throw new FileReadException(e.getMessage());
            }
        });

        PdfFileDetails pdfFileDetails = new PdfFileDetails(
            image.get(),
            numberOfPages.get()
        );

        return pdfFileDetails;
    }
}
