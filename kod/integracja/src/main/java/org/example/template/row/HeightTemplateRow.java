package org.example.template.row;

import lombok.*;
import org.example.template.TemplateCords;
import org.example.template.field.HorizontalTemplateRowField;
import org.example.template.field.TemplateRowField;
import org.example.template.TriFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class HeightTemplateRow extends TemplateRow{

    private Integer skipStart;
    private String startStr;
    private String endStr;
    private float rowHeight;

    public HeightTemplateRow(String type, List<TemplateRowField> fields, Integer skipStart, String startStr, String endStr, float rowHeight) {

        super(type, fields);

        this.skipStart = skipStart;
        this.startStr = startStr;
        this.endStr = endStr;
        this.rowHeight = rowHeight;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {

            return true;
        }

        if (o == null || getClass() != o.getClass()){

            return false;
        }
        if (!super.equals(o)) {

            return false;
        }

        HeightTemplateRow that = (HeightTemplateRow) o;

        if (skipStart != null ? !skipStart.equals(that.skipStart) : that.skipStart != null) {

            return false;
        }

        if (startStr != null ? !startStr.equals(that.startStr) : that.startStr != null) {

            return false;
        }

        if (endStr != null ? !endStr.equals(that.endStr) : that.endStr != null){

            return false;
        }

        return Float.compare(rowHeight, this.rowHeight) == 0;
    }

    @Override
    public int hashCode() {

        int result = super.hashCode();

        result = 31 * result + (skipStart != null ? skipStart.hashCode() : 0);
        result = 31 * result + (startStr != null ? startStr.hashCode() : 0);
        result = 31 * result + (endStr != null ? endStr.hashCode() : 0);
        result = 31 * result + Float.hashCode(rowHeight);

        return result;
    }

    public List<Map<String, String>> getValues(List<String> gotLines, List<Float> linesYCords, TriFunction<HorizontalTemplateRowField, Double, Double, String[]> extractValues){

        int lineStartIndex = getLineStartIndex(gotLines, startStr);

        if(skipStart != null){

            lineStartIndex += skipStart;
        }

        double startY = linesYCords.get(lineStartIndex);
        startY = TemplateCords.convertPtToPx(startY);

        List<Map<String, String>> values = new ArrayList<>();

        for(int i = lineStartIndex; i < gotLines.size(); i++){

            String line = gotLines.get(i);

            if(endStr != null && line.startsWith(endStr)){
                break;
            }

            Map<String, String> gotValuesRow = getValuesRow(extractValues, startY);

            values.add(gotValuesRow);

            startY += rowHeight;
        }

        return values;
    }

    private static int getLineStartIndex(List<String> gotLines, String searchText){

        int invoiceItemsStartIndex = 0;

        for (String line : gotLines) {

            if (line.startsWith(searchText)) {
                break;
            }

            invoiceItemsStartIndex++;
        }

        return invoiceItemsStartIndex;
    }

    private Map<String, String> getValuesRow(TriFunction<HorizontalTemplateRowField, Double, Double, String[]> extractValues, double startY) {

        Map<String, String> values = new HashMap<>();

        if(fields == null || fields.isEmpty()){

            return values;
        }

        double endY = startY + rowHeight;

        for(TemplateRowField field : fields){

            HorizontalTemplateRowField horizontalField = (HorizontalTemplateRowField) field;

            String[] gotValues = extractValues.apply(horizontalField, startY, endY);

            String extractedValue = horizontalField.extract(gotValues);

            String fieldName = field.getName();

            values.put(fieldName, extractedValue);
        }

        return values;
    }

}
