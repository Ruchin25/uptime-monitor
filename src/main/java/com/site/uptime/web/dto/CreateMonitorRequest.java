package com.site.uptime.web.dto;

import com.site.uptime.domain.MonitorType;
import jakarta.validation.constraints.*;

public record CreateMonitorRequest(
        @NotBlank String name,
        @NotNull MonitorType type,
        @NotBlank String target,
        @Min(5) int intervalSeconds,
        Integer expectedStatus,
        @Min(100) int timeoutMs
) {
    public CreateMonitorRequest {
        if (intervalSeconds == 0) intervalSeconds = 60;
        if (timeoutMs == 0) timeoutMs = 5000;
        if (expectedStatus == null && type == MonitorType.HTTP) expectedStatus = 200;
    }
}
