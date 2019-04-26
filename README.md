# QUARTZ MANAGER
GUI Manager for Quartz Scheduler.

Through this webapp you can launch and control your scheduled job. The GUI Console is composed by a managament panel to set trigger, start/stop scheduler and a log panel with a progress bar to display the job output. 

## SCREENSHOT
![](https://github.com/fabioformosa/quartz-manager/blob/master/quartz-manager-backend/src/main/resources/quartz-manager-2-screenshot_800.PNG)

## HOW IT WORKS
* Set up the trigger into the left sidebar in terms of: daily frequency and and max occurrences.
* Press the start button
* The GUI manager updates the progress bar and reports all logs of your quartz job.

## QUICK START
**[requirements]** Make sure you have installed
* [Java 8](https://java.com/download/) or greater
* [Maven](https://maven.apache.org/)
* [npm](https://www.npmjs.com/get-npm), [node](https://nodejs.org) and [angular-cli](https://cli.angular.io/)

```
#CLONE REPOSITORY
git clone https://github.com/fabioformosa/quartz-manager.git

# START QUARTZ-MANAGER-BACKEND
cd quartz-manager/quartz-manager-backend
mvn spring-boot:run

# START QUARTZ-MANAGER-FRONTEND
cd quartz-manager/quartz-manager-backend
npm install
npm start

```

1. Open browser at [http://localhost:4200](http://localhost:4200)
1. Log in with **default credentials**: `admin/admin`

If you are not confident with maven CLI, you can start it by your IDE. For more details [spring boot ref.](http://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-running-your-application.html)

## HOW TO RUN YOUR SCHEDULED JOB
By default, quartz-manager executes the dummy job that logs "hello world!".
Replace the dummy job (class: `it.fabioformosa.quartzmanager.jobs.SampleJob`) with yours. Follow these steps:

1. Let extend the super class `it.fabioformosa.quartzmanager.jobs.AbstractLoggingJob`
1. Change the scheduler settings, providing the class name of your job. Open class `it.fabioformosa.quartzmanager.configuration.SchedulerConfig` and for the method `jobDetail` replace SampleJob.class with YourJob.class

## HOW TO CHANGE SETTINGS
* Num of Threads: `/quartz-manager/src/main/resources/quartz.properties`
* Credentials: To change admin's password, set ENV var `quartz-manager.account.pwd`
* quartz-manager backend context path (default `/quartz-manager`) and port (default `8080`): `/quartz-manager/src/main/resources/application.properties`

## Tech Overview

**Backend Stack** Java 8, Spring Boot 2.1.4 (Spring MVC 5.1.6, Spring Security 5.1.5, Spring AOP 5.1.6), Quartz Scheduler 2.3.1

**Application Server** Tomcat (embedded)

**Frontend** Angular 7.2.13, Web-Socket (stompjs 2.3.3)

**Style** angular material, FontAwesome 5

From quartz manager ver 2.x.x, the new structure of project is:
 * REST backend (java based, using [http://www.quartz-scheduler.org/](http://www.quartz-scheduler.org/)
 * Single Page Application frontend (angular 7)

(The previous version of quartz manager was a monolithic backend that provided also frontend developed with angularjs 1.6.x. You can find it at the branch 1.x.x)

## Credits

* this project has been created from [angular-spring-starter](https://github.com/bfwg/angular-spring-starter)


