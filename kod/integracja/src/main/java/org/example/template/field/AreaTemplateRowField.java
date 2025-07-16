package org.example.template.field;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.example.template.TemplateRectCords;

import java.awt.geom.Rectangle2D;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AreaTemplateRowField extends TemplateRowField{

    @JsonProperty("xMinCord")
    private float xMinCord;

    @JsonProperty("xMaxCord")
    private float xMaxCord;

    @JsonProperty("yMinCord")
    private float yMinCord;

    @JsonProperty("yMaxCord")
    private float yMaxCord;

    @Override
    public boolean equals(Object o) {

        if (this == o) {

            return true;
        }
        if (o == null || getClass() != o.getClass()) {

            return false;
        }
        if (!super.equals(o)) {

            return false;
        }

        AreaTemplateRowField that = (AreaTemplateRowField) o;

        if (Float.compare(that.xMinCord, xMinCord) != 0) {

            return false;
        }
        if (Float.compare(that.xMaxCord, xMaxCord) != 0) {

            return false;
        }
        if (Float.compare(that.yMinCord, yMinCord) != 0) {

            return false;
        }

        return Float.compare(that.yMaxCord, yMaxCord) == 0;
    }

    @Override
    public int hashCode() {

        int result = super.hashCode();

        result = 31 * result + (xMinCord != +0.0f ? Float.floatToIntBits(xMinCord) : 0);
        result = 31 * result + (xMaxCord != +0.0f ? Float.floatToIntBits(xMaxCord) : 0);
        result = 31 * result + (yMinCord != +0.0f ? Float.floatToIntBits(yMinCord) : 0);
        result = 31 * result + (yMaxCord != +0.0f ? Float.floatToIntBits(yMaxCord) : 0);

        return result;
    }

    public Rectangle2D.Double getRect(){

        return TemplateRectCords.getRect(xMinCord, yMinCord, xMaxCord, yMaxCord);
    }

}

