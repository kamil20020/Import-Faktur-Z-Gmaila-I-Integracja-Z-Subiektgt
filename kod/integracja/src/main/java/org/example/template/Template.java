package org.example.template;

import org.example.loader.JsonFileLoader;
import org.example.loader.pdf.PdfFileReader;
import org.example.loader.pdf.PdfLinesDetails;
import org.example.template.data.TemplateCombinedData;
import org.example.template.data.TemplateCreator;
import org.example.template.data.TemplateInvoiceItem;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public record Template(

    boolean isTaxOriented,
    TemplateRow place,
    TemplateRow creationDate,
    TemplateRow receiveDate,
    TemplateRow title,
    TemplateRow creator,
    TemplateRow invoiceItems
){

    private static final List<String> toCheckDateFormats;

    static {

        toCheckDateFormats = List.of(
            "yyy-MM-dd",
            "yyy.MM.dd",
            "yyy/MM/dd",
            "dd-MM-yyyy",
            "dd.MM.yyyy",
            "dd/MM/yyyy"
        );
    }

    public static boolean hasCompany(File gotFile, String company){

        PdfLinesDetails pdfLinesDetails = getLines(gotFile);

        List<String> lines = pdfLinesDetails.lines().get(0);

        for(String line : lines) {

            if (line.contains(company)) {

                return true;
            }
        }

        return false;
    }

    public TemplateCreator extractCreator(File gotFile){

        String[] lines = getLinesForTemplateRow(gotFile, creator);

        Map<String, Integer> templateRowFieldsSkipSpaceMappings = new HashMap<>();

        creator.fields()
            .forEach(templateRowField -> {

                templateRowFieldsSkipSpaceMappings.put(templateRowField.name(), templateRowField.skipSpace());
            });

        Integer nameSkipSpace = templateRowFieldsSkipSpaceMappings.get("name");
        Integer streetSkipSpace = templateRowFieldsSkipSpaceMappings.get("street");
        Integer citySkipSpace = templateRowFieldsSkipSpaceMappings.get("city");
        Integer nipSkipSpace = templateRowFieldsSkipSpaceMappings.get("nip");

        String name = null;

        int nextIndex = 1;

        if(lines.length == creator().maxSize()){

            name = lines[0] + lines[1];

            nextIndex = 2;
        }
        else{

            name = lines[0];
        }

        name = name
            .stripIndent();

        String street = lines[nextIndex]
            .stripIndent()
            .replaceAll("ul. ", "");

        String city = lines[nextIndex + 1]
            .stripIndent();

        if(nipSkipSpace != null){

            nextIndex += nipSkipSpace;
        }

        String rawNip = lines[nextIndex + 2];

        rawNip = rawNip
            .strip()
            .replaceAll("NIP: ", "")
            .replaceAll("PL", "");

        return new TemplateCreator(
            name,
            street,
            city,
            rawNip
        );
    }

    public LocalDate extractCreationDate(File gotFile){

        String[] lines = getLinesForTemplateRow(gotFile, creationDate);

        return extractGeneralDate(lines[0].strip());
    }

    public static LocalDate extractGeneralDate(String input){

        LocalDate gotDate = null;

        for (String format : toCheckDateFormats){

            gotDate = tryToParseLocalDate(format, input);

            if(gotDate != null){

                break;
            }
        }

        return gotDate;
    }

    private static LocalDate tryToParseLocalDate(String format, String input) throws DateTimeParseException {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);

        try{

            return LocalDate.parse(input, dateTimeFormatter);
        }
        catch (DateTimeParseException e){

            //:D
        }

        return null;
    }

    public List<TemplateInvoiceItem> extractInvoiceItems(File gotFile) {

        List<TemplateInvoiceItem> templateInvoiceItems = new ArrayList<>();

        PdfLinesDetails pdfLinesDetails = getLines(gotFile);

        int numberOfPages = pdfLinesDetails.numberOfPages();

        for(int pageIndex = 0; pageIndex < numberOfPages; pageIndex++){

            extractInvoiceLinesForPage(gotFile, pdfLinesDetails, pageIndex, templateInvoiceItems);
        }

        return templateInvoiceItems;
    }

    private void extractInvoiceLinesForPage(File gotFile, PdfLinesDetails pdfLinesDetails, int pageIndex, List<TemplateInvoiceItem> templateInvoiceItems){

        List<String> gotLines = pdfLinesDetails.lines().get(pageIndex);
        List<Float> linesYCords = pdfLinesDetails.linesYCords().get(pageIndex);

        int invoiceItemsStartIndex = getInvoiceLinesStartIndex(gotLines);

        for(int i = invoiceItemsStartIndex; i < gotLines.size(); i++){

            String line = gotLines.get(i).stripLeading();

            if(line.startsWith(invoiceItems.endStr())){
                break;
            }

            String[] words = line.split("\\s");

            if(!isRowValidInvoiceLine(words)){
                continue;
            }

            double lineYCord = linesYCords.get(i) + invoiceItems.startOffset();

            TemplateInvoiceItem templateInvoiceItem = extractTemplateInvoiceItem(gotFile, pageIndex, lineYCord);

            templateInvoiceItems.add(templateInvoiceItem);
        }
    }

    private int getInvoiceLinesStartIndex(List<String> gotLines){

        int invoiceItemsStartIndex = 0;

        for (String line : gotLines){

            if(line.startsWith(invoiceItems.startStr())){
                break;
            }

            invoiceItemsStartIndex++;
        }

        invoiceItemsStartIndex++;

        return invoiceItemsStartIndex;
    }

    private boolean isRowValidInvoiceLine(String[] words){

        if(words.length < invoiceItems.minLength()){

            return false;
        }

        try{

            Integer.valueOf(words[0]);
        }
        catch(NumberFormatException e){

            return false;
        }

        return true;
    }

    private TemplateInvoiceItem extractTemplateInvoiceItem(File gotFile, int pageIndex, double lineYCord){

        Map<String, String> gotValues = new HashMap<>();

        for(TemplateRowField templateRowField : invoiceItems.fields()){

            String fieldName = templateRowField.name();

            String value = extractWordFromLine(gotFile, pageIndex, lineYCord, templateRowField);

            gotValues.put(fieldName, value);
        }

        String name = gotValues.get("name").stripIndent();
        String code = gotValues.get("code").stripIndent();
        BigDecimal price = convertToBigDecimal(gotValues.get("price"));
        Integer quantity = convertToInteger(gotValues.get("quantity"));
        BigDecimal tax = convertToBigDecimal(gotValues.get("tax"));

        return TemplateInvoiceItem.builder()
            .name(name)
            .code(code)
            .price(price)
            .quantity(quantity)
            .tax(tax)
            .build();
    }

    private String extractWordFromLine(File gotFile, int pageIndex, double lineYCord, TemplateRowField templateRowField){

        float minXCord = templateRowField.xMinCord();
        float maxXCord = templateRowField.xMaxCord();

        float width = maxXCord - minXCord;

        String[] lines = extractWordsForField(gotFile, pageIndex, minXCord, lineYCord - 1, width, invoiceItems.rowHeight());

        if(lines.length == 0){

            return "";
        }

        return lines[0];
    }

    private static String[] extractWordsForField(File gotFile, int pageIndex, double x, double yPt, double width, double height){

        Rectangle2D.Double rect = new Rectangle2D.Double(
            TemplateCords.convertPxToPt(x),
            yPt,
            TemplateCords.convertPxToPt(width),
            TemplateCords.convertPxToPt(height)
        );

        return PdfFileReader.getLinesInRectFromFile(gotFile, pageIndex, rect, true);
    }

    private Integer convertToInteger(String value){

        value = value.replaceAll(",", "\\.")
            .replaceAll("\\s", "")
            .split("\\.")[0];

        return Integer.valueOf(value);
    }

    private BigDecimal convertToBigDecimal(String value){

        value = value
            .replaceAll("\\s", "")
            .replaceAll(",", "\\.")
            .replaceAll("%", "")
            .replaceAll("PLN", "");

        return new BigDecimal(value);
    }

    public LocalDate extractReceiveDate(File gotFile){

        String[] lines = getLinesForTemplateRow(gotFile, receiveDate);

        return extractGeneralDate(lines[0].strip());
    }

    public String extractTitle(File gotFile){

        String[] lines = getLinesForTemplateRow(gotFile, title);

        return lines[0].stripIndent();
    }

    public String extractPlace(File gotFile){

        String[] lines = getLinesForTemplateRow(gotFile, place);

        return lines[0].stripIndent();
    }

    private static String[] getLinesForTemplateRow(File gotFile, TemplateRow templateRow){

        if(templateRow == null){

            return new String[0];
        }

        Rectangle2D.Double rect = templateRow.coords().getRect();

        String[] gotLines = PdfFileReader.getLinesInRectFromFile(gotFile, 0, rect, true);

        return gotLines;
    }

    private static PdfLinesDetails getLines(File gotFile){

        PdfLinesDetails pdfLinesDetails = PdfFileReader.getLinesFromFile(gotFile,true);

        return pdfLinesDetails;
    }

}
