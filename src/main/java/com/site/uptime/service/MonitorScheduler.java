package com.site.uptime.service;

import com.site.uptime.domain.Monitor;
import com.site.uptime.repository.MonitorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Wakes up on a fixed cadence and runs any monitor whose per-monitor interval has elapsed.
 * The tick interval is the resolution; each monitor still honours its own intervalSeconds.
 */
@Component
public class MonitorScheduler {

    private static final Logger log = LoggerFactory.getLogger(MonitorScheduler.class);

    private final MonitorRepository monitorRepository;
    private final MonitoringService monitoringService;

    public MonitorScheduler(MonitorRepository monitorRepository, MonitoringService monitoringService) {
        this.monitorRepository = monitorRepository;
        this.monitoringService = monitoringService;
    }

    @Scheduled(fixedDelayString = "${app.scheduler.tick-ms:15000}")
    public void tick() {
        Instant now = Instant.now();
        List<Monitor> due = monitorRepository.findByEnabledTrue().stream()
                .filter(m -> m.isDue(now))
                .toList();

        if (due.isEmpty()) return;
        log.debug("Running {} due monitor(s)", due.size());
        for (Monitor m : due) {
            try {
                monitoringService.runCheck(m);
            } catch (Exception e) {
                log.error("Check failed for monitor {} ({})", m.getId(), m.getName(), e);
            }
        }
    }
}
