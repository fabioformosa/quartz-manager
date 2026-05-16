<div align="center">

# Quartz Manager

**A Spring Boot library and standalone web app that adds REST API and dashboard management to Quartz Scheduler.**

[![Java CI with Maven](https://github.com/fabioformosa/quartz-manager/actions/workflows/maven.yml/badge.svg)](https://github.com/fabioformosa/quartz-manager/actions/workflows/maven.yml)
[![npm CI](https://github.com/fabioformosa/quartz-manager/actions/workflows/npm.yml/badge.svg)](https://github.com/fabioformosa/quartz-manager/actions/workflows/npm.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=fabioformosa_quartz-manager&metric=coverage)](https://sonarcloud.io/summary/new_code?id=fabioformosa_quartz-manager)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=fabioformosa_quartz-manager&metric=bugs)](https://sonarcloud.io/summary/new_code?id=fabioformosa_quartz-manager)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=fabioformosa_quartz-manager&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=fabioformosa_quartz-manager)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=fabioformosa_quartz-manager&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=fabioformosa_quartz-manager)

[Choose Your Path](#choose-your-path) • [Features](#features) • [Quick Start](#quick-start) • [REST API](#rest-api) • [Scheduler Configuration](#scheduler-configuration) • [Security](#security) • [Persistence](#persistence) • [Roadmap](#roadmap)

</div>

[Quartz Scheduler](https://www.quartz-scheduler.org/) is powerful, but it does not ship with a REST API or an operations dashboard. Quartz Manager fills that gap for Spring Boot applications and can also run as a standalone scheduler web app.

Compatibility note: Quartz Manager targets Java 21 and is compatible with Spring Boot `3.5.x` and `4.0.x`.

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
http://localhost:8080/swagger-ui/index.html
```

## Scheduler Configuration

Quartz Manager APIs always operate on a scheduler bean named `quartzManagerScheduler`.

### Managed Scheduler

By default, Quartz Manager creates that scheduler for you when `quartz-manager-starter-api` is on the classpath.

Default managed Quartz properties:

```properties
org.quartz.scheduler.instanceName=quartz-manager-scheduler
org.quartz.threadPool.threadCount=1
```

The managed scheduler is created through Spring's `SchedulerFactoryBean` with a job factory that autowires Spring beans into Quartz jobs.

To customize the managed scheduler, add a `managed-quartz.properties` file to your application classpath. Any property in that file is merged into the Quartz Manager scheduler configuration and can override the defaults.

Example:

```properties
org.quartz.scheduler.instanceName=my-managed-scheduler
org.quartz.threadPool.threadCount=5
org.quartz.scheduler.isAutoStartup=true
```

Quartz Manager sets `SchedulerFactoryBean.setAutoStartup(true)` only when `org.quartz.scheduler.isAutoStartup=true` is present in the merged Quartz properties.

When `quartz-manager-starter-persistence` is imported, persistence-related Quartz properties are also contributed to the managed scheduler. Application-level `managed-quartz.properties` remains the place for scheduler-specific overrides.

### Existing Scheduler

If your application already has a Quartz scheduler and you want Quartz Manager to operate on that scheduler, disable Quartz Manager's managed scheduler creation:

```properties
quartz-manager.quartz.enabled=false
```

Then expose a compatible `Scheduler` bean named `quartzManagerScheduler`:

```java
@Bean("quartzManagerScheduler")
public Scheduler quartzManagerScheduler() {
  return existingScheduler;
}
```

Quartz Manager services inject that bean by name. The scheduler must be fully configured by the host application, including its job factory, datasource, job store, thread pool, startup behavior, clustering settings, and any Spring Boot Quartz properties.

This is the current integration point for existing schedulers. First-class automatic discovery and management of arbitrary existing scheduler instances is planned on the roadmap.

## Security

Security is optional and depends on how you want Quartz Manager to be embedded.

There are two main models:

| Model | Use When | Owner Of Authentication |
| --- | --- | --- |
| Quartz Manager security starter | You want Quartz Manager to provide login, JWT generation, and API protection | Quartz Manager |
| Host application security | Your app already has Spring Security, OIDC, SSO, or another enterprise security layer | Host application |

### Quartz Manager Security Starter

Add the security starter when you want Quartz Manager API calls protected by Quartz Manager's own JWT flow:

```xml
<dependency>
  <groupId>it.fabioformosa.quartz-manager</groupId>
  <artifactId>quartz-manager-starter-security</artifactId>
  <version>VERSION</version>
</dependency>
```

With this starter, Quartz Manager registers a high-priority Spring Security filter chain for:

```text
/quartz-manager/**
```

The static UI webjar path is ignored by that filter chain:

```text
/quartz-manager-ui/**
```

The UI itself is served as static content, but its API calls to `/quartz-manager/**` require authentication.

Quartz Manager UI is not integrated into the hosting application's frontend. It is a dedicated UI exposed by the backend because `quartz-manager-starter-ui` provides it as a webjar at:

```text
/quartz-manager-ui/index.html
```

When `quartz-manager-starter-security` is enabled, that UI uses Quartz Manager's own login and JWT flow. This JWT is independent from any SSO, OIDC, or OAuth2 access token used by the hosting application.

The security starter provides these auth endpoints:

| Endpoint | Purpose |
| --- | --- |
| `POST /quartz-manager/auth/login` | Authenticates username/password and returns a Quartz Manager JWT |
| `GET /quartz-manager/auth/whoami` | Returns the current authenticated principal from the Spring Security context |
| `POST /quartz-manager/auth/logout` | Runs Quartz Manager logout handling and clears the auth cookie when cookie transport is enabled |

The default login model is Spring Security form login posted to `/quartz-manager/auth/login` with `application/x-www-form-urlencoded` credentials:

```text
username=admin&password=admin
```
NB: customize admin credentials through the in-memory accounts configuration or provide your own user details service.

On success, Quartz Manager generates a JWT. The JWT can be transported through an HTTP header or through a cookie.

Default transport:

```text
Authorization: Bearer <jwt>
```

Cookie transport is also available:

```text
AUTH-TOKEN=<jwt>
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

Header-based JWT login is the default shape:

```properties
quartz-manager.security.jwt.header-strategy.enabled=true
quartz-manager.security.jwt.header-strategy.header=Authorization
quartz-manager.security.jwt.cookie-strategy.enabled=false
```

Cookie-based JWT login can be enabled when you want the browser to carry the JWT as an HTTP-only cookie:

```properties
quartz-manager.security.jwt.header-strategy.enabled=false
quartz-manager.security.jwt.cookie-strategy.enabled=true
quartz-manager.security.jwt.cookie-strategy.cookie=AUTH-TOKEN
```

Quartz Manager can also switch from form-login configuration to a username/password authentication filter:

```properties
quartz-manager.security.login-model.form-login-enabled=false
quartz-manager.security.login-model.userpwd-filter-enabled=true
```

### Host Application Security

If you do not import `quartz-manager-starter-security`, Quartz Manager does not install its own Spring Security filter chain.

In that setup, the REST API endpoints exposed by `quartz-manager-starter-api` under `/quartz-manager/**` are regular Spring MVC endpoints. They can be protected by the hosting application's own Spring Security configuration, just like any other controller in the host application.

The UI is different. `quartz-manager-starter-ui` does not integrate Quartz Manager screens into the hosting application's frontend. It exposes a dedicated Quartz Manager UI from the backend as a webjar:

```text
/quartz-manager-ui/index.html
```

If the hosting application uses cookie/session-based security, the browser may be able to access both `/quartz-manager-ui/**` and `/quartz-manager/**` through the host application's existing cookies, depending on the host Spring Security configuration.

Modern applications are commonly stateless and rely on OIDC/OAuth2 bearer tokens. The current Quartz Manager UI does not integrate with the hosting application's OIDC client and does not automatically obtain or attach a host-issued access token to Quartz Manager API calls.

This is a known UI integration limitation. A change request is tracked to make Quartz Manager UI integrable with the hosting application's security layer, for example through an OIDC-client-aware mode or an external token provider: [#141](https://github.com/fabioformosa/quartz-manager/issues/141).

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

Spring Boot compatibility checks are available in [quartz-manager-compatibility-cases](https://github.com/fabioformosa/quartz-manager-compatibility-cases).

The use cases cover simple Spring applications, secured and unsecured setups, existing application security, existing Quartz scenarios, and persistence. The compatibility cases keep Spring Boot 3.5.x checks separate while the main use cases track Spring Boot 4.0.x.

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
- Quartz Manager UI integration with host application security layers, including OIDC/OAuth2 client-based applications.
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
