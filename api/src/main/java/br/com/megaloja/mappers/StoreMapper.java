package br.com.megaloja.mappers;

import br.com.megaloja.dtos.CreateStoreRequest;
import br.com.megaloja.dtos.StoreResponse;
import br.com.megaloja.dtos.UpdateStoreRequest;
import br.com.megaloja.models.Store;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    Store toEntity(CreateStoreRequest request);

    StoreResponse toResponse(Store store);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateStoreRequest request, @MappingTarget Store entity);
}