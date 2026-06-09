package br.com.megaloja.mappers;

import br.com.megaloja.dtos.CreateProductRequest;
import br.com.megaloja.dtos.ProductResponse;
import br.com.megaloja.dtos.UpdateProductRequest;
import br.com.megaloja.models.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

	@Mapping(target = "active", constant = "true")
	Product toEntity(CreateProductRequest request);

	ProductResponse toResponse(Product product);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateFromDto(UpdateProductRequest request, @MappingTarget Product entity);
}


