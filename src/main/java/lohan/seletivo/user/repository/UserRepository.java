package lohan.seletivo.user.repository;

import java.util.Optional;
import lohan.seletivo.user.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUsername(String username);
}
