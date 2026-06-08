package br.com.megaloja.mappers;

import br.com.megaloja.dtos.CreateOrderItemRequest;
import br.com.megaloja.dtos.OrderItemResponse;
import br.com.megaloja.models.OrderItem;
import br.com.megaloja.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "product", expression = "java(mapProduct(dto.productId()))")
    OrderItem toEntity(CreateOrderItemRequest dto);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "orderId", source = "order.id")
    OrderItemResponse toResponse(OrderItem item);

    default Product mapProduct(Long id) {
        if (id == null) return null;
        Product p = new Product();
        p.setId(id);
        return p;
    }
}


