package lohan.seletivo.auth.repository;

import java.util.Optional;
import lohan.seletivo.auth.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
}
