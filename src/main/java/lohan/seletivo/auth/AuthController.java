package lohan.seletivo.auth;

import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lohan.seletivo.auth.dto.LoginRequest;
import lohan.seletivo.auth.dto.RefreshRequest;
import lohan.seletivo.auth.dto.TokenResponse;
import lohan.seletivo.auth.model.RefreshToken;
import lohan.seletivo.auth.service.RefreshTokenService;
import lohan.seletivo.security.JwtService;
import lohan.seletivo.security.SecurityProperties;
import lohan.seletivo.user.model.UserAccount;
import lohan.seletivo.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final SecurityProperties securityProperties;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtService jwtService,
            SecurityProperties securityProperties,
            UserRepository userRepository,
            RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.securityProperties = securityProperties;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        UserAccount userAccount = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario invalido"));
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        OffsetDateTime expiresAt = OffsetDateTime.ofInstant(jwtService.extractExpiration(refreshToken), ZoneOffset.UTC);
        refreshTokenService.create(userAccount, refreshToken, expiresAt);
        long expiresInSeconds = securityProperties.getJwt().getAccessTokenTtl().toSeconds();
        return new TokenResponse(accessToken, refreshToken, expiresInSeconds);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        String token = request.refreshToken();
        RefreshToken storedToken = refreshTokenService.validate(token);
        String username = jwtService.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!jwtService.isTokenValid(token, userDetails, "refresh")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalido");
        }
        if (!storedToken.getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalido");
        }

        String accessToken = jwtService.generateAccessToken(userDetails);
        long expiresInSeconds = securityProperties.getJwt().getAccessTokenTtl().toSeconds();
        return new TokenResponse(accessToken, token, expiresInSeconds);
    }
}
