package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.api.Api;
import org.example.exception.ConflictException;
import org.example.exception.FileReadException;
import org.example.loader.FileReader;
import org.example.loader.JsonFileLoader;
import org.example.loader.pdf.PdfFileReader;
import org.example.template.Template;
import org.example.template.data.DataExtractedFromTemplate;
import org.example.template.data.TemplateBasicData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Service
public class TemplateService {

    private static final Map<String, Template> companyTemplateMappings = new HashMap<>();
    private static Set<String> companies = new HashSet<>();

    private static final Logger log = LoggerFactory.getLogger(Api.class);

    private static final String TEMPLATES_SCHEMA_PATH = "schemas/";

    private static final ObjectMapper objectMapper = new ObjectMapper();

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
                .replaceAll("\\.json", "")
                .toLowerCase();

            Template template = loadTemplateSchema(schemaFile);

            companyTemplateMappings.put(schemaFileName, template);

            log.info("Loaded template {}", schemaFileName);
        }

        log.info("Loaded {} templates", companyTemplateMappings.size());

        companies = companyTemplateMappings.keySet();
    }

    public Set<String> getSchemasNames(){

        return companies;
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

            throw new FileReadException("Nie udało się odnaleźć odpowiedniego szablonu faktury");
        }

        Template foundTemplate = foundTemplateOpt.get();

        return applyTemplate(foundTemplate, data);
    }

    public Optional<Template> findTemplate(String content){

        Optional<String> foundTemplateNameOpt = Template.searchInContent(content, companies);

        if(foundTemplateNameOpt.isEmpty()){

            return Optional.empty();
        }

        String foundTemplateName = foundTemplateNameOpt.get();

        return handleFoundTemplate(foundTemplateName);
    }

    public Optional<Template> findTemplateForData(byte[] data){

        Optional<String> foundTemplateNameOpt = Template.searchInFile(data, companies);

        if(foundTemplateNameOpt.isEmpty()){

            return Optional.empty();
        }

        String foundTemplateName = foundTemplateNameOpt.get();

        return handleFoundTemplate(foundTemplateName);
    }

    private Optional<Template> handleFoundTemplate(String foundTemplateName){

        log.info("Template {} was selected", foundTemplateName);

        Template foundTemplate = companyTemplateMappings.get(foundTemplateName);

        return Optional.of(foundTemplate);
    }

    public void addTemplate(String templateName, Template template) throws ConflictException, IllegalStateException{

        if(templateName == null){

            templateName = "szablon";
        }
        else{

            templateName = templateName.toLowerCase();
        }

        addTemplateFile(templateName, template);

        companyTemplateMappings.put(templateName, template);
        companies = companyTemplateMappings.keySet();
    }

    private void addTemplateFile(String templateName, Template template) throws ConflictException {

        String templateFilePath = getTemplateFilePath(templateName);

        File templateFile = new File(templateFilePath);

        if(templateFile.exists()){

            throw new ConflictException("Istnieje już szablon o takiej nazwie " + templateName);
        }

        try {

            boolean didCreateTemplateFile = templateFile.createNewFile();

            if(!didCreateTemplateFile){

                throw new IOException();
            }

            objectMapper.writeValue(templateFile, template);
        }
        catch (IOException e) {

            e.printStackTrace();

            throw new IllegalStateException("Nie udało się zapisać szablonu " + templateName + " do pliku " + templateFilePath);
        }
    }

    public boolean remove(String templateName){

        if(templateName == null){

            return false;
        }

        templateName = templateName.toLowerCase();

        Template removedTemplate = companyTemplateMappings.remove(templateName);

        boolean didRemoveCompany = companies.remove(templateName);

        boolean didRemoveTemplateFile = removeTemplateFile(templateName);

        return removedTemplate != null && didRemoveCompany && didRemoveTemplateFile;
    }

    private boolean removeTemplateFile(String templateName){

        String templateFilePath = getTemplateFilePath(templateName);

        File templateFile = new File(templateFilePath);

        return templateFile.delete();
    }

    private String getTemplateFilePath(String templateName){

        return TEMPLATES_SCHEMA_PATH + templateName + ".json";
    }

    public DataExtractedFromTemplate applyTemplate(Template template, byte[] data) {

        TemplateBasicData templateBasicData = template.extractBasicData(data);

        return new DataExtractedFromTemplate(
            templateBasicData.getPlace(),
            templateBasicData.getCreationDate(),
            templateBasicData.getReceiveDate(),
            templateBasicData.getTitle(),
            template.extractCreator(data),
            template.extractInvoiceItems(data),
            template.isTaxOriented(),
            template.extractTotalPrice(data),
            template.extractPayDate(data)
        );
    }

}
