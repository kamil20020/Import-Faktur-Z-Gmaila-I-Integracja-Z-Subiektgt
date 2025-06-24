package org.example.template.data;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TemplateInvoiceItem {

    private String code;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal tax;

}
