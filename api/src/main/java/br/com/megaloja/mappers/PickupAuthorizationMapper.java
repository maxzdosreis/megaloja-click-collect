package br.com.megaloja.mappers;

import br.com.megaloja.dtos.CreatePickupAuthorizationRequest;
import br.com.megaloja.dtos.PickupAuthorizationResponse;
import br.com.megaloja.models.Order;
import br.com.megaloja.models.PickupAuthorization;
import br.com.megaloja.repositories.OrderRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PickupAuthorizationMapper {

    @Autowired
    protected OrderRepository orderRepository;

    @Mapping(target = "order", expression = "java(orderRepository.getReferenceById(dto.orderId()))")
    @Mapping(target = "createdAt", ignore = true)
    public abstract PickupAuthorization toEntity(CreatePickupAuthorizationRequest dto);

    @Mapping(target = "orderId", source = "order.id")
    public abstract PickupAuthorizationResponse toResponse(PickupAuthorization pickupAuthorization);
}

