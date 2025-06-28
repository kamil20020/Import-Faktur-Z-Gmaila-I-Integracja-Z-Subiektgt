package org.example.template;

import org.example.loader.pdf.PdfFileReader;
import org.example.loader.pdf.PdfLinesDetails;
import org.example.template.data.TemplateConverter;
import org.example.template.data.TemplateCreator;
import org.example.template.data.TemplateInvoiceItem;

import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public record Template(

    boolean isTaxOriented,
    TemplateRow place,
    TemplateRow creationDate,
    TemplateRow receiveDate,
    TemplateRow title,
    TemplateRow creator,
    TemplateRow invoiceItems,
    TemplateRow totalPrice
){

    public static boolean hasCompany(byte[] data, String company){

        PdfLinesDetails pdfLinesDetails = getLines(data);

        List<String> lines = pdfLinesDetails.lines().get(0);

        for(String line : lines) {

            line = line
                .toLowerCase();

            if (line.contains(company)) {

                return true;
            }
        }

        return false;
    }

    public TemplateCreator extractCreator(byte[] data){

        String[] lines = getLinesForTemplateRow(data, creator);

        Map<String, Integer> templateRowFieldsSkipSpaceMappings = new HashMap<>();

        creator.fields()
            .forEach(templateRowField -> {

                templateRowFieldsSkipSpaceMappings.put(templateRowField.name(), templateRowField.skipSpace());
            });

        Integer creatorMaxSize = creator.maxSize();

        return TemplateCreator.extract(lines, templateRowFieldsSkipSpaceMappings, creatorMaxSize);
    }

    public LocalDate extractCreationDate(byte[] data){

        String[] lines = getLinesForTemplateRow(data, creationDate);

        return TemplateConverter.tryToParseLocalDate(lines[0].strip());
    }

    public List<TemplateInvoiceItem> extractInvoiceItems(byte[] data) {

        List<TemplateInvoiceItem> templateInvoiceItems = new ArrayList<>();

        PdfLinesDetails pdfLinesDetails = getLines(data);

        int numberOfPages = pdfLinesDetails.numberOfPages();

        for(int pageIndex = 0; pageIndex < numberOfPages; pageIndex++){

            extractInvoiceLinesForPage(data, pdfLinesDetails, pageIndex, templateInvoiceItems);
        }

        return templateInvoiceItems;
    }

    private void extractInvoiceLinesForPage(byte[] data, PdfLinesDetails pdfLinesDetails, int pageIndex, List<TemplateInvoiceItem> templateInvoiceItems){

        List<String> gotLines = pdfLinesDetails.lines().get(pageIndex);
        List<Float> linesYCords = pdfLinesDetails.linesYCords().get(pageIndex);

        int invoiceItemsStartIndex = getLineStartIndex(gotLines, invoiceItems.startStr(), true);

        for(int i = invoiceItemsStartIndex; i < gotLines.size(); i++){

            String line = gotLines.get(i).stripLeading();

            if(line.startsWith(invoiceItems.endStr())){
                break;
            }

            String[] words = line.split("\\s");

            if(!TemplateInvoiceItem.isRowValid(words, invoiceItems.minLength())){
                continue;
            }

            double lineYCord = linesYCords.get(i) + invoiceItems.startOffset();

            TemplateInvoiceItem templateInvoiceItem = extractTemplateInvoiceItem(data, pageIndex, lineYCord);

            templateInvoiceItems.add(templateInvoiceItem);
        }
    }

    private int getLineStartIndex(List<String> gotLines, String searchText, boolean shouldAppendOne){

        int invoiceItemsStartIndex = 0;

        for (String line : gotLines){

            if(line.startsWith(searchText)){
                break;
            }

            invoiceItemsStartIndex++;
        }

        if(shouldAppendOne){

            invoiceItemsStartIndex++;
        }

        return invoiceItemsStartIndex;
    }

    private TemplateInvoiceItem extractTemplateInvoiceItem(byte[] data, int pageIndex, double lineYCord){

        Map<String, String> gotValues = new HashMap<>();

        for(TemplateRowField templateRowField : invoiceItems.fields()){

            String fieldName = templateRowField.name();

            String value = extractWordFromLine(data, pageIndex, lineYCord, templateRowField);

            gotValues.put(fieldName, value);
        }

        return TemplateInvoiceItem.extract(gotValues);
    }

    public String extractWordFromLine(byte[] data, int pageIndex, double lineYCord, TemplateRowField templateRowField){

        float minXCord = templateRowField.xMinCord();
        float maxXCord = templateRowField.xMaxCord();

        float width = maxXCord - minXCord;

        String[] lines = extractWordsForField(data, pageIndex, minXCord, lineYCord - 1, width, invoiceItems.rowHeight());

        if(lines.length == 0){

            return "";
        }

        return lines[0];
    }

    private static String[] extractWordsForField(byte[] data, int pageIndex, double x, double yPt, double width, double height){

        Rectangle2D.Double rect = new Rectangle2D.Double(
            TemplateCords.convertPxToPt(x),
            yPt,
            TemplateCords.convertPxToPt(width),
            TemplateCords.convertPxToPt(height)
        );

        return PdfFileReader.getLinesInRectFromFile(data, pageIndex, rect, true);
    }

    public LocalDate extractReceiveDate(byte[] data){

        String[] lines = getLinesForTemplateRow(data, receiveDate);

        return TemplateConverter.tryToParseLocalDate(lines[0].strip());
    }

    public String extractTitle(byte[] data){

        String[] lines = getLinesForTemplateRow(data, title);

        return lines[0].stripIndent();
    }

    public String extractPlace(byte[] data){

        String[] lines = getLinesForTemplateRow(data, place);

        return lines[0].stripIndent();
    }

    public BigDecimal extractTotalPrice(byte[] data){

        String totalPriceSearchStr = totalPrice.startStr();

        PdfLinesDetails pdfLinesDetails = getLines(data);

        int numberOfPages = pdfLinesDetails.numberOfPages();

        int lastPageIndex = numberOfPages - 1;

        List<String> lines = pdfLinesDetails.lines().get(lastPageIndex);

        int totalPriceLineIndex = getLineStartIndex(lines, totalPriceSearchStr, false);

        String gotLine = lines.get(totalPriceLineIndex);

        String gotRawTotalPrice = gotLine.split(totalPriceSearchStr)[1];

        return TemplateConverter.convertToBigDecimal(gotRawTotalPrice);
    }

    private static String[] getLinesForTemplateRow(byte[] data, TemplateRow templateRow){

        if(templateRow == null){

            return new String[0];
        }

        Rectangle2D.Double rect = templateRow.coords().getRect();

        String[] gotLines = PdfFileReader.getLinesInRectFromFile(data, 0, rect, true);

        return gotLines;
    }

    private static PdfLinesDetails getLines(byte[] data){

        PdfLinesDetails pdfLinesDetails = PdfFileReader.getLinesFromFile(data, true);

        return pdfLinesDetails;
    }

}
