package org.example.gui.integration;

import lombok.RequiredArgsConstructor;
import org.example.service.TemplateService;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Flow;

@Service
@RequiredArgsConstructor
public class TemplateMenuService {

    private final TemplateService templateService;

    public void showAvaiableSchemas(){

        Collection<String> schemasNames = templateService.getSchemasNames();

        GridBagLayout layout = new GridBagLayout();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel panel = new JPanel(layout);

        String combinedNames = loadSchemasNamesStr(schemasNames);

        JLabel namesLabel = new JLabel(combinedNames);
        namesLabel.setFont(new Font("", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        panel.add(namesLabel, gbc);

        JScrollPane scrollPane = new JScrollPane(panel);

        JDialog dialog = new JDialog((Frame) null, "Dostępne szablony faktur", false);

        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(panel);

        dialog.add(scrollPane);

        dialog.setVisible(true);
    }

    private String loadSchemasNamesStr(Collection<String> schemasNames){

        StringBuilder stringBuilder = new StringBuilder("<html>");

        Iterator<String> schemasNamesIterator = schemasNames.iterator();

        for(int i = 0; i < schemasNames.size(); i++){

            String schemaName = schemasNamesIterator.next();

            stringBuilder.append(i + 1);
            stringBuilder.append(". ");
            stringBuilder.append(schemaName);
            stringBuilder.append("<br/>");
        }

        stringBuilder.append("</html>");

        return stringBuilder.toString();
    }

}
