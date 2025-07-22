package org.example.template.field;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.example.template.TemplateRectCords;

import java.awt.geom.Rectangle2D;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

    @JsonIgnore
    public Rectangle2D.Double getRect(){

        return TemplateRectCords.getRect(xMinCord, yMinCord, xMaxCord, yMaxCord);
    }

    @Override
    public void handleRect(Rectangle2D.Float rect) throws IllegalStateException {

        xMinCord = rect.x;
        yMinCord = rect.y;
        xMaxCord = rect.x + rect.width;
        yMaxCord = rect.y + rect.height;
    }

    public boolean isValid(){

        return xMinCord >= 0 && xMaxCord >= 0 && yMinCord >= 0 && yMinCord != 0 &&
            xMinCord != xMaxCord && yMinCord != yMaxCord;
    }

    @Override
    public String toString() {

        return "AreaTemplateRowField{" +
                "xMinCord=" + xMinCord +
                ", xMaxCord=" + xMaxCord +
                ", yMinCord=" + yMinCord +
                ", yMaxCord=" + yMaxCord +
                ", name='" + getName() + '\'' +
                ", type='" + getType() + '\'' +
                ", hidden=" + isHidden() +
                ", defaultValue='" + getDefaultValue() + '\'' +
                ", separator='" + getSeparator() + '\'' +
                ", index=" + getIndex() +
                '}';
    }
}

