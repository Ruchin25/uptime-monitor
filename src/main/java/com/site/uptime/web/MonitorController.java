package com.site.uptime.web;

import com.site.uptime.domain.Monitor;
import com.site.uptime.repository.MonitorRepository;
import com.site.uptime.service.MonitoringService;
import com.site.uptime.web.dto.CreateMonitorRequest;
import com.site.uptime.web.dto.MonitorResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/monitors")
public class MonitorController {

    private final MonitorRepository monitorRepository;
    private final MonitoringService monitoringService;

    public MonitorController(MonitorRepository monitorRepository, MonitoringService monitoringService) {
        this.monitorRepository = monitorRepository;
        this.monitoringService = monitoringService;
    }

    @GetMapping
    public List<MonitorResponse> list() {
        return monitorRepository.findAll().stream().map(MonitorResponse::from).toList();
    }

    @GetMapping("/{id}")
    public MonitorResponse get(@PathVariable Long id) {
        return MonitorResponse.from(find(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MonitorResponse create(@Valid @RequestBody CreateMonitorRequest req) {
        Monitor monitor = new Monitor(req.name(), req.type(), req.target(),
                req.intervalSeconds(), req.expectedStatus(), req.timeoutMs());
        return MonitorResponse.from(monitorRepository.save(monitor));
    }

    /** Trigger an immediate check instead of waiting for the scheduler — handy for demos. */
    @PostMapping("/{id}/check")
    public ResponseEntity<Void> checkNow(@PathVariable Long id) {
        monitoringService.runCheck(find(id));
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{id}/enabled")
    public MonitorResponse setEnabled(@PathVariable Long id, @RequestParam boolean value) {
        Monitor m = find(id);
        m.setEnabled(value);
        return MonitorResponse.from(monitorRepository.save(m));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        monitorRepository.delete(find(id));
    }

    private Monitor find(Long id) {
        return monitorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Monitor " + id + " not found"));
    }
}
