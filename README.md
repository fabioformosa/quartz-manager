# quartz-manager
GUI Manager for Quartz Scheduler

## SCREENSHOT
![Alt text](http://www.fabioformosa.it/quartz-manager/quartz-manager-screenshot_800.png "Quartz Manager Screenshot")

## HOW IT WORKS
* Set up the trigger into the left sidebar in terms of: daily frequency and and max occurrences.
* Press the start button
* The GUI manager updates the progress bar and reports all logs of your quartz job.

## HOW IT RUNS
1. Quartz-Manager is a Spring Boot Application. To run by CLI  `mvn spring-boot:run` or through your IDE. For more details [spring boot ref.](http://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-running-your-application.html)
1. Open quartz-manager at the link: [http://localhost:9000/quartz-manager/manager](http://localhost:9000/quartz-manager/manager)
1. Log in with default credentials: `admin/admin`

## HOW RUN YOUR JOB
By default, quartz-manager executes the dummy job that logs "hello world!".
Replace the dummy job (class: `it.fabioformosa.quartzmanager.jobs.SampleJob`) with yours. Follow these steps:

1. Let extend the super class `it.fabioformosa.quartzmanager.jobs.AbstractLoggingJob`
1. Change the scheduler settings, providing the class name of your job. Open class `it.fabioformosa.quartzmanager.configuration.SchedulerConfig` and for the method `jobDetail` replace SampleJob.class with YourJob.class

## HOW CHANGE SETTINGS
* Num of Threads: `/quartz-manager/src/main/resources/quartz.properties`
* Credentials: `it.fabioformosa.quartzmanager.configuration.WebSecurityConfig`
* Server context path (default `/quartz-manager`) and port (default `9000`): `/quartz-manager/src/main/resources/application.properties`

## Tech Overview

**Backend Stack** Java 8, Spring Boot 1.3.2 (Spring MVC 4.2.4, Spring Security 4.0.3), Quartz Scheduler 2.2.2

**Application Server** Tomcat (embedded)

**Frontend** Angularjs 1.5.0, Thymeleaf 2.1.4, Web-Socket (sockjs 0.3.4, stompjs)

**Style** Bootstrap 3.3.4


