package com.site.uptime.service;

import com.site.uptime.domain.CheckResult;
import com.site.uptime.domain.Monitor;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

/**
 * Performs a single probe of a monitor and turns it into a CheckResult.
 * Uses the JDK's built-in HttpClient (HTTP) and Socket (TCP) — no extra dependencies.
 */
@Service
public class HealthCheckService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public CheckResult check(Monitor monitor) {
        return switch (monitor.getType()) {
            case HTTP -> checkHttp(monitor);
            case TCP -> checkTcp(monitor);
        };
    }

    private CheckResult checkHttp(Monitor monitor) {
        Instant start = Instant.now();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(monitor.getTarget()))
                    .timeout(Duration.ofMillis(monitor.getTimeoutMs()))
                    .GET()
                    .build();

            HttpResponse<Void> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.discarding());

            long latency = elapsedMs(start);
            int status = response.statusCode();
            int expected = monitor.getExpectedStatus() != null ? monitor.getExpectedStatus() : 200;
            boolean ok = status == expected;

            String message = ok ? "OK" : "Expected " + expected + " but got " + status;
            return new CheckResult(monitor, start, ok, status, latency, message);

        } catch (Exception e) {
            return new CheckResult(monitor, start, false, null, elapsedMs(start), reason(e));
        }
    }

    private CheckResult checkTcp(Monitor monitor) {
        Instant start = Instant.now();
        HostPort hp;
        try {
            hp = HostPort.parse(monitor.getTarget());
        } catch (IllegalArgumentException e) {
            return new CheckResult(monitor, start, false, null, 0, e.getMessage());
        }

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(hp.host(), hp.port()), monitor.getTimeoutMs());
            return new CheckResult(monitor, start, true, null, elapsedMs(start), "OK");
        } catch (Exception e) {
            return new CheckResult(monitor, start, false, null, elapsedMs(start), reason(e));
        }
    }

    private static long elapsedMs(Instant start) {
        return Duration.between(start, Instant.now()).toMillis();
    }

    private static String reason(Exception e) {
        String msg = e.getMessage();
        return e.getClass().getSimpleName() + (msg != null ? ": " + msg : "");
    }

    /** Parses "host:port" for TCP monitors. */
    record HostPort(String host, int port) {
        static HostPort parse(String target) {
            int idx = target.lastIndexOf(':');
            if (idx <= 0 || idx == target.length() - 1) {
                throw new IllegalArgumentException("TCP target must be host:port, got: " + target);
            }
            try {
                return new HostPort(target.substring(0, idx), Integer.parseInt(target.substring(idx + 1)));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid port in TCP target: " + target);
            }
        }
    }
}
