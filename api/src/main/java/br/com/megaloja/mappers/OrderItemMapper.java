package br.com.megaloja.mappers;

import br.com.megaloja.dtos.CreateOrderItemRequest;
import br.com.megaloja.dtos.OrderItemResponse;
import br.com.megaloja.models.OrderItem;
import br.com.megaloja.repositories.ProductRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class OrderItemMapper {

    @Autowired
    protected ProductRepository productRepository;

    @Mapping(target = "product", expression = "java(productRepository.getReferenceById(dto.productId()))")
    @Mapping(target = "order", ignore = true)
    public abstract OrderItem toEntity(CreateOrderItemRequest dto);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "orderId", source = "order.id")
    public abstract OrderItemResponse toResponse(OrderItem item);
}
