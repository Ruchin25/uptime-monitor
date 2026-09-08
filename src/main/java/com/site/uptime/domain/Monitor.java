package com.site.uptime.domain;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * A thing we watch: a URL (HTTP) or a host:port (TCP), plus how often to check it
 * and what "healthy" means.
 */
@Entity
@Table(name = "monitors")
public class Monitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private MonitorType type;

    /** For HTTP: a full URL (https://example.com). For TCP: host:port (db.internal:5432). */
    @Column(nullable = false)
    private String target;

    @Column(name = "interval_seconds", nullable = false)
    private int intervalSeconds = 60;

    /** Expected HTTP status; ignored for TCP monitors. */
    @Column(name = "expected_status")
    private Integer expectedStatus = 200;

    @Column(name = "timeout_ms", nullable = false)
    private int timeoutMs = 5000;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "last_checked_at")
    private Instant lastCheckedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Monitor() { }

    public Monitor(String name, MonitorType type, String target,
                   int intervalSeconds, Integer expectedStatus, int timeoutMs) {
        this.name = name;
        this.type = type;
        this.target = target;
        this.intervalSeconds = intervalSeconds;
        this.expectedStatus = expectedStatus;
        this.timeoutMs = timeoutMs;
    }

    /** True if enough time has passed since the last check (or it has never run). */
    public boolean isDue(Instant now) {
        if (!enabled) return false;
        if (lastCheckedAt == null) return true;
        return lastCheckedAt.plusSeconds(intervalSeconds).isBefore(now);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public MonitorType getType() { return type; }
    public String getTarget() { return target; }
    public int getIntervalSeconds() { return intervalSeconds; }
    public Integer getExpectedStatus() { return expectedStatus; }
    public int getTimeoutMs() { return timeoutMs; }
    public boolean isEnabled() { return enabled; }
    public Instant getLastCheckedAt() { return lastCheckedAt; }
    public Instant getCreatedAt() { return createdAt; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setLastCheckedAt(Instant lastCheckedAt) { this.lastCheckedAt = lastCheckedAt; }
}
