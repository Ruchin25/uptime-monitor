package com.site.uptime.service;

import com.site.uptime.domain.CheckResult;
import com.site.uptime.domain.Monitor;
import com.site.uptime.domain.MonitorType;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises the real HTTP/TCP probing logic against a local in-JVM server.
 * No Postgres and no internet required, so it runs anywhere including CI.
 */
class HealthCheckServiceTest {

    private final HealthCheckService service = new HealthCheckService();
    private HttpServer server;
    private int port;

    @BeforeEach
    void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/ok", exchange -> respond(exchange, 200));
        server.createContext("/boom", exchange -> respond(exchange, 500));
        server.start();
        port = server.getAddress().getPort();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    @Test
    void httpMonitor_reports_up_on_expected_status() {
        Monitor m = new Monitor("ok", MonitorType.HTTP,
                "http://localhost:" + port + "/ok", 60, 200, 2000);

        CheckResult result = service.check(m);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getLatencyMs()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void httpMonitor_reports_down_on_unexpected_status() {
        Monitor m = new Monitor("boom", MonitorType.HTTP,
                "http://localhost:" + port + "/boom", 60, 200, 2000);

        CheckResult result = service.check(m);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(500);
        assertThat(result.getMessage()).contains("500");
    }

    @Test
    void tcpMonitor_reports_up_when_port_is_open() {
        Monitor m = new Monitor("tcp", MonitorType.TCP,
                "localhost:" + port, 60, null, 2000);

        CheckResult result = service.check(m);

        assertThat(result.isSuccess()).isTrue();
    }

    private static void respond(com.sun.net.httpserver.HttpExchange exchange, int code) throws java.io.IOException {
        byte[] body = "hello".getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }
}
