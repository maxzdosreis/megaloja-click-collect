package br.com.megaloja.services;

import br.com.megaloja.dtos.CreateUserRequest;
import br.com.megaloja.dtos.UpdateUserRequest;
import br.com.megaloja.dtos.UserResponse;
import br.com.megaloja.exceptions.BusinessException;
import br.com.megaloja.exceptions.ResourceNotFoundException;
import br.com.megaloja.filters.UserFilter;
import br.com.megaloja.mappers.UserMapper;
import br.com.megaloja.models.User;
import br.com.megaloja.models.enums.UserRole;
import br.com.megaloja.repositories.UserRepository;
import br.com.megaloja.specifications.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Já existe um usuário cadastrado com o email: " + request.email());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.CUSTOMER);
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = findEntityById(id);

        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Já existe um usuário cadastrado com o email: " + request.getEmail());
        }

        userMapper.updateFromDto(request, user);
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    public UserResponse findById(Long id) {
        return userMapper.toResponse(findEntityById(id));
    }

    public Page<UserResponse> findAll(UserFilter filter, Pageable pageable) {
        return userRepository.findAll(UserSpecification.withFilters(filter), pageable)
                .map(userMapper::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        User user = findEntityById(id);
        userRepository.delete(user);
    }

    public User findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
    }
}
