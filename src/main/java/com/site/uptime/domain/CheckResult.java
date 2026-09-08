package com.site.uptime.domain;

import jakarta.persistence.*;
import java.time.Instant;

/** One probe of one monitor: was it up, what did it return, how long did it take. */
@Entity
@Table(name = "check_results")
public class CheckResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "monitor_id", nullable = false)
    private Monitor monitor;

    @Column(name = "checked_at", nullable = false)
    private Instant checkedAt;

    @Column(nullable = false)
    private boolean success;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "latency_ms", nullable = false)
    private long latencyMs;

    @Column(length = 500)
    private String message;

    protected CheckResult() { }

    public CheckResult(Monitor monitor, Instant checkedAt, boolean success,
                       Integer statusCode, long latencyMs, String message) {
        this.monitor = monitor;
        this.checkedAt = checkedAt;
        this.success = success;
        this.statusCode = statusCode;
        this.latencyMs = latencyMs;
        this.message = message;
    }

    public Long getId() { return id; }
    public Monitor getMonitor() { return monitor; }
    public Instant getCheckedAt() { return checkedAt; }
    public boolean isSuccess() { return success; }
    public Integer getStatusCode() { return statusCode; }
    public long getLatencyMs() { return latencyMs; }
    public String getMessage() { return message; }
}
