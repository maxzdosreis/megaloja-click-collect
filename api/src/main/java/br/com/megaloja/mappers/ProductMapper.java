package br.com.megaloja.mappers;

import br.com.megaloja.dtos.CreateProductRequest;
import br.com.megaloja.dtos.ProductResponse;
import br.com.megaloja.dtos.UpdateProductRequest;
import br.com.megaloja.models.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {

	Product toEntity(CreateProductRequest request);

	ProductResponse toResponse(Product product);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateFromDto(UpdateProductRequest request, @MappingTarget Product entity);
}


