package br.com.megaloja.mappers;

import br.com.megaloja.dtos.CreateNotificationRequest;
import br.com.megaloja.dtos.NotificationResponse;
import br.com.megaloja.models.Notification;
import br.com.megaloja.models.Order;
import br.com.megaloja.repositories.OrderRepository;
import br.com.megaloja.repositories.StoreRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class NotificationMapper {

    @Autowired
    protected OrderRepository orderRepository;

    @Mapping(target = "order", expression = "java(orderRepository.getReferenceById(dto.orderId()))")
    @Mapping(target = "isRead", constant = "false")
    @Mapping(target = "sentAt", ignore = true)
    public abstract Notification toEntity(CreateNotificationRequest dto);

    @Mapping(target = "orderId", source = "order.id")
    public abstract NotificationResponse toResponse(Notification notification);
}

