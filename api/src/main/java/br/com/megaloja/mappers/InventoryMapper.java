package br.com.megaloja.mappers;

import br.com.megaloja.dtos.CreateInventoryRequest;
import br.com.megaloja.dtos.InventoryResponse;
import br.com.megaloja.dtos.UpdateInventoryRequest;
import br.com.megaloja.models.Inventory;
import br.com.megaloja.models.Product;
import br.com.megaloja.models.Store;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "store", expression = "java(mapStore(dto.storeId()))")
    @Mapping(target = "product", expression = "java(mapProduct(dto.productId()))")
    Inventory toEntity(CreateInventoryRequest dto);

    @Mapping(target = "storeId", source = "store.id")
    @Mapping(target = "productId", source = "product.id")
    InventoryResponse toResponse(Inventory inventory);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateInventoryRequest dto, @MappingTarget Inventory entity);

    default Store mapStore(Long id) {
        if (id == null) return null;
        Store s = new Store();
        s.setId(id);
        return s;
    }

    default Product mapProduct(Long id) {
        if (id == null) return null;
        Product p = new Product();
        p.setId(id);
        return p;
    }
}

