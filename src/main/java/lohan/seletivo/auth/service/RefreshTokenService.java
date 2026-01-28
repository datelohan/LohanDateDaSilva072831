package lohan.seletivo.auth.service;

import java.time.OffsetDateTime;
import lohan.seletivo.auth.model.RefreshToken;
import lohan.seletivo.auth.repository.RefreshTokenRepository;
import lohan.seletivo.security.JwtService;
import lohan.seletivo.user.model.UserAccount;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final TokenHashService tokenHashService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            TokenHashService tokenHashService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.tokenHashService = tokenHashService;
    }

    public RefreshToken create(UserAccount user, String token, OffsetDateTime expiresAt) {
        String tokenId = jwtService.extractTokenId(token);
        if (tokenId == null || tokenId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalido");
        }
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenId(tokenId);
        refreshToken.setTokenHash(tokenHashService.hash(token));
        refreshToken.setExpiresAt(expiresAt);
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validate(String token) {
        String tokenId = jwtService.extractTokenId(token);
        if (tokenId == null || tokenId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalido");
        }
        RefreshToken refreshToken = refreshTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalido"));

        if (refreshToken.getRevokedAt() != null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token revogado");
        }
        if (refreshToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expirado");
        }
        if (!tokenHashService.matches(token, refreshToken.getTokenHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalido");
        }

        return refreshToken;
    }

    public RefreshToken revoke(RefreshToken token) {
        token.setRevokedAt(OffsetDateTime.now());
        return refreshTokenRepository.save(token);
    }
}
