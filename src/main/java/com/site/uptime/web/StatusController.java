package com.site.uptime.web;

import com.site.uptime.domain.CheckResult;
import com.site.uptime.domain.Monitor;
import com.site.uptime.repository.CheckResultRepository;
import com.site.uptime.repository.MonitorRepository;
import com.site.uptime.web.dto.StatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Read model for the dashboard: current status of every monitor. */
@RestController
@RequestMapping("/api/status")
public class StatusController {

    private final MonitorRepository monitorRepository;
    private final CheckResultRepository checkResultRepository;

    public StatusController(MonitorRepository monitorRepository, CheckResultRepository checkResultRepository) {
        this.monitorRepository = monitorRepository;
        this.checkResultRepository = checkResultRepository;
    }

    @GetMapping
    public List<StatusResponse> all() {
        return monitorRepository.findAll().stream()
                .map(this::toStatus)
                .toList();
    }

    private StatusResponse toStatus(Monitor m) {
        CheckResult latest = checkResultRepository
                .findFirstByMonitorIdOrderByCheckedAtDesc(m.getId())
                .orElse(null);
        return StatusResponse.of(m, latest);
    }
}
