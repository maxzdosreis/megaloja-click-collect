package br.com.megaloja.security;

import br.com.megaloja.dtos.*;
import br.com.megaloja.exceptions.BusinessException;
import br.com.megaloja.exceptions.InvalidTokenException;
import br.com.megaloja.mappers.UserMapper;
import br.com.megaloja.models.User;
import br.com.megaloja.models.enums.UserRole;
import br.com.megaloja.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Já existe um usuário cadastrado com o email: " + request.email());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.CUSTOMER);
        user = userRepository.save(user);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com email: " + request.email()));

        return buildAuthResponse(user);
    }

    public AuthResponse refresh(RefreshRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtService.isValid(refreshToken)) {
            throw new InvalidTokenException("Refresh token inválido ou expirado");
        }

        String email = jwtService.extractEmail(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com email: " + email));

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        UserResponse userResponse = userMapper.toResponse(user);

        return new AuthResponse(token, refreshToken, "Bearer", jwtService.getExpiration(), userResponse);
    }
}
