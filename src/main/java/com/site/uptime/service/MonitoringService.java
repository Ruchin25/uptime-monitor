package com.site.uptime.service;

import com.site.uptime.domain.CheckResult;
import com.site.uptime.domain.Monitor;
import com.site.uptime.repository.CheckResultRepository;
import com.site.uptime.repository.MonitorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/** Orchestrates one check: probe, persist, detect state change, update the monitor. */
@Service
public class MonitoringService {

    private final MonitorRepository monitorRepository;
    private final CheckResultRepository checkResultRepository;
    private final HealthCheckService healthCheckService;
    private final AlertService alertService;

    public MonitoringService(MonitorRepository monitorRepository,
                             CheckResultRepository checkResultRepository,
                             HealthCheckService healthCheckService,
                             AlertService alertService) {
        this.monitorRepository = monitorRepository;
        this.checkResultRepository = checkResultRepository;
        this.healthCheckService = healthCheckService;
        this.alertService = alertService;
    }

    @Transactional
    public CheckResult runCheck(Monitor monitor) {
        Optional<CheckResult> previous =
                checkResultRepository.findFirstByMonitorIdOrderByCheckedAtDesc(monitor.getId());

        CheckResult result = healthCheckService.check(monitor);
        checkResultRepository.save(result);

        monitor.setLastCheckedAt(Instant.now());
        monitorRepository.save(monitor);

        boolean stateChanged = previous.isEmpty() || previous.get().isSuccess() != result.isSuccess();
        if (stateChanged && previous.isPresent()) {
            alertService.onStateChange(monitor, result);
        }
        return result;
    }
}
