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

    public static void handlePdfFileDocument(File pdfFile, Consumer<PDDocument> handlePdDocument) throws FileReadException {

        try (PDDocument document = Loader.loadPDF(pdfFile)){

            handlePdDocument.accept(document);
        }
        catch(IOException e){

            throw new FileReadException(e.getMessage());
        }
    }

    public static PdfLinesDetails getLinesFromFile(File gotFile, boolean shouldGetRealNewLines) throws FileReadException {

        AtomicReference<List<List<Float>>> linesYCords = new AtomicReference<>();
        AtomicReference<List<List<String>>> linesAtomic = new AtomicReference<>();
        AtomicInteger numberOfPagesAtomic = new AtomicInteger(0);

        handlePdfFileDocument(gotFile, pdDocument -> {

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

    public static String[] getLinesInRectFromFile(File gotFile, Integer pageIndex, Rectangle2D.Double rect, boolean shouldGetRealNewLines) throws FileReadException {

        AtomicReference<String> gotTextAtomic = new AtomicReference<>();

        handlePdfFileDocument(gotFile, pdDocument -> {

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

    public static Dimension getFilePageSize(File gotFile, Integer pageIndex) throws FileReadException {

        AtomicReference<Dimension> result = new AtomicReference<>();

        handlePdfFileDocument(gotFile, pdDocument -> {

            PDPage page = pdDocument.getPage(pageIndex);

            int pageWidth = (int) page.getMediaBox().getWidth();
            int pageHeight = (int) page.getMediaBox().getHeight();

            Dimension gotSize = new Dimension(pageWidth, pageHeight);

            result.set(gotSize);
        });

        return result.get();
    }

}
