package br.com.megaloja.mappers;

import br.com.megaloja.dtos.CreateUserRequest;
import br.com.megaloja.dtos.UpdateUserRequest;
import br.com.megaloja.dtos.UserResponse;
import br.com.megaloja.models.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(CreateUserRequest request);

    UserResponse toResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateUserRequest request, @MappingTarget User entity);
}