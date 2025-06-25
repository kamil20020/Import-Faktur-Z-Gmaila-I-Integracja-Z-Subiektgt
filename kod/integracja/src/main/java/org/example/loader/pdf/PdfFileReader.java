package org.example.loader.pdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.example.exception.FileReadException;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class PdfFileReader {

    public static void handlePdfFileDocument(File gotFile, Consumer<PDDocument> handlePdDocument) throws FileReadException {

        try (PDDocument document = Loader.loadPDF(gotFile)){

            handlePdDocument.accept(document);
        }
        catch(IOException e){

            throw new FileReadException(e.getMessage());
        }
    }

    public static void handlePdfFileDocument(byte[] data, Consumer<PDDocument> handlePdDocument) throws FileReadException {

        try (PDDocument document = Loader.loadPDF(data)){

            handlePdDocument.accept(document);
        }
        catch(IOException e){

            throw new FileReadException(e.getMessage());
        }
    }

    public static PdfLinesDetails getLinesFromFile(byte[] data, boolean shouldGetRealNewLines) throws FileReadException {

        AtomicReference<List<List<Float>>> linesYCords = new AtomicReference<>();
        AtomicReference<List<List<String>>> linesAtomic = new AtomicReference<>();
        AtomicInteger numberOfPagesAtomic = new AtomicInteger(0);

        handlePdfFileDocument(data, pdDocument -> {

            PosYTextPdfStripper textStripper = new PosYTextPdfStripper();

            textStripper.setSortByPosition(shouldGetRealNewLines);
            textStripper.setStartPage(1);
            textStripper.setEndPage(pdDocument.getNumberOfPages());

            try {

                textStripper.getText(pdDocument);

                linesYCords.set(textStripper.getLinesYCords());
                linesAtomic.set(textStripper.getLines());
                numberOfPagesAtomic.set(pdDocument.getNumberOfPages());
            }
            catch (IOException e) {

                e.printStackTrace();

                throw new FileReadException(e.getMessage());
            }
        });

        return new PdfLinesDetails(
            linesAtomic.get(),
            linesYCords.get(),
            numberOfPagesAtomic.get()
        );
    }

    public static String[] getLinesInRectFromFile(byte[] data, Integer pageIndex, Rectangle2D.Double rect, boolean shouldGetRealNewLines) throws FileReadException {

        AtomicReference<String> gotTextAtomic = new AtomicReference<>();

        handlePdfFileDocument(data, pdDocument -> {

            PDPage page = pdDocument.getPage(pageIndex);

            String gotText;

            PDFTextStripperByArea stripper;

            try {
                stripper = new PDFTextStripperByArea();

                stripper.setSortByPosition(shouldGetRealNewLines);
                stripper.addRegion("targetRegion", rect);
                stripper.extractRegions(page);

                gotText = stripper.getTextForRegion("targetRegion");
            }
            catch (IOException e) {

                e.printStackTrace();

                throw new FileReadException(e.getMessage());
            }

            gotTextAtomic.set(gotText);
        });

        return gotTextAtomic.get().split("\\r?\\n");
    }

}
