package com.forcreators.api.web;

import com.forcreators.api.dto.ApiDtos.AuthRequest;
import com.forcreators.api.dto.ApiDtos.AuthResponse;
import com.forcreators.api.dto.ApiDtos.UserResponse;
import com.forcreators.api.security.CurrentUser;
import com.forcreators.api.service.AuthService;
import com.forcreators.api.service.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CurrentUser currentUser;
    private final DtoMapper mapper;

    public AuthController(AuthService authService, CurrentUser currentUser, DtoMapper mapper) {
        this.authService = authService;
        this.currentUser = currentUser;
        this.mapper = mapper;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody AuthRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse me() {
        return mapper.toUser(currentUser.require());
    }
}
