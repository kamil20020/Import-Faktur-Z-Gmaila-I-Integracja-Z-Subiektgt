package org.example.template.data;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.Map;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TemplateInvoiceItem {

    private String code = "";
    private String name = "";
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal tax;

    public boolean hasAllValuesWithoutCode(){

        return name != null && price != null && quantity != null && tax != null;
    }

    public static TemplateInvoiceItem extract(Map<String, String> gotValues){

        if(gotValues == null || gotValues.isEmpty()){

            return new TemplateInvoiceItem();
        }

        return TemplateInvoiceItem.builder()
            .name(extractName(gotValues))
            .code(extractCode(gotValues))
            .price(extractPrice(gotValues))
            .quantity(extractQuantity(gotValues))
            .tax(extractTax(gotValues))
            .build();
    }

    private static String extractName(Map<String, String> gotValues){

        String rawName = gotValues.get("name");

        if(rawName == null){

            return null;
        }

        return rawName
            .stripIndent();
    }

    private static String extractCode(Map<String, String> gotValues){

        String rawCode = gotValues.get("code");

        if(rawCode == null){

            return null;
        }

        String code = rawCode.stripIndent();

        String normalized = Normalizer.normalize(code, Normalizer.Form.NFD);

        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
            .replaceAll("ł", "l")
            .replaceAll("Ł", "L");
    }

    private static BigDecimal extractPrice(Map<String, String> gotValues){

        String rawPrice = gotValues.get("price");

        if(rawPrice == null){

            return null;
        }

        return TemplateConverter.convertToBigDecimal(rawPrice);
    }

    private static Integer extractQuantity(Map<String, String> gotValues){

        String rawQuantity = gotValues.get("quantity");

        if(rawQuantity == null){

            return null;
        }

        return TemplateConverter.convertToInteger(rawQuantity);
    }

    private static BigDecimal extractTax(Map<String, String> gotValues){

        String rawTax = gotValues.get("tax");

        if(rawTax == null){

            return null;
        }

        return TemplateConverter.convertToBigDecimal(rawTax);
    }

    public BigDecimal getTotalPriceWithTax(boolean isInvoiceTaxOriented){

        BigDecimal unitPriceWithTax = getUnitPriceWithTax(isInvoiceTaxOriented);

        if(unitPriceWithTax == null || quantity == null){

            return null;
        }

        BigDecimal quantityConverted = BigDecimal.valueOf(quantity);

        return quantityConverted.multiply(unitPriceWithTax);
    }

    public BigDecimal getUnitPriceWithTax(boolean isInvoiceTaxOriented){

        if(isInvoiceTaxOriented){

            return price;
        }

        if(price == null || tax == null){

            return null;
        }

        BigDecimal hundred = BigDecimal.valueOf(100);

        BigDecimal taxFraction = tax.divide(hundred);

        BigDecimal taxValue = price.multiply(taxFraction);

        return price.add(taxValue);
    }

    public BigDecimal getTotalPriceWithoutTax(boolean isInvoiceTaxOriented){

        BigDecimal unitPriceWithoutTax = getUnitPriceWithoutTax(isInvoiceTaxOriented);

        if(unitPriceWithoutTax == null || quantity == null){

            return null;
        }

        BigDecimal quantityConverted = BigDecimal.valueOf(quantity);

        return quantityConverted.multiply(unitPriceWithoutTax);
    }


    public BigDecimal getUnitPriceWithoutTax(boolean isInvoiceTaxOriented){

        if(!isInvoiceTaxOriented){

            return price;
        }

        if(price == null || tax == null){

            return null;
        }

        //brutto = netto + 0,23 * netto
        //brutto = netto * (1 + 0,23)
        //brutto / (1 + 0,23) = netto
        //brutto / 1,23 = netto

        BigDecimal hundred = BigDecimal.valueOf(100);
        BigDecimal scaledTax = tax.divide(hundred);

        BigDecimal denominator = BigDecimal.ONE.add(scaledTax);

        return price.divide(denominator, 10, RoundingMode.HALF_UP);
    }

    public void appendData(TemplateInvoiceItem newTemplateInvoiceItemData){

        if(newTemplateInvoiceItemData == null){
            return;
        }

        String newName = newTemplateInvoiceItemData.getName();

        if(newName != null && !newName.isEmpty()){

            String separator = name.isEmpty() ? "" : " ";

            name = name + separator + newTemplateInvoiceItemData.getName();
        }

        if(code == null || code.isEmpty()){

            code = newTemplateInvoiceItemData.getCode();

            if(code == null){

                code = "";
            }
        }

        if(price == null){

            price = newTemplateInvoiceItemData.getPrice();
        }

        if(quantity == null){

            quantity = newTemplateInvoiceItemData.getQuantity();
        }

        if(tax == null){

            tax = newTemplateInvoiceItemData.getTax();
        }
    }

}
