package com.site.uptime.web.dto;

import com.site.uptime.domain.CheckResult;
import com.site.uptime.domain.Monitor;

import java.time.Instant;

/** Current status of a monitor for the dashboard: is it up, latency, when last seen. */
public record StatusResponse(
        Long monitorId,
        String name,
        String target,
        String status,       // UP, DOWN, or UNKNOWN (never checked yet)
        Long latencyMs,
        Integer statusCode,
        Instant lastCheckedAt,
        String message
) {
    public static StatusResponse of(Monitor m, CheckResult latest) {
        if (latest == null) {
            return new StatusResponse(m.getId(), m.getName(), m.getTarget(),
                    "UNKNOWN", null, null, null, "Not checked yet");
        }
        return new StatusResponse(m.getId(), m.getName(), m.getTarget(),
                latest.isSuccess() ? "UP" : "DOWN",
                latest.getLatencyMs(), latest.getStatusCode(),
                latest.getCheckedAt(), latest.getMessage());
    }
}
