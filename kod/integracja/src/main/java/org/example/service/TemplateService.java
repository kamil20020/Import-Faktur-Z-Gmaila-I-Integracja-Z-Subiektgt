package org.example.service;

import org.example.exception.FileReadException;
import org.example.loader.FileReader;
import org.example.loader.JsonFileLoader;
import org.example.template.Template;
import org.example.template.data.TemplateCombinedData;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class TemplateService {

    private static final Map<String, Template> templateSchemas = new HashMap<>();

    private static final String TEMPLATES_SCHEMA_PATH = "schemas/";

    static {

        loadTemplatesSchemas();
    }

    private static void loadTemplatesSchemas() throws FileReadException {

        File templatesSchemasLocation = FileReader.getFileFromInside(TEMPLATES_SCHEMA_PATH);

        File[] schemaFiles = templatesSchemasLocation.listFiles();

        if(schemaFiles == null){

            throw new FileReadException("There are no templates schemas files");
        }

        for(File schemaFile : schemaFiles){

            String schemaFileName = schemaFile.getName()
                .replaceAll("\\.pdf", "");

            Template template = loadTemplate(schemaFile);

            templateSchemas.put(schemaFileName, template);
        }
    }

    public static Template loadTemplate(File file) throws FileReadException {

        return JsonFileLoader.loadStrFromFile(file, Template.class);
    }

    public TemplateCombinedData applyTemplate(String templateFilePath, byte[] data) {

        Template template = loadTemplate(templateFilePath);

        return applyTemplate(template, data);
    }

    public TemplateCombinedData applyTemplate(Template template, byte[] data) {

        return new TemplateCombinedData(
            template.extractPlace(data),
            template.extractCreationDate(data),
            template.extractReceiveDate(data),
            template.extractTitle(data),
            template.extractCreator(data),
            template.extractInvoiceItems(data),
            template.isTaxOriented(),
            template.extractTotalPrice(data)
        );
    }

    public Template loadTemplate(String templateFilePath) throws FileReadException {

        return JsonFileLoader.loadFromFileInside(templateFilePath, Template.class);
    }

    public static void main(String[] args){

        TemplateService templateService = new TemplateService();

        Map<String, Map.Entry<String, String>> templatesAndInvoicesMappings = new HashMap<>();

        templatesAndInvoicesMappings.put("techpil", new AbstractMap.SimpleEntry<>("schemas/techpil.json", "invoices/fra z nr.pdf"));
        templatesAndInvoicesMappings.put("garden-parts", new AbstractMap.SimpleEntry<>("schemas/garden-parts.json", "invoices/329157-2025.pdf"));
        templatesAndInvoicesMappings.put("rozkwit", new AbstractMap.SimpleEntry<>("schemas/rozkwit.json", "invoices/FS015302025B26A.pdf"));

        Map.Entry<String, String> gotTemplateAndInvoiceMapping = templatesAndInvoicesMappings.get("garden-parts");

        String schemaFilePath = gotTemplateAndInvoiceMapping.getKey();
        String invoiceFilePath = gotTemplateAndInvoiceMapping.getValue();

        Template pdfFileTemplate = templateService.loadTemplate(schemaFilePath);

        byte[] data = FileReader.getDataFromFileInside(invoiceFilePath);

        TemplateCombinedData templateCombinedData = templateService.applyTemplate(pdfFileTemplate, data);

        System.out.println(templateCombinedData);
    }

}
