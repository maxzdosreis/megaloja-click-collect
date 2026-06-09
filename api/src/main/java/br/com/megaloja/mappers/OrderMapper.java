package br.com.megaloja.mappers;

import br.com.megaloja.dtos.CreateOrderRequest;
import br.com.megaloja.dtos.OrderResponse;
import br.com.megaloja.models.Order;
import br.com.megaloja.models.OrderItem;
import br.com.megaloja.models.User;
import br.com.megaloja.models.Store;
import br.com.megaloja.repositories.StoreRepository;
import br.com.megaloja.repositories.UserRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public abstract class OrderMapper {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected StoreRepository storeRepository;

    @Mapping(target = "customer", expression = "java(userRepository.getReferenceById(dto.customerId()))")
    @Mapping(target = "store", expression = "java(storeRepository.getReferenceById(dto.storeId()))")
    @Mapping(target = "items", source = "items")
    public abstract Order toEntity(CreateOrderRequest dto);

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "storeId", source = "store.id")
    @Mapping(target = "storeName", source = "store.name")
    public abstract OrderResponse toResponse(Order order);

    @AfterMapping
    void linkOrderItems(@MappingTarget Order order) {
        List<OrderItem> items = order.getItems();
        if (items != null) {
            for (OrderItem i : items) {
                i.setOrder(order);
            }
        }
    }
}

