package org.example.template;

import org.example.loader.pdf.PdfFileReader;
import org.example.loader.pdf.PdfLinesDetails;
import org.example.template.data.TemplateBasicData;
import org.example.template.data.TemplateConverter;
import org.example.template.data.TemplateCreator;
import org.example.template.data.TemplateInvoiceItem;
import org.example.template.row.HeightTemplateRow;
import org.example.template.row.TemplateRow;
import org.example.template.field.AreaTemplateRowField;
import org.example.template.field.HorizontalTemplateRowField;

import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public record Template(

    boolean isTaxOriented,
    TemplateRow basicInfo,
    TemplateRow creator,
    HeightTemplateRow invoiceItems,
    HeightTemplateRow totalPrice,
    HeightTemplateRow payDate
){

    public static Optional<String> searchInFile(byte[] data, Collection<String> values){

        return PdfFileReader.search(data, values, true);
    }

    public static Optional<String> searchInContent(String content, Collection<String> values){

        if(content == null || content.isEmpty() || values == null || values.isEmpty()){

            return Optional.empty();
        }

        String[] lines = content.split("\\n");

        if(lines.length == 0){

            return Optional.empty();
        }

        for(String line : lines) {

            line = line
                .toLowerCase();

            for(String value : values){

                if(line.contains(value)){

                    return Optional.of(value);
                }
            }
        }

        return Optional.empty();
    }

    public TemplateCreator extractCreator(byte[] data){

        Map<String, String> values = getValuesFromTemplateRow(data, creator, 0);

        return TemplateCreator.extract(values);
    }

    public TemplateBasicData extractBasicData(byte[] data){

        Map<String, String> values = getValuesFromTemplateRow(data, basicInfo, 0);

        return TemplateBasicData.extract(values);
    }

    public List<TemplateInvoiceItem> extractInvoiceItems(byte[] data) {

        List<TemplateInvoiceItem> templateInvoiceItems = new ArrayList<>();

        PdfLinesDetails pdfLinesDetails = getLines(data);

        int numberOfPages = pdfLinesDetails.numberOfPages();

        for(int pageIndex = 0; pageIndex < numberOfPages; pageIndex++){

            extractInvoiceLinesForPage(data, pageIndex, templateInvoiceItems);
        }

        return templateInvoiceItems;
    }

    private void extractInvoiceLinesForPage(byte[] data, int pageIndex, List<TemplateInvoiceItem> templateInvoiceItems){

        if(doesInvoiceItemsHaveIndices()){

            extractTemplateInvoiceItemsWithIndices(data, pageIndex, templateInvoiceItems);
        }
        else{

            extractTemplateInvoiceItemsWithoutIndices(data, pageIndex, templateInvoiceItems);
        }
    }

    private boolean doesInvoiceItemsHaveIndices(){

        return invoiceItems.getFields().stream()
            .anyMatch(field -> Objects.equals(field.getName(), "index"));
    }

    private void extractTemplateInvoiceItemsWithIndices(byte[] data, int pageIndex, List<TemplateInvoiceItem> templateInvoiceItems) {

        List<Map<String, String>> gotValues = getValuesFromHeightTemplateRow(data, invoiceItems, pageIndex);

        int actualTemplateInvoiceIndex = 0;

        TemplateInvoiceItem templateInvoiceItem = null;

        for(Map<String, String> gotValuesRow : gotValues){

            if(!gotValuesRow.containsKey("index")){
                continue;
            }

            String gotRawIndex = gotValuesRow.get("index");

            Integer gotIndex = TemplateConverter.convertToInteger(gotRawIndex);

            if(gotIndex != null){

                if(gotIndex > actualTemplateInvoiceIndex){

                    templateInvoiceItem = TemplateInvoiceItem.extract(gotValuesRow);

                    templateInvoiceItems.add(templateInvoiceItem);

                    actualTemplateInvoiceIndex++;

                    continue;
                }
            }

            if(templateInvoiceItem == null){
                continue;
            }


            TemplateInvoiceItem newTemplateInvoiceItemData = TemplateInvoiceItem.extract(gotValuesRow);

            templateInvoiceItem.appendData(newTemplateInvoiceItemData);
        }
    }

    private void extractTemplateInvoiceItemsWithoutIndices(byte[] data, int pageIndex, List<TemplateInvoiceItem> templateInvoiceItems){

        List<Map<String, String>> gotValues = getValuesFromHeightTemplateRow(data, invoiceItems, pageIndex);

        TemplateInvoiceItem templateInvoiceItem = new TemplateInvoiceItem();

        for(Map<String, String> gotValuesRow : gotValues){

            TemplateInvoiceItem newTemplateInvoiceItem = TemplateInvoiceItem.extract(gotValuesRow);

            templateInvoiceItem.appendData(newTemplateInvoiceItem);

            if(templateInvoiceItem.hasAllValuesWithoutCode()) {

                templateInvoiceItems.add(templateInvoiceItem);

                templateInvoiceItem = new TemplateInvoiceItem();

                if(templateInvoiceItem.getCode() == null || templateInvoiceItem.getCode().isEmpty()){

                    String templateName = templateInvoiceItem.getName();

                    templateInvoiceItem.setCode(templateName);
                }
            }
        }
    }

    public BigDecimal extractTotalPrice(byte[] data){

        Map<String, String> gotValues = extractValueFromEnd(data, totalPrice);

        if(gotValues == null || gotValues.isEmpty()){

            return null;
        }

        String gotRawPrice = gotValues.get("value");

        return TemplateConverter.convertToBigDecimal(gotRawPrice);
    }

    public LocalDate extractPayDate(byte[] data){

        Map<String, String> gotValues = extractValueFromEnd(data, payDate);

        if(gotValues == null || gotValues.isEmpty()){

            return null;
        }

        String gotRawPrice = gotValues.get("value");

        return TemplateConverter.tryToParseLocalDate(gotRawPrice);
    }

    private Map<String, String> extractValueFromEnd(byte[] data, HeightTemplateRow heightTemplateRow){

        PdfLinesDetails pdfLinesDetails = getLines(data);

        int numberOfPages = pdfLinesDetails.numberOfPages();

        int lastPageIndex = numberOfPages - 1;

        List<Map<String, String>> gotValuesList = getValuesFromHeightTemplateRow(data, heightTemplateRow, lastPageIndex);

        if(gotValuesList == null || gotValuesList.isEmpty()){

            return null;
        }

        return gotValuesList.get(0);
    }

    private static Map<String, String> getValuesFromTemplateRow(byte[] data, TemplateRow templateRow, int pageIndex) throws IllegalStateException{

       return templateRow.getValues((templateRowField) -> getLinesFromTemplateRowField(data, templateRowField, pageIndex));
    }

    private static List<Map<String, String>> getValuesFromHeightTemplateRow(byte[] data, HeightTemplateRow heightTemplateRow, int pageIndex) throws IllegalStateException{

        PdfLinesDetails gotLinesDetails = getLines(data);

        List<String> gotLines = gotLinesDetails.lines().get(pageIndex);
        List<Float> yCoords = gotLinesDetails.linesYCords().get(pageIndex);

        return heightTemplateRow.getValues(
            gotLines,
            yCoords,
            (templateRowField, minY, maxY) -> getLinesFromTemplateRowField(data, templateRowField, minY, maxY, pageIndex)
        );
    }

    private static String[] getLinesFromTemplateRowField(byte[] data, HorizontalTemplateRowField horizontalTemplateRowField, double yMin, double yMax, int pageIndex) throws IllegalStateException{

        if(horizontalTemplateRowField == null){

            return new String[0];
        }

        Rectangle2D.Double rect = horizontalTemplateRowField.getRect(yMin, yMax);

        return getLinesFromRect(data, rect, pageIndex);
    }

    private static String[] getLinesFromTemplateRowField(byte[] data, AreaTemplateRowField areaTemplateRowField, int pageIndex) throws IllegalStateException{

        if(areaTemplateRowField == null){

            return new String[0];
        }

        Rectangle2D.Double rect = areaTemplateRowField.getRect();

        return getLinesFromRect(data, rect, pageIndex);
    }

    private static String[] getLinesFromRect(byte[] data, Rectangle2D.Double rect, int pageIndex){

        return PdfFileReader.getLinesInRectFromFile(data, pageIndex, rect, true);
    }

    private static PdfLinesDetails getLines(byte[] data){

        PdfLinesDetails pdfLinesDetails = PdfFileReader.getLinesFromFile(data, true);

        return pdfLinesDetails;
    }

}
