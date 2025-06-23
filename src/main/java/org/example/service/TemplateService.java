package org.example.service;

import org.example.loader.JsonFileLoader;
import org.example.template.Template;
import org.example.template.data.TemplateCombinedData;

import java.io.File;

public class TemplateService {

    public TemplateCombinedData applyTemplate(String templateFilePath, File gotFile) {

        Template template = loadTemplate(templateFilePath);

        return applyTemplate(template, gotFile);
    }

    public TemplateCombinedData applyTemplate(Template template, File gotFile) {

        return new TemplateCombinedData(
            template.extractPlace(gotFile),
            template.extractCreationDate(gotFile),
            template.extractReceiveDate(gotFile),
            template.extractTitle(gotFile),
            template.extractCreator(gotFile),
            template.extractInvoiceItems(gotFile),
            template.isTaxOriented()
        );
    }

    public Template loadTemplate(String templateFilePath) {

        return JsonFileLoader.loadFromFileInside(templateFilePath, Template.class);
    }

}
