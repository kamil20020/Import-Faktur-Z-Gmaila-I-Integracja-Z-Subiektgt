package org.example.service;

import org.example.api.Api;
import org.example.exception.FileReadException;
import org.example.loader.FileReader;
import org.example.loader.JsonFileLoader;
import org.example.loader.pdf.PdfFileReader;
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
    private static Set<String> companies = new HashSet<>();

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

    public DataExtractedFromTemplate applyTemplate(Template template, byte[] data) {

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

}
