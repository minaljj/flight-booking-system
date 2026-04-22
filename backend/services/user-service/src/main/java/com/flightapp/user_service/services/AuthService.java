package com.flightapp.user_service.service;

import com.flightapp.user_service.dto.JwtResponse;
import com.flightapp.user_service.dto.LoginRequest;
import com.flightapp.user_service.dto.MessageResponse;
import com.flightapp.user_service.dto.SignupRequest;
import com.flightapp.user_service.model.AuditLog;

import com.flightapp.user_service.model.RefreshToken;
import com.flightapp.user_service.model.Role;
import com.flightapp.user_service.model.User;
import com.flightapp.user_service.repository.AuditLogRepository;

import com.flightapp.user_service.repository.RoleRepository;
import com.flightapp.user_service.repository.UserRepository;
import com.flightapp.user_service.security.JwtUtils;
import com.flightapp.user_service.security.UserDetailsServiceImplementation;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AuditLogRepository auditLogRepository;
   
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsServiceImplementation userDetails = (UserDetailsServiceImplementation) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        refreshTokenService.deleteByUserId(userDetails.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        auditLogRepository.save(
                new AuditLog("LOGIN", userDetails.getUsername(), "User logged in successfully")
        );
        return new JwtResponse(
                jwt,
                refreshToken.getToken(),
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        );
    }
    public MessageResponse registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }
        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())
        );
        Set<String> requestedRoles = signUpRequest.getRoles();
        Set<Role> roles = resolveRoles(requestedRoles, false);
        user.setRoles(roles);
        userRepository.save(user);
        auditLogRepository.save(
                new AuditLog("REGISTER", user.getUsername(), "User registered successfully")
        );
        return new MessageResponse("User registered successfully!");
    }
    public JwtResponse refreshJwtToken(String requestRefreshToken) {
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    List<String> roles = user.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toList());

                    String token = jwtUtils.generateTokenFromUsername(user.getUsername(), roles);
                    return new JwtResponse(
                            token,
                            requestRefreshToken,
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            roles
                    );
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }
    public MessageResponse logoutUser(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String jwt = headerAuth.substring(7);
            try {
                long expirationMs =
                        jwtUtils.getExpirationDateFromJwtToken(jwt).getTime()
                                - System.currentTimeMillis();

                tokenBlacklistService.blacklistToken(jwt, expirationMs);
            } catch (Exception e) {
                tokenBlacklistService.blacklistToken(jwt, 0);
            }
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsServiceImplementation) {
            UserDetailsServiceImplementation userDetails = (UserDetailsServiceImplementation) auth.getPrincipal();
            refreshTokenService.deleteByUserId(userDetails.getId());
            auditLogRepository.save(
                    new AuditLog("LOGOUT", userDetails.getUsername(), "User logged out")
            );
        }
        SecurityContextHolder.clearContext();
        return new MessageResponse("Log out successful!");
    }
    public MessageResponse blockUser(String username, boolean block, String adminName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        if (username.equals(adminName)) {
            throw new RuntimeException("Error: You cannot block yourself.");
        }
        user.setBlocked(block);
        userRepository.save(user);
        String action = block ? "BLOCK_USER" : "UNBLOCK_USER";
        auditLogRepository.save(
                new AuditLog(action, username, "User " + action + " by admin " + adminName)
        );
        logger.info("AUDIT: Admin [{}] {} user [{}]", adminName, action, username);
        return new MessageResponse(
                "User " + (block ? "blocked" : "unblocked") + " successfully."
        );
    }
    private Set<Role> resolveRoles(Set<String> requestedRoles, boolean adminAllowed) {
        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found in DB"));
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            return Set.of(defaultRole);
        }
        Set<Role> roles = new HashSet<>();
        for (String name : requestedRoles) {
            if ("ROLE_ADMIN".equalsIgnoreCase(name) && !adminAllowed) {
                continue;
            }
            roleRepository.findByName(name.toUpperCase()).ifPresentOrElse(
                    roles::add,
                    () -> roles.add(defaultRole)
            );
        }
        return roles.isEmpty() ? Set.of(defaultRole) : roles;
    }
}