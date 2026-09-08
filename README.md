# Uptime Monitor

A self-hosted uptime & infrastructure monitoring service. You register targets
(URLs or `host:port`), the service probes them on a schedule, records latency and
status over time, and alerts when something changes state. It also exposes its own
health and metrics through Actuator/Prometheus — the same observability stack real
backend teams run in production.

Built to demonstrate the current industry-standard Java backend stack.

---

## Easiest way to run — just Java, no Docker

This path uses an embedded database (H2), so there is **nothing to install except a
Java kit**. No Docker, no Postgres.

### 1. Install a Java Development Kit (JDK 21)

Download **Eclipse Temurin 21 (.msi)** for Windows from https://adoptium.net.
Run the installer and, on the "Custom Setup" screen, enable **"Add to PATH"** and
**"Set JAVA_HOME variable"** (click each and choose "Will be installed"). Finish the
install, then open a **new** PowerShell window and confirm:

```
java -version
```

It should print `openjdk version "21..."`.

### 2. Run the app

Open the project folder in File Explorer (the one containing `mvnw.cmd`), click the
address bar, type `powershell`, and press Enter. Then run:

```
.\mvnw.cmd spring-boot:run
```

The first run downloads Maven and the project libraries, so give it a few minutes.
You're done when you see a line like `Started UptimeMonitorApplication`. Leave the
window open. The app is now at http://localhost:8080.

Stop it any time with `Ctrl+C`.

---

## Try it

Open a **second** PowerShell window and add a monitor:

```
curl -X POST http://localhost:8080/api/monitors -H "Content-Type: application/json" -d '{\"name\":\"Google\",\"type\":\"HTTP\",\"target\":\"https://www.google.com\",\"intervalSeconds\":30,\"expectedStatus\":200,\"timeoutMs\":3000}'
```

Force an immediate check, then read the dashboard status:

```
curl -X POST http://localhost:8080/api/monitors/1/check
curl http://localhost:8080/api/status
```

You can also open http://localhost:8080/h2-console in a browser to see the data
(JDBC URL: `jdbc:h2:file:./data/uptime`, user `sa`, no password).

## Endpoints

| Method | Path                          | Purpose                          |
|--------|-------------------------------|----------------------------------|
| GET    | `/api/monitors`               | List all monitors                |
| POST   | `/api/monitors`               | Create a monitor                 |
| POST   | `/api/monitors/{id}/check`    | Run a check now                  |
| PATCH  | `/api/monitors/{id}/enabled?value=false` | Pause/resume a monitor |
| DELETE | `/api/monitors/{id}`          | Delete a monitor                 |
| GET    | `/api/status`                 | Current status of every monitor  |
| GET    | `/actuator/health`            | App health                       |
| GET    | `/actuator/prometheus`        | Metrics for Prometheus to scrape |

## Stack

Java 21, Spring Boot 3.3, Spring Web, Spring Data JPA, Spring Boot Actuator +
Micrometer/Prometheus, JUnit 5. Runs on embedded **H2** by default; ships with a
**PostgreSQL + Flyway + Docker** profile for the production-style setup.

---

## Optional: the Docker / Postgres path

Once you're comfortable, the production-style setup runs Postgres in Docker and the
app against it. Requires Docker Desktop:

```
docker compose up --build
```

This activates the `postgres` profile (Postgres + Flyway migrations) automatically.

## Suggested next steps

1. **Live dashboard** — a React front end reading `/api/status`, with WebSocket/SSE
   so status lights update in real time.
2. **Grafana** — point Prometheus at `/actuator/prometheus` and build dashboards.
3. **Real alert channels** — implement email / Slack / webhook in `AlertService`.
4. **GitHub Actions** — CI that builds and tests on every push.
