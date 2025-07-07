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
import java.util.stream.Collectors;

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

    public static Optional<String> searchInFile(byte[] data, Collection<String> values){

        return PdfFileReader.search(data, values, true);
    }

    public static Optional<String> searchInContent(String content, Collection<String> values){

        if(content == null || content.isEmpty()){

            return Optional.empty();
        }

        String[] lines = content.split("\\s");

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

        if(creator.isHidden()){

            return extractCreatorForHiddenTextValues();
        }

        return extractCreatorForTextValues(data);
    }

    private TemplateCreator extractCreatorForTextValues(byte[] data){

        Map<String, String> templateRowFieldsValuesMappings = new HashMap<>();

        creator.fields()
            .forEach(templateRowField -> {

                float minX = templateRowField.xMinCord();
                float minY = templateRowField.yMinCord();
                float width = templateRowField.xMaxCord() - minX;
                float height = templateRowField.yMaxCord() - minY;

                String[] gotValues = extractWordsForField(data, 0, minX, minY, width, height);

                String gotValue = String.join(" ", gotValues);

                templateRowFieldsValuesMappings.put(templateRowField.name(), gotValue);
            });

        return TemplateCreator.extract(templateRowFieldsValuesMappings);
    }

    private TemplateCreator extractCreatorForHiddenTextValues(){

        Map<String, String> templateRowFieldsValuesMappings = new HashMap<>();

        creator.fields()
            .forEach(templateRowField -> {

                templateRowFieldsValuesMappings.put(templateRowField.name(), templateRowField.defaultValue());
        });

        return TemplateCreator.extract(templateRowFieldsValuesMappings);
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

        int skipStart = invoiceItems.skipStart() != null ? invoiceItems.skipStart() : 0;

        int invoiceItemsStartIndex = getLineStartIndex(gotLines, invoiceItems.startStr(), true) + skipStart;

        if(doesInvoiceItemsHaveIndices()){

            extractTemplateInvoiceItemsWithIndices(data, pageIndex, gotLines, linesYCords, invoiceItemsStartIndex, templateInvoiceItems);
        }
        else{

            extractTemplateInvoiceItemsWithoutIndices(data, pageIndex, gotLines, linesYCords, invoiceItemsStartIndex, templateInvoiceItems);
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

    private boolean doesInvoiceItemsHaveIndices(){

        return invoiceItems.fields().stream()
            .anyMatch(field -> Objects.equals(field.name(), "index"));
    }

    private void extractTemplateInvoiceItemsWithIndices(byte[] data, int pageIndex, List<String> gotLines, List<Float> linesYCords, int invoiceItemsStartIndex, List<TemplateInvoiceItem> templateInvoiceItems){

        int actualTemplateInvoiceIndex = 0;

        TemplateInvoiceItem templateInvoiceItem = null;

        for(int i = invoiceItemsStartIndex; i < gotLines.size(); i++){

            String line = gotLines.get(i).stripLeading();

            if(line.startsWith(invoiceItems.endStr())){
                return;
            }

            double lineYCord = linesYCords.get(i);

            Optional<Integer> gotIndexOpt = getTemplateInvoiceItemIndex(data, pageIndex, lineYCord);

            if(gotIndexOpt.isPresent()){

                Integer gotIndex = gotIndexOpt.get();

                if(gotIndex > actualTemplateInvoiceIndex){

                    templateInvoiceItem = extractTemplateInvoiceItemValues(data, pageIndex, lineYCord);

                    templateInvoiceItems.add(templateInvoiceItem);

                    actualTemplateInvoiceIndex++;

                    continue;
                }
            }

            if(templateInvoiceItem == null){
                continue;
            }

            TemplateInvoiceItem newTemplateInvoiceItemData = extractTemplateInvoiceItemValues(data, pageIndex, lineYCord);

            appendDataToTemplateInvoiceItem(templateInvoiceItem, newTemplateInvoiceItemData);
        }
    }

    private void extractTemplateInvoiceItemsWithoutIndices(byte[] data, int pageIndex, List<String> gotLines, List<Float> linesYCords, int invoiceItemsStartIndex, List<TemplateInvoiceItem> templateInvoiceItems){

        TemplateInvoiceItem templateInvoiceItem = new TemplateInvoiceItem();

        for(int i = invoiceItemsStartIndex; i < gotLines.size(); i++){

            String line = gotLines.get(i).stripLeading();

            if(line.startsWith(invoiceItems.endStr())){
                return;
            }

            double lineYCord = linesYCords.get(i);

            TemplateInvoiceItem newTemplateInvoiceItem = extractTemplateInvoiceItemValues(data, pageIndex, lineYCord);

            appendDataToTemplateInvoiceItem(templateInvoiceItem, newTemplateInvoiceItem);

            if(templateInvoiceItem.hasAllValues()) {

                templateInvoiceItems.add(templateInvoiceItem);

                templateInvoiceItem = new TemplateInvoiceItem();
            }
        }
    }

    private Optional<Integer> getTemplateInvoiceItemIndex(byte[] data, int pageIndex, double lineYCord){

        TemplateRowField firstTemplateRowField = invoiceItems.fields().get(0);

        String value = extractWordFromLine(data, pageIndex, lineYCord, firstTemplateRowField);

        if(value == null || value.isEmpty()){

            return Optional.empty();
        }

        value = value.stripIndent();

        try{

            Integer gotIndex = Integer.valueOf(value);

            return Optional.of(gotIndex);
        }
        catch (NumberFormatException e){

            e.printStackTrace();
        }

        return Optional.empty();
    }

    private void appendDataToTemplateInvoiceItem(TemplateInvoiceItem destTemplateInvoiceItem, TemplateInvoiceItem newTemplateInvoiceItemData){

        if(!newTemplateInvoiceItemData.getName().isEmpty()){

            String actualName = destTemplateInvoiceItem.getName();

            String separator = actualName.isEmpty() ? "" : " ";

            destTemplateInvoiceItem.setName(actualName + separator + newTemplateInvoiceItemData.getName());
        }

        if(destTemplateInvoiceItem.getCode().isEmpty()){

            destTemplateInvoiceItem.setCode(newTemplateInvoiceItemData.getCode());
        }

        if(newTemplateInvoiceItemData.getPrice() != null){

            destTemplateInvoiceItem.setPrice(newTemplateInvoiceItemData.getPrice());
        }

        if(newTemplateInvoiceItemData.getQuantity() != null){

            destTemplateInvoiceItem.setQuantity(newTemplateInvoiceItemData.getQuantity());
        }

        if(newTemplateInvoiceItemData.getTax() != null){

            destTemplateInvoiceItem.setTax(newTemplateInvoiceItemData.getTax());
        }
    }

    private TemplateInvoiceItem extractTemplateInvoiceItemValues(byte[] data, int pageIndex, double lineYCord){

        Map<String, String> gotValues = new HashMap<>();

        for(TemplateRowField templateRowField : invoiceItems.fields()){

            String fieldName = templateRowField.name();

            String value = extractWordFromLine(data, pageIndex, lineYCord, templateRowField);

            String separator = templateRowField.separator();

            if(value != null && separator != null){

                String[] words = value.split(separator);

                if(words.length >= 2){

                    int index = templateRowField.index();

                    value = words[index];
                }
            }

            gotValues.put(fieldName, value);
        }

        return TemplateInvoiceItem.extract(gotValues);
    }

    public String extractWordFromLine(byte[] data, int pageIndex, double lineYCord, TemplateRowField templateRowField){

        float minXCord = templateRowField.xMinCord();
        float maxXCord = templateRowField.xMaxCord();

        float width = maxXCord - minXCord;

        String[] lines = extractWordsForFieldWithConvertedY(data, pageIndex, minXCord, lineYCord, width, invoiceItems.rowHeight());

        if(lines.length == 0){

            return "";
        }

        return lines[0];
    }

    private static String[] extractWordsForField(byte[] data, int pageIndex, double x, double y, double width, double height){

        double convertedY = TemplateCords.convertPxToPt(y);

        return extractWordsForFieldWithConvertedY(data, pageIndex, x, convertedY, width, height);
    }

    private static String[] extractWordsForFieldWithConvertedY(byte[] data, int pageIndex, double x, double yPt, double width, double height){

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

        if(place.isHidden()){

            return place.defaultValue();
        }

        String[] lines = getLinesForTemplateRow(data, place);

        return lines[0].stripIndent();
    }

    public BigDecimal extractTotalPrice(byte[] data){

        String totalPriceSearchStr = totalPrice.startStr();

        PdfLinesDetails pdfLinesDetails = getLines(data);

        int numberOfPages = pdfLinesDetails.numberOfPages();

        int lastPageIndex = numberOfPages - 1;

        List<String> lines = pdfLinesDetails.lines().get(lastPageIndex);
        List<Float> linesYCords = pdfLinesDetails.linesYCords().get(lastPageIndex);

        int totalPriceLineIndex = getLineStartIndex(lines, totalPriceSearchStr, false);

        if(totalPrice.skipLines() != null){

            totalPriceLineIndex += totalPrice.skipLines();
        }

        TemplateRectCords cords = totalPrice.coords();

        double minX = cords.leftTop().x();
        double maxX = cords.rightDown().x();

        double width = maxX - minX;

        double lineHeight = totalPrice.rowHeight();
        double lineHeightConverted = TemplateCords.convertPxToPt(lineHeight);

        double minY = linesYCords.get(totalPriceLineIndex);

        String[] gotValues = extractWordsForFieldWithConvertedY(data, lastPageIndex, minX,  minY, width, lineHeightConverted);

        if(gotValues == null || gotValues.length == 0){

            return null;
        }

        String rawPrice = String.join("", gotValues);

        return TemplateConverter.convertToBigDecimal(rawPrice);
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
