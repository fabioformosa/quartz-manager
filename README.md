<div align="center">

# Quartz Manager

**A Spring Boot library and standalone web app that adds REST API and dashboard management to Quartz Scheduler.**

[![Java CI with Maven](https://github.com/fabioformosa/quartz-manager/actions/workflows/maven.yml/badge.svg)](https://github.com/fabioformosa/quartz-manager/actions/workflows/maven.yml)
[![npm CI](https://github.com/fabioformosa/quartz-manager/actions/workflows/npm.yml/badge.svg)](https://github.com/fabioformosa/quartz-manager/actions/workflows/npm.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=fabioformosa_quartz-manager&metric=coverage)](https://sonarcloud.io/summary/new_code?id=fabioformosa_quartz-manager)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=fabioformosa_quartz-manager&metric=bugs)](https://sonarcloud.io/summary/new_code?id=fabioformosa_quartz-manager)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=fabioformosa_quartz-manager&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=fabioformosa_quartz-manager)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=fabioformosa_quartz-manager&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=fabioformosa_quartz-manager)

[Choose Your Path](#choose-your-path) • [Features](#features) • [Quick Start](#quick-start) • [REST API](#rest-api) • [Security](#security) • [Persistence](#persistence) • [Roadmap](#roadmap)

</div>

[Quartz Scheduler](https://www.quartz-scheduler.org/) is powerful, but it does not ship with a REST API or an operations dashboard. Quartz Manager fills that gap for Spring Boot applications and can also run as a standalone scheduler web app.

Use it to start and stop a scheduler, create jobs, schedule triggers, manage calendars, inspect executions, and monitor job progress from HTTP endpoints or from a browser UI.

![Quartz Manager dashboard](https://github.com/fabioformosa/quartz-manager/blob/master/docs/assets/quartz-manager-dashboard.png)

## Choose Your Path

### 1. Add API And UI To Your Existing App

Use this path when you already have a Spring Boot application and want to add a Quartz management API, an embedded management panel, or both.

Current behavior: Quartz Manager creates and manages its own scheduler bean named `quartzManagerScheduler` by default. It can coexist with other Quartz schedulers in the same application, but it does not automatically take control of an arbitrary existing scheduler instance.

If you want Quartz Manager to manage an existing scheduler today, disable the default scheduler configuration with `quartz-manager.quartz.enabled=false` and provide a compatible bean named `quartzManagerScheduler`. First-class existing-scheduler integration is planned on the roadmap.

Your managed jobs must extend `AbstractQuartzManagerJob` so Quartz Manager can expose them as eligible jobs and stream their execution logs/progress to the UI.

If you also want the browser dashboard, see [Add The UI](#add-the-ui).

### 2. Add A New Scheduler To Your App

Use this path when your Spring Boot application does not have Quartz yet and you want to add a scheduler managed by Quartz Manager.

The easiest setup is to let Quartz Manager import, initialize, and manage a Quartz Scheduler for you. Import the API starter, create jobs extending `AbstractQuartzManagerJob`, configure the package that contains your jobs, and use the REST API or UI to create jobs and triggers.

You can later add optional modules for the embedded UI, JWT security, and PostgreSQL persistence.

If you also want the browser dashboard, see [Add The UI](#add-the-ui).

### 3. Run Quartz Manager As A Standalone App

Use this path when you want a standalone scheduler web application instead of embedding Quartz Manager into an existing product.

The `quartz-manager-web-showcase` application imports Quartz Manager API, UI, and security modules and runs with an embedded Quartz scheduler. It is useful as a ready-to-run management console, as a demo, and as a reference application.

Even in standalone mode, the jobs managed by Quartz Manager must extend `AbstractQuartzManagerJob`.

## Features

- REST API for scheduler, job, trigger, and calendar management.
- Embeddable management UI provided as a webjar: import it as a Maven dependency and open `/quartz-manager-ui` in the browser.
- Scheduler commands: start, standby, resume, and shutdown.
- Job management: list eligible job classes, create stored jobs, update jobs, delete jobs, and trigger jobs manually.
- Trigger management: create, inspect, reschedule, pause, resume, and unschedule triggers.
- Trigger types: simple, cron, daily time interval, and calendar interval.
- Calendar management: annual, cron, daily, holiday, monthly, and weekly calendars.
- Misfire handling for supported trigger types.
- WebSocket updates for job execution progress and logs.
- Optional OpenAPI/Swagger UI documentation.
- Optional JWT-based security with in-memory users.
- Optional PostgreSQL persistence using Quartz JDBC job store and Liquibase-managed schema creation.

In dependency snippets, replace `VERSION` with the version you want to use.

## Requirements

- Java 21+
- Spring Boot 4.x
- Maven 3.9+
- Node.js and npm only if you build the frontend locally

## Modules

| Module | Required | Purpose |
| --- | --- | --- |
| `quartz-manager-starter-api` | Required | REST API, managed scheduler integration, jobs, triggers, calendars, OpenAPI support, and WebSocket updates |
| `quartz-manager-starter-ui` | Optional | Embeddable management UI provided as a webjar |
| `quartz-manager-starter-security` | Optional | JWT authentication for Quartz Manager API and UI |
| `quartz-manager-starter-persistence` | Optional | PostgreSQL-backed Quartz persistence and Liquibase schema setup |
| `quartz-manager-web-showcase` | Optional | Standalone/demo Spring Boot application using the Quartz Manager modules |

## Quick Start

### Path 1: Existing Spring Boot App

Add the API starter:

```xml
<dependency>
  <groupId>it.fabioformosa.quartz-manager</groupId>
  <artifactId>quartz-manager-starter-api</artifactId>
  <version>VERSION</version>
</dependency>
```

Create jobs by extending `AbstractQuartzManagerJob`:

```java
import it.fabioformosa.quartzmanager.api.jobs.AbstractQuartzManagerJob;
import it.fabioformosa.quartzmanager.api.jobs.entities.LogRecord;
import org.quartz.JobExecutionContext;

public class SampleJob extends AbstractQuartzManagerJob {

  @Override
  public LogRecord doIt(JobExecutionContext context) {
    return new LogRecord(LogRecord.LogType.INFO, "Hello from Quartz Manager");
  }
}
```

Configure job discovery:

```properties
quartz-manager.jobClassPackages=com.example.jobs
quartz-manager.oas.enabled=true
```

By default, Quartz Manager creates a dedicated scheduler named `quartz-manager-scheduler`. If your app already has another Quartz scheduler, both can coexist.

Advanced existing-scheduler setup:

```properties
quartz-manager.quartz.enabled=false
```

Then provide a `Scheduler` bean named `quartzManagerScheduler`. This is the current integration point; a more explicit existing-scheduler mode is planned.

To add the browser dashboard to your application, see [Add The UI](#add-the-ui).

### Path 2: New Scheduler In Your App

Use the same API starter setup as Path 1, then let Quartz Manager create its managed scheduler.

Create one or more jobs extending `AbstractQuartzManagerJob`, configure `quartz-manager.jobClassPackages`, then create stored jobs and triggers through the REST API, Swagger UI, or the dashboard.

Default managed Quartz properties:

```properties
org.quartz.scheduler.instanceName=quartz-manager-scheduler
org.quartz.threadPool.threadCount=1
```

To customize the managed scheduler, add `managed-quartz.properties` to your classpath.

To add the browser dashboard to your application, see [Add The UI](#add-the-ui).

### Path 3: Standalone Quartz Manager App

Run the standalone showcase application when you want Quartz Manager as a ready-to-use scheduler web app.

```bash
git clone https://github.com/fabioformosa/quartz-manager.git
cd quartz-manager/quartz-manager-parent
mvn install -Pbuild-webjar
cd quartz-manager-web-showcase
mvn spring-boot:run
```

Open the dashboard:

```text
http://localhost:8080/quartz-manager-ui/index.html
```

Open Swagger UI when OpenAPI is enabled:

```text
http://localhost:8080/swagger-ui/index.html
```

Default showcase credentials:

```text
admin / admin
```

To plug in your own jobs today, add your job classes inside the cloned repository, rebuild the standalone application, and configure `quartz-manager.jobClassPackages` to include their package.

A Docker-based standalone distribution is planned. It will provide a supported mechanism to attach external job classes without modifying the cloned repository.

## Add The UI

Add the UI starter when you want the embedded management panel in your Spring Boot app:

```xml
<dependency>
  <groupId>it.fabioformosa.quartz-manager</groupId>
  <artifactId>quartz-manager-starter-ui</artifactId>
  <version>VERSION</version>
</dependency>
```

The UI is served from:

```text
http://localhost:8080/quartz-manager-ui/index.html
```

## REST API

Quartz Manager exposes its API under `/quartz-manager`.

| Area | Endpoints |
| --- | --- |
| Scheduler | `GET /quartz-manager/scheduler`, `POST /quartz-manager/scheduler/start`, `POST /quartz-manager/scheduler/standby`, `POST /quartz-manager/scheduler/resume`, `POST /quartz-manager/scheduler/shutdown` |
| Job classes | `GET /quartz-manager/job-classes` |
| Jobs | `GET /quartz-manager/jobs`, `POST /quartz-manager/jobs/{group}/{name}`, `PUT /quartz-manager/jobs/{group}/{name}`, `POST /quartz-manager/jobs/{group}/{name}/trigger`, `DELETE /quartz-manager/jobs/{group}/{name}` |
| Triggers | `GET /quartz-manager/triggers`, `POST /quartz-manager/triggers/{group}/{name}`, `PUT /quartz-manager/triggers/{group}/{name}`, `POST /quartz-manager/triggers/{group}/{name}/pause`, `POST /quartz-manager/triggers/{group}/{name}/resume`, `DELETE /quartz-manager/triggers/{group}/{name}` |
| Calendars | `GET /quartz-manager/calendars`, `POST /quartz-manager/calendars/{name}`, `PUT /quartz-manager/calendars/{name}`, `DELETE /quartz-manager/calendars/{name}`, `POST /quartz-manager/calendars/{name}/included-time-test` |

Enable OpenAPI and Swagger UI with:

```properties
quartz-manager.oas.enabled=true
```

Then open:

```text
http://localhost:8080/swagger-ui.html
```

## Security

Add the security starter when you want Quartz Manager API and UI protected by JWT authentication:

```xml
<dependency>
  <groupId>it.fabioformosa.quartz-manager</groupId>
  <artifactId>quartz-manager-starter-security</artifactId>
  <version>VERSION</version>
</dependency>
```

Example configuration:

```yaml
quartz-manager:
  security:
    jwt:
      secret: "change-me"
      expiration-in-sec: 28800
      header-strategy:
        enabled: true
        header: Authorization
      cookie-strategy:
        enabled: false
        cookie: AUTH-TOKEN
    accounts:
      in-memory:
        enabled: true
        users:
          - username: admin
            password: admin
            roles:
              - ADMIN
```

Security is applied to `/quartz-manager/**`. The UI webjar path is ignored by the security filter chain, while API calls require authentication.

## Persistence

By default, Quartz Manager uses Quartz's in-memory job store. Scheduling data is lost when the application stops.

Add the persistence starter when you want Quartz Manager's managed scheduler to use PostgreSQL-backed Quartz persistence:

```xml
<dependency>
  <groupId>it.fabioformosa.quartz-manager</groupId>
  <artifactId>quartz-manager-starter-persistence</artifactId>
  <version>VERSION</version>
</dependency>
```

Configure the Quartz Manager datasource:

```yaml
quartz-manager:
  persistence:
    quartz:
      datasource:
        url: jdbc:postgresql://localhost:5432/quartzmanager
        user: quartzmanager
        password: quartzmanager
```

The persistence module configures Quartz `JobStoreTX`, uses the PostgreSQL delegate, and creates the required Quartz tables through Liquibase.

## Examples

Example integrations are available in [quartz-manager-use-cases](https://github.com/fabioformosa/quartz-manager-use-cases).

The use cases cover simple Spring applications, secured and unsecured setups, existing application security, existing Quartz scenarios, and persistence.

## Current Limitations

- Quartz Manager creates and manages its own scheduler by default. Automatic discovery and first-class management of an arbitrary existing scheduler is not yet supported.
- Existing applications that want Quartz Manager to manage a pre-existing scheduler must currently provide it as a bean named `quartzManagerScheduler` and disable Quartz Manager's default scheduler creation.
- Persistence currently targets PostgreSQL.
- Cluster mode is not currently documented as a supported production mode.
- Managed jobs must extend `AbstractQuartzManagerJob` to be eligible for job discovery and UI log/progress streaming.

## Roadmap

The next priorities are tracked in the [project roadmap](https://github.com/users/fabioformosa/projects/1).

Planned improvements include:

- First-class support for managing an existing Quartz Scheduler instance.
- Cluster mode support.
- Additional persistence targets beyond PostgreSQL.
- OAuth2 client support.
- Continued UI improvements.

## Development

This repository contains the backend modules and the frontend application.

For local development, repository structure, build commands, and contribution details, see [quartz-manager-parent/README.md](https://github.com/fabioformosa/quartz-manager/blob/develop/quartz-manager-parent/README.md).

## Contributing

Contributions are welcome. Open an issue to discuss bugs, questions, or feature proposals before starting larger changes.

## License

Quartz Manager is released under the [Apache License 2.0](LICENSE).

## Support

If Quartz Manager is useful to you, consider starring the repository to support the project.
