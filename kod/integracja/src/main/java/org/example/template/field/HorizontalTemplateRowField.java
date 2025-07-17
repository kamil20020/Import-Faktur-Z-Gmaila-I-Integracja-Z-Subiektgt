package org.example.template.field;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.example.template.TemplateRectCords;

import java.awt.geom.Rectangle2D;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HorizontalTemplateRowField extends TemplateRowField{

    @JsonProperty("xMinCord")
    private float xMinCord;

    @JsonProperty("xMaxCord")
    private float xMaxCord;

    @Override
    public boolean equals(Object o) {

        if (this == o){

            return true;
        }

        if (o == null || getClass() != o.getClass()) {

            return false;
        }

        if (!super.equals(o)) {

            return false;
        }

        HorizontalTemplateRowField that = (HorizontalTemplateRowField) o;

        if (Float.compare(that.xMinCord, xMinCord) != 0) {

            return false;
        }

        return Float.compare(that.xMaxCord, xMaxCord) == 0;
    }

    @Override
    public int hashCode() {

        int result = super.hashCode();

        result = 31 * result + (xMinCord != +0.0f ? Float.floatToIntBits(xMinCord) : 0);
        result = 31 * result + (xMaxCord != +0.0f ? Float.floatToIntBits(xMaxCord) : 0);

        return result;
    }

    public Rectangle2D.Double getRect(double yMin, double yMax) {

        return TemplateRectCords.getRect(xMinCord, yMin, xMaxCord, yMax);
    }

    @Override
    public void handleRect(Rectangle2D.Float rect) throws IllegalStateException {

        xMinCord = rect.x;
        xMaxCord = rect.x + rect.width;
    }

    @Override
    public String toString() {

        return "HorizontalTemplateRowField{" +
                "XMinCord=" + getXMinCord() +
                ", XMaxCord=" + getXMaxCord() +
                ", xMinCord=" + xMinCord +
                ", xMaxCord=" + xMaxCord +
                ", name='" + getName() + '\'' +
                ", type='" + getType() + '\'' +
                ", hidden=" + isHidden() +
                ", defaultValue='" + getDefaultValue() + '\'' +
                ", index=" + getIndex() +
                ", separator='" + getSeparator() + '\'' +
                '}';
    }
}
