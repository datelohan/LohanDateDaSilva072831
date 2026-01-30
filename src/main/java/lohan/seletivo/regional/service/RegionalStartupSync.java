package lohan.seletivo.regional.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

@Component
public class RegionalStartupSync {

    private static final Logger log = LoggerFactory.getLogger(RegionalStartupSync.class);

    private final RegionalSyncService regionalSyncService;

    public RegionalStartupSync(RegionalSyncService regionalSyncService) {
        this.regionalSyncService = regionalSyncService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void syncOnStartup() {
        try {
            regionalSyncService.sync();
            log.info("Regionais sincronizadas no startup.");
        } catch (Exception ex) {
            log.warn("Falha ao sincronizar regionais no startup. Aplicacao continua.", ex);
        }
    }
}
