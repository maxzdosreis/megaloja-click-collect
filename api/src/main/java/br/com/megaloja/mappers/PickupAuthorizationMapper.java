package br.com.megaloja.mappers;

import br.com.megaloja.dtos.CreatePickupAuthorizationRequest;
import br.com.megaloja.dtos.PickupAuthorizationResponse;
import br.com.megaloja.models.Order;
import br.com.megaloja.models.PickupAuthorization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PickupAuthorizationMapper {

    @Mapping(target = "order", expression = "java(mapOrder(dto.orderId()))")
    PickupAuthorization toEntity(CreatePickupAuthorizationRequest dto);

    @Mapping(target = "orderId", source = "order.id")
    PickupAuthorizationResponse toResponse(PickupAuthorization pickupAuthorization);

    default Order mapOrder(Long id) {
        if (id == null) return null;
        Order o = new Order();
        o.setId(id);
        return o;
    }
}

