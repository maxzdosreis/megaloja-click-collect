package br.com.megaloja.mappers;

import br.com.megaloja.dtos.CreateOrderStatusHistoryRequest;
import br.com.megaloja.dtos.OrderStatusHistoryResponse;
import br.com.megaloja.models.OrderStatusHistory;
import br.com.megaloja.models.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderStatusHistoryMapper {

    @Mapping(target = "order", expression = "java(mapOrder(dto.orderId()))")
    OrderStatusHistory toEntity(CreateOrderStatusHistoryRequest dto);

    @Mapping(target = "orderId", source = "order.id")
    OrderStatusHistoryResponse toResponse(OrderStatusHistory statusHistory);

    default Order mapOrder(Long id) {
        if (id == null) return null;
        Order o = new Order();
        o.setId(id);
        return o;
    }
}

