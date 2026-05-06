# QUARTZ-MANAGER REST API

This is a multi-module maven project.

## PROJECT STRUCTURE
* `quartz-parent/quartz-manager-starter-api` is the core library must be imported to get the Quartz-Manager API and to interact with the [Quartz Scheduler](http://www.quartz-scheduler.org/) via REST.
* `quartz-parent/quartz-manager-starter-security` is a library that can be imported to get an out-of-the-box security layer over the quartz-manager API.
* `quartz-parent/quartz-manager-starter-persistence` is a library that can be imported to persist the Quartz Scheduler managed by Quartz Manager, in a Postgresql database.
* `quartz-parent/quartz-manager-starter-ui`  is a maven module with all the logic to build and package the angular frontend in a webjar.
* `quartz-parent/quartz-manager-web-showcase` is nothing but a simple backend which imports the above libraries, helpful to develop with a frontend started locally with the webpack dev server.
* `quartz-frontend` is the angular single-page-app that interacts with the Quartz Manager API.

## PROJECT DETAILS
**[requirements]** Make sure you have installed
* [JDK](https://java.com/download/) 9 or greater
* [Maven](https://maven.apache.org/) 3.6 or greater
* [npm](https://www.npmjs.com/get-npm) 16 or greater , [node](https://nodejs.org) 8 or greater
* [angular-cli](https://cli.angular.io/)

To build&run Quartz Manager locally in your machine:

```
#CLONE REPOSITORY
git clone https://github.com/fabioformosa/quartz-manager.git

# START QUARTZ-MANAGER-WEB
cd quartz-manager/quartz-parent
mvn install
cd quartz-manager/quartz-parent/quartz-manager-web-showcase
mvn spring-boot:run

# START QUARTZ-MANAGER-FRONTEND
cd quartz-manager/quartz-manager-frontend
npm install
npm start

```

1. Open browser at [http://localhost:4200](http://localhost:4200)
1. If you've imported `quartz-manager-security-starter` log in with **default credentials**: `admin/admin`

If you are not confident with maven CLI, you can start it by your IDE. For more details [spring boot ref.](http://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-running-your-application.html)


## HOW TO RUN YOUR SCHEDULED JOB
By default, `quartz-manager-web-showcase` executes the dummy job that logs "hello world!".
Replace the dummy job (class: `it.fabioformosa.quartzmanager.jobs.SampleJob`) with yours. Follow these steps:

1. Extend the super class `it.fabioformosa.quartzmanager.jobs.AbstractLoggingJob`
1. set property `quartz-manager.jobClassPackages` with the list of the java packages (comma separated) containing the job class eligible for Quartz Manager

## HOW TO CHANGE SETTINGS
* Num of Threads: `/quartz-manager-parent/quartz-manager-web/src/main/resources/managed-quartz.properties`
* Credentials: To change admin's password, set app property (or ENV var) `quartz-manager.security.accounts.in-memory.users[0].passord`
* quartz-manager backend context path (default `/quartz-manager`) and port (default `8080`): `/quartz-manager/src/main/resources/application.properties`

## Tech Overview

**Backend Stack** Java 9, Spring Boot 2.5.6 (Spring MVC 5.3.12, Spring Security 5.5.3), Quartz Scheduler 2.3.2

**Frontend** Angular 14.2.12, Web-Socket (stompjs 2.3.3)

**Style** Angular Material 14, FontAwesome 5

Starting from Quartz Manager v2.x.x, the new structure of project is:
* Multi-module maven project: REST API backend
* Angular 14: Single Page Application frontend

(The first version of quartz manager was a monolithic backend that provided also frontend developed with angularjs 1.6.x. You can find it at the branch 1.x.x)

## Contributes

Every contribution is welcome. Open a github's issue, let's discuss about the new features and how to implement them.
