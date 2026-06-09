package br.com.megaloja.filters;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class ProductFilter {

    private String name;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private Boolean active;

}
