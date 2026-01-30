package lohan.seletivo.regional.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lohan.seletivo.regional.dto.RegionalApiItem;
import lohan.seletivo.regional.dto.RegionalSyncResult;
import lohan.seletivo.regional.model.Regional;
import lohan.seletivo.regional.repository.RegionalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegionalSyncService {

    private static final String ENDPOINT = "https://integrador-argus-api.geia.vip/v1/regionais";

    private final RegionalRepository regionalRepository;
    private final RestClient restClient;

    public RegionalSyncService(RegionalRepository regionalRepository) {
        this.regionalRepository = regionalRepository;
        this.restClient = RestClient.create();
    }

    @Transactional
    public RegionalSyncResult sync() {
        List<RegionalApiItem> itens = fetchRegionais();
        Map<Integer, String> incoming = new HashMap<>();
        for (RegionalApiItem item : itens) {
            if (item == null || item.id() == null || item.nome() == null) {
                continue;
            }
            incoming.put(item.id(), item.nome());
        }

        Set<Integer> ids = incoming.keySet();
        List<Regional> ativos = regionalRepository.findByRegionalIdInAndAtivoTrue(ids);
        Map<Integer, Regional> ativosPorId = ativos.stream()
                .collect(Collectors.toMap(Regional::getRegionalId, r -> r));

        int inseridos = 0;
        int inativados = 0;
        int atualizados = 0;

        for (Map.Entry<Integer, String> entry : incoming.entrySet()) {
            Integer id = entry.getKey();
            String nome = entry.getValue();
            Regional ativo = ativosPorId.get(id);
            if (ativo == null) {
                regionalRepository.save(novoRegional(id, nome, true));
                inseridos++;
                continue;
            }
            if (!Objects.equals(ativo.getNome(), nome)) {
                ativo.setAtivo(false);
                regionalRepository.save(ativo);
                regionalRepository.save(novoRegional(id, nome, true));
                atualizados++;
            }
        }

        List<Regional> ativosAtual = regionalRepository.findByAtivoTrue();
        Set<Integer> incomingIds = incoming.keySet();
        for (Regional ativo : ativosAtual) {
            if (!incomingIds.contains(ativo.getRegionalId())) {
                ativo.setAtivo(false);
                regionalRepository.save(ativo);
                inativados++;
            }
        }

        return new RegionalSyncResult(inseridos, inativados, atualizados);
    }

    public List<Regional> listar(Boolean ativo) {
        if (ativo == null) {
            return regionalRepository.findAll();
        }
        return regionalRepository.findByAtivo(ativo);
    }

    private List<RegionalApiItem> fetchRegionais() {
        try {
            return restClient.get()
                    .uri(ENDPOINT)
                    .retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<>() {});
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao consultar endpoint de regionais", ex);
        }
    }

    private Regional novoRegional(Integer id, String nome, boolean ativo) {
        Regional regional = new Regional();
        regional.setRegionalId(id);
        regional.setNome(nome);
        regional.setAtivo(ativo);
        return regional;
    }
}
