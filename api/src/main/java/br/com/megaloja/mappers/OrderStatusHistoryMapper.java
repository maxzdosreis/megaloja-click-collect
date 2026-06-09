package br.com.megaloja.mappers;

import br.com.megaloja.dtos.CreateOrderStatusHistoryRequest;
import br.com.megaloja.dtos.OrderStatusHistoryResponse;
import br.com.megaloja.models.OrderStatusHistory;
import br.com.megaloja.models.Order;
import br.com.megaloja.repositories.OrderRepository;
import br.com.megaloja.repositories.StoreRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class OrderStatusHistoryMapper {

    @Autowired
    protected OrderRepository orderRepository;

    @Mapping(target = "order", expression = "java(orderRepository.getReferenceById(dto.orderId()))")
    @Mapping(target = "createdAt", ignore = true)
    public abstract OrderStatusHistory toEntity(CreateOrderStatusHistoryRequest dto);

    @Mapping(target = "orderId", source = "order.id")
    public abstract OrderStatusHistoryResponse toResponse(OrderStatusHistory statusHistory);
}

