package br.com.megaloja.mappers;

import br.com.megaloja.dtos.CreateNotificationRequest;
import br.com.megaloja.dtos.NotificationResponse;
import br.com.megaloja.models.Notification;
import br.com.megaloja.models.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "order", expression = "java(mapOrder(dto.orderId()))")
    Notification toEntity(CreateNotificationRequest dto);

    @Mapping(target = "orderId", source = "order.id")
    NotificationResponse toResponse(Notification notification);

    default Order mapOrder(Long id) {
        if (id == null) return null;
        Order o = new Order();
        o.setId(id);
        return o;
    }
}

