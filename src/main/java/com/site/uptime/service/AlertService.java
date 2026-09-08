package com.site.uptime.service;

import com.site.uptime.domain.CheckResult;
import com.site.uptime.domain.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Fires when a monitor changes state (up->down or down->up).
 * Skeleton logs the alert; this is the extension point for email, Slack, or a webhook.
 */
@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    public void onStateChange(Monitor monitor, CheckResult result) {
        if (result.isSuccess()) {
            log.warn("RECOVERED: '{}' ({}) is back UP after {} ms",
                    monitor.getName(), monitor.getTarget(), result.getLatencyMs());
        } else {
            log.error("ALERT: '{}' ({}) is DOWN — {}",
                    monitor.getName(), monitor.getTarget(), result.getMessage());
        }
        // TODO: send email / Slack / webhook here.
    }
}
