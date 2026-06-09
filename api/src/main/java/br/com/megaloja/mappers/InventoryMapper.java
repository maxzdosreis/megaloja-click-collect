package br.com.megaloja.mappers;

import br.com.megaloja.dtos.CreateInventoryRequest;
import br.com.megaloja.dtos.InventoryResponse;
import br.com.megaloja.dtos.UpdateInventoryRequest;
import br.com.megaloja.models.Inventory;
import br.com.megaloja.models.Product;
import br.com.megaloja.models.Store;
import br.com.megaloja.repositories.ProductRepository;
import br.com.megaloja.repositories.StoreRepository;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class InventoryMapper {

    @Autowired
    protected StoreRepository storeRepository;

    @Autowired
    protected ProductRepository productRepository;

    @Mapping(target = "store", expression = "java(storeRepository.getReferenceById(dto.storeId()))")
    @Mapping(target = "product", expression = "java(productRepository.getReferenceById(dto.productId()))")
    public abstract Inventory toEntity(CreateInventoryRequest dto);

    @Mapping(target = "storeId", source = "store.id")
    @Mapping(target = "productId", source = "product.id")
    public abstract InventoryResponse toResponse(Inventory inventory);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateFromDto(UpdateInventoryRequest dto, @MappingTarget Inventory entity);
}

