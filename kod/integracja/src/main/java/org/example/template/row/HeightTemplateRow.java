package org.example.template.row;

import lombok.*;
import org.example.template.field.HorizontalTemplateRowField;
import org.example.template.field.TemplateRowField;
import org.example.template.TriFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class HeightTemplateRow extends TemplateRow{

    private Integer skipStart;
    private String startStr;
    private String endStr;
    private Float rowHeight;

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

        return rowHeight != null ? rowHeight.equals(that.rowHeight) : that.rowHeight == null;
    }

    @Override
    public int hashCode() {

        int result = super.hashCode();

        result = 31 * result + (skipStart != null ? skipStart.hashCode() : 0);
        result = 31 * result + (startStr != null ? startStr.hashCode() : 0);
        result = 31 * result + (endStr != null ? endStr.hashCode() : 0);
        result = 31 * result + (rowHeight != null ? rowHeight.hashCode() : 0);

        return result;
    }

    public List<Map<String, String>> getValues(List<String> gotLines, List<Float> linesYCords, TriFunction<HorizontalTemplateRowField, Float, Float, String[]> extractValues){

        int lineStartIndex = getLineStartIndex(gotLines, startStr, true);

        float startY = linesYCords.get(lineStartIndex);

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

    private static int getLineStartIndex(List<String> gotLines, String searchText, boolean shouldAppendOne){

        int invoiceItemsStartIndex = 0;

        for (String line : gotLines) {

            if (line.startsWith(searchText)) {
                break;
            }

            invoiceItemsStartIndex++;
        }

        if(shouldAppendOne){

            invoiceItemsStartIndex++;
        }

        return invoiceItemsStartIndex;
    }

    private Map<String, String> getValuesRow(TriFunction<HorizontalTemplateRowField, Float, Float, String[]> extractValues, float startY) {

        Map<String, String> values = new HashMap<>();

        if(fields == null || fields.isEmpty()){

            return values;
        }

        float endY = startY + rowHeight;

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
