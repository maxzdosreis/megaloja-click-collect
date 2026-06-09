package br.com.megaloja.mappers;

import br.com.megaloja.dtos.CreateStoreRequest;
import br.com.megaloja.dtos.StoreResponse;
import br.com.megaloja.dtos.UpdateStoreRequest;
import br.com.megaloja.models.Store;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    @Mapping(target = "active", constant = "true")
    Store toEntity(CreateStoreRequest request);

    StoreResponse toResponse(Store store);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateStoreRequest request, @MappingTarget Store entity);
}