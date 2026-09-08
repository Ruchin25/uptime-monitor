package com.site.uptime.web.dto;

import com.site.uptime.domain.Monitor;
import com.site.uptime.domain.MonitorType;

import java.time.Instant;

public record MonitorResponse(
        Long id,
        String name,
        MonitorType type,
        String target,
        int intervalSeconds,
        Integer expectedStatus,
        int timeoutMs,
        boolean enabled,
        Instant lastCheckedAt
) {
    public static MonitorResponse from(Monitor m) {
        return new MonitorResponse(m.getId(), m.getName(), m.getType(), m.getTarget(),
                m.getIntervalSeconds(), m.getExpectedStatus(), m.getTimeoutMs(),
                m.isEnabled(), m.getLastCheckedAt());
    }
}
