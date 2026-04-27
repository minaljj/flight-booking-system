package com.flightapp.user_service.service;

import com.flightapp.user_service.dto.JwtResponse;
import com.flightapp.user_service.dto.LoginRequest;
import com.flightapp.user_service.dto.MessageResponse;
import com.flightapp.user_service.dto.SignupRequest;
import com.flightapp.user_service.dto.TokenRefreshRequest;
import com.flightapp.user_service.model.AuditLog;
import com.flightapp.user_service.model.RefreshToken;
import com.flightapp.user_service.model.Role;
import com.flightapp.user_service.model.RoleName;
import com.flightapp.user_service.model.User;
import com.flightapp.user_service.repository.AuditLogRepository;
import com.flightapp.user_service.repository.RoleRepository;
import com.flightapp.user_service.repository.UserRepository;
import com.flightapp.user_service.security.JwtUtils;
import com.flightapp.user_service.security.UserDetailsImplementation;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Date;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AuditLogRepository auditLogRepository;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImplementation userDetails = (UserDetailsImplementation) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        refreshTokenService.deleteByUserId(userDetails.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        auditLogRepository.save(new AuditLog("LOGIN", userDetails.getUsername(), "User logged in successfully"));
        return new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(), userDetails.getUsername(),
                userDetails.getEmail(), roles);
    }

    public void registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())
                || userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException();
        }
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));
        Set<String> requestedRoles = signUpRequest.getRoles();
        Set<Role> roles = resolveRoles(requestedRoles, false);
        user.setRoles(roles);
        userRepository.save(user);
        auditLogRepository.save(new AuditLog("REGISTER", user.getUsername(), "User registered successfully"));
    }

    public JwtResponse refreshJwtToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    List<String> roles = user.getRoles().stream().map(role -> role.getName().name())
                            .collect(Collectors.toList());
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername(), roles);
                    return new JwtResponse(token, requestRefreshToken, user.getId(), user.getUsername(),
                            user.getEmail(), roles);
                })
                .orElseThrow(() -> new RuntimeException());
    }

    public void logoutUser(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String jwt = headerAuth.substring(7);
            try {
                Date expiryDate = jwtUtils.getExpirationDateFromJwtToken(jwt);
                long ttl = expiryDate.getTime() - System.currentTimeMillis();
                tokenBlacklistService.blacklistToken(jwt, ttl);
            } catch (Exception e) {
                tokenBlacklistService.blacklistToken(jwt, 15 * 60 * 1000); // Default 15 mins
            }
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImplementation) {
            UserDetailsImplementation userDetails = (UserDetailsImplementation) auth.getPrincipal();
            refreshTokenService.deleteByUserId(userDetails.getId());
            auditLogRepository.save(new AuditLog("LOGOUT", userDetails.getUsername(), "User logged out"));
        }
        SecurityContextHolder.clearContext();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public MessageResponse blockUser(String username, boolean block, String adminName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException());
        if (username.equals(adminName)) {
            throw new RuntimeException();
        }
        user.setBlocked(block);
        userRepository.save(user);
        if (block) {
            tokenBlacklistService.blockUser(username);
            refreshTokenService.deleteByUserId(user.getId());
        } else {
            tokenBlacklistService.unblockUser(username);
        }
        String action = block ? "BLOCK_USER" : "UNBLOCK_USER";
        auditLogRepository.save(new AuditLog(action, username, "User " + action + " by admin " + adminName));
        logger.info("AUDIT: Admin [{}] {} user [{}]", adminName, action, username);
        return new MessageResponse("User " + (block ? "blocked" : "unblocked") + " successfully.");
    }

    private Set<Role> resolveRoles(Set<String> requestedRoles, boolean adminAllowed) {
        Role defaultRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException());
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            return Set.of(defaultRole);
        }
        Set<Role> roles = new HashSet<>();
        for (String strName : requestedRoles) {
            try {
                RoleName roleName = RoleName.valueOf(strName.toUpperCase());
                if (roleName == RoleName.ROLE_ADMIN && !adminAllowed) {
                    continue;
                }
                roleRepository.findByName(roleName).ifPresentOrElse(
                        roles::add,
                        () -> roles.add(defaultRole));
            } catch (IllegalArgumentException e) {
                roles.add(defaultRole);
            }
        }
        return roles.isEmpty() ? Set.of(defaultRole) : roles;
    }
}
