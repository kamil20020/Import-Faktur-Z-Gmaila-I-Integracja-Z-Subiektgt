package org.example;

import org.example.gui.Window;
import org.example.loader.FileReader;
import org.example.template.Template;
import org.example.template.data.TemplateCombinedData;

import java.io.File;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {

        Template pdfFileTemplate = Template.load("schemas/subiekt.json");
        File gotFile = FileReader.getFileFromInside("invoices/fra z nr.pdf");

//        Template pdfFileTemplate = Template.load("schemas/garden-parts.json");
//        File gotFile = FileReader.getFileFromInside("invoices/329157-2025.pdf");

//        Template pdfFileTemplate = Template.load("schemas/rozkwit.json");
//        File gotFile = FileReader.getFileFromInside("invoices/FS015302025B26A.pdf");

        TemplateCombinedData templateCombinedData = pdfFileTemplate.applyTemplate(gotFile);

        System.out.println(templateCombinedData);

        System.out.println("Hello World!");

        new Window();
    }
}
