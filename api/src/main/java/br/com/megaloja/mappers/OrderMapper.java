package br.com.megaloja.mappers;

import br.com.megaloja.dtos.CreateOrderRequest;
import br.com.megaloja.dtos.OrderResponse;
import br.com.megaloja.models.Order;
import br.com.megaloja.models.OrderItem;
import br.com.megaloja.models.User;
import br.com.megaloja.models.Store;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(target = "customer", expression = "java(mapUser(dto.customerId()))")
    @Mapping(target = "store", expression = "java(mapStore(dto.storeId()))")
    @Mapping(target = "items", source = "items")
    Order toEntity(CreateOrderRequest dto);

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "storeId", source = "store.id")
    @Mapping(target = "storeName", source = "store.name")
    OrderResponse toResponse(Order order);

    default User mapUser(Long id) {
        if (id == null) return null;
        User u = new User();
        u.setId(id);
        return u;
    }

    default Store mapStore(Long id) {
        if (id == null) return null;
        Store s = new Store();
        s.setId(id);
        return s;
    }

    @AfterMapping
    default void linkOrderItems(@MappingTarget Order order) {
        List<OrderItem> items = order.getItems();
        if (items != null) {
            for (OrderItem i : items) {
                i.setOrder(order);
            }
        }
    }
}

