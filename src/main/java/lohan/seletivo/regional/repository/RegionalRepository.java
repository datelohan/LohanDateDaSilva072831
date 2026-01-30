package lohan.seletivo.regional.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lohan.seletivo.regional.model.Regional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionalRepository extends JpaRepository<Regional, Long> {

    List<Regional> findByAtivoTrue();

    List<Regional> findByAtivo(Boolean ativo);

    List<Regional> findByRegionalIdInAndAtivoTrue(Collection<Integer> ids);

    Optional<Regional> findByRegionalIdAndAtivoTrue(Integer id);
}
