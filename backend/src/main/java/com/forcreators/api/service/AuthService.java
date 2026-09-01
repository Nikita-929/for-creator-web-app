package com.forcreators.api.service;

import com.forcreators.api.domain.Role;
import com.forcreators.api.domain.UserAccount;
import com.forcreators.api.dto.ApiDtos.AuthRequest;
import com.forcreators.api.dto.ApiDtos.AuthResponse;
import com.forcreators.api.exception.BusinessException;
import com.forcreators.api.repository.UserAccountRepository;
import com.forcreators.api.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final DtoMapper mapper;

    public AuthService(UserAccountRepository users, PasswordEncoder encoder, JwtService jwtService, DtoMapper mapper) {
        this.users = users;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.mapper = mapper;
    }

    @Transactional
    public AuthResponse register(AuthRequest request) {
        if (users.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessException("An account with this email already exists");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new BusinessException("Name is required");
        }
        UserAccount user = UserAccount.builder()
                .email(request.email().trim().toLowerCase())
                .name(sanitize(request.name()))
                .passwordHash(encoder.encode(request.password()))
                .role(Role.BUYER)
                .build();
        users.save(user);
        return tokenFor(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        UserAccount user = users.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BusinessException("Invalid email or password"));
        if (!encoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException("Invalid email or password");
        }
        return tokenFor(user);
    }

    private AuthResponse tokenFor(UserAccount user) {
        String token = jwtService.generate(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token, mapper.toUser(user));
    }

    static String sanitize(String input) {
        return input == null ? null : input.replaceAll("<[^>]*>", "").trim();
    }
}
