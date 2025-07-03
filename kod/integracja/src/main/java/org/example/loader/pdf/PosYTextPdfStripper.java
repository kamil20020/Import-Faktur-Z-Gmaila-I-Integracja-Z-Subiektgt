package org.example.loader.pdf;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PosYTextPdfStripper extends PDFTextStripper {

    private final List<List<Float>> linesYCords = new ArrayList<>();
    private final List<List<String>> lines = new ArrayList<>();

    private int currentPage = -1;

    @Override
    protected void startPage(PDPage pdPage) throws IOException {

        super.startPage(pdPage);

        linesYCords.add(new ArrayList<>());
        lines.add(new ArrayList<>());

        currentPage++;
    }

    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {

        TextPosition firstTextPosition = textPositions.get(0);

        List<Float> lineYCordsForPage = linesYCords.get(currentPage);
        List<String> linesForPage = lines.get(currentPage);

        if(lineYCordsForPage.size() != 0){

            int lastIndex = lineYCordsForPage.size() - 1;
            float lastYCord = lineYCordsForPage.get(lastIndex);

            float actualYCord = firstTextPosition.getY() - firstTextPosition.getHeight();

            if(actualYCord - lastYCord > 1){

                lineYCordsForPage.add(firstTextPosition.getY() - firstTextPosition.getHeight());

                linesForPage.add(text);
            }
            else{

                linesForPage.set(lastIndex, linesForPage.get(lastIndex) + " " + text);
            }
        }
        else{

            lineYCordsForPage.add(firstTextPosition.getY());

            linesForPage.add(text);
        }

        super.writeString(text, textPositions);
    }

    public List<List<Float>> getLinesYCords(){

        return linesYCords;
    }

    public List<List<String>> getLines(){

        return lines;
    }

}
