package br.com.megaloja.filters;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class InventoryFilter {

    private Long storeId;

    private Long productId;

    private String productName;

    private Integer minQuantity;

    private Integer maxQuantity;

    private Integer reservedQuantityMin;

    private Integer reservedQuantityMax;

    private Boolean hasStock;

}
