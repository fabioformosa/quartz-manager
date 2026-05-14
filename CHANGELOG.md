## **v5.0.1**

### New Features
* Added full job management: list eligible job classes, create stored jobs, update jobs, delete jobs, and trigger jobs on demand.
* Added trigger management APIs and UI flows to inspect, create, reschedule, pause, resume, and unschedule triggers.
* Added support for Quartz trigger types beyond simple triggers: cron, daily time interval, and calendar interval triggers.
* Added Quartz calendar management for annual, cron, daily, holiday, monthly, and weekly calendars.
* Added calendar-aware scheduling support, including calendar assignment to triggers and included-time checks.
* Redesigned the Quartz Manager dashboard with a broader operations view for scheduler, jobs, triggers, calendars, progress, and logs.
* Updated the embedded UI to Angular 21.
* Added support for Spring Boot 4 applications.

### Breaking Changes
* Quartz Manager now requires Java 21+ and Spring Boot 4.x.
* Applications using Quartz Manager APIs must migrate from `javax.*` validation/annotation dependencies to `jakarta.*` equivalents through the Spring Boot 4 stack.
* Scheduler command endpoints now use `POST` operations and clearer action names: `/scheduler/start`, `/scheduler/standby`, `/scheduler/resume`, and `/scheduler/shutdown` replace the previous `GET` command endpoints.
* Simple trigger endpoints now include the trigger group in the path: `/simple-triggers/{group}/{name}`.
* New trigger creation should use the generalized `/triggers/{group}/{name}` API when working with cron, daily time interval, or calendar interval triggers.

### Fixes
* Fixed WebSocket log retrieval for job execution logs.
* Fixed UI style regressions and improved readability in the dashboard, login page, job class display, and misfire instruction display.
* Improved API error handling for missing jobs, missing triggers, missing calendars, unsupported trigger types, and scheduling conflicts.

## **v4.1.1**
**NEW FEATURE** support for multiple triggers

## **v4.0.10**
Migrated to the new maven central repo

## **v4.0.9**
Fixed a bug which prevented to run the liquibase migration scripts in case of usage of quartz-manager-starter-persistence 

## **v4.0.8**
Upgraded the frontend to angular v14

## **v4.0.6**
Minor bug fixes

## **v4.0.5**
Fixed potential security issues

## **v4.0.4**
* Conformed the trigger configuration to the Simple Trigger of Quartz
* **BREAKING CHANGE** Changed accordingly the API and the UI
* Made Quartz Manager embeddable in projects with existing quartz instance, security layer, swagger ui.

## **v3.1.0**
* Added a new persistence module to persist the quartz triggers in a postgresql database

## **v3.0.1** 

Quartz-Manager is now publicly available into the maven central repo into 3 different packages.
You can import:  

* `quartz-manager-starter-api` to have a REST API layer to control your scheduler
* `quartz-manager-starter-ui` to import the UI also, in your spring webapp.
* `quartz-manager-starter-security` if you want to give access to the quartz-manager UI and API only to authenticated users
