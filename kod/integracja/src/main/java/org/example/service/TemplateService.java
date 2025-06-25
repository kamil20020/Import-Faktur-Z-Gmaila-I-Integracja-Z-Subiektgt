package org.example.service;

import org.example.api.Api;
import org.example.exception.FileReadException;
import org.example.loader.FileReader;
import org.example.loader.JsonFileLoader;
import org.example.template.Template;
import org.example.template.data.DataExtractedFromTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class TemplateService {

    private static final Map<String, Template> companyTemplateMappings = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(Api.class);

    private static final String TEMPLATES_SCHEMA_PATH = "schemas/";

    static {

        loadTemplatesSchemas();
    }

    private static void loadTemplatesSchemas() throws FileReadException {

        log.info("Loading templates");

        File templatesSchemasLocation = FileReader.getFileFromOutside(TEMPLATES_SCHEMA_PATH);

        File[] schemaFiles = templatesSchemasLocation.listFiles();

        if(schemaFiles == null){

            throw new FileReadException("There are no templates schemas files");
        }

        for(File schemaFile : schemaFiles){

            String schemaFileName = schemaFile.getName()
                .replaceAll("\\.json", "");

            Template template = loadTemplateSchema(schemaFile);

            companyTemplateMappings.put(schemaFileName, template);

            log.info("Loaded template {}", schemaFileName);
        }

        log.info("Loaded {} templates", companyTemplateMappings.size());
    }

    private static Template loadTemplateSchema(File file) throws FileReadException {

        return JsonFileLoader.loadFromFile(file, Template.class);
    }

    private Template loadTemplateSchema(String templateFilePath) throws FileReadException {

        return JsonFileLoader.loadFromFileOutside(templateFilePath, Template.class);
    }

    public DataExtractedFromTemplate applyGoodTemplateForData(byte[] data) throws FileReadException{

        Optional<Template> foundTemplateOpt = findTemplateForData(data);

        if(foundTemplateOpt.isEmpty()){

            throw new FileReadException("Could not find good template");
        }

        Template foundTemplate = foundTemplateOpt.get();

        return applyTemplate(foundTemplate, data);
    }

    private Optional<Template> findTemplateForData(byte[] data){

        for(Map.Entry<String, Template> companyTemplateMapping : companyTemplateMappings.entrySet()){

            String templateCompany = companyTemplateMapping.getKey();

            boolean isGoodTemplate = Template.hasCompany(data, templateCompany);

            if(isGoodTemplate){

                log.info("Template {} was selected", templateCompany);

                Template template = companyTemplateMapping.getValue();

                return Optional.of(template);
            }
        }

        return Optional.empty();
    }

    private DataExtractedFromTemplate applyTemplate(Template template, byte[] data) {

        return new DataExtractedFromTemplate(
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

    public static void main(String[] args){

        TemplateService templateService = new TemplateService();

        List<String> invoices = List.of(
            "invoices/fra z nr.pdf",
            "invoices/329157-2025.pdf",
            "invoices/FS015302025B26A.pdf"
        );

        String invoiceName = invoices.get(0);

        byte[] invoiceData = FileReader.getDataFromFileInside(invoiceName);

        DataExtractedFromTemplate dataExtractedFromTemplate = templateService.applyGoodTemplateForData(invoiceData);

        System.out.println(dataExtractedFromTemplate);
    }

}
