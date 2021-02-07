[![Build Status](https://travis-ci.org/fabioformosa/quartz-manager.svg?branch=master)](https://travis-ci.org/fabioformosa/quartz-manager)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/it.fabioformosa.quartz-manager/quartz-manager-starter-api/badge.svg)](https://maven-badges.herokuapp.com/maven-central/it.fabioformosa.quartz-manager/quartz-manager-starter-api)
[![Gitter](https://badges.gitter.im/quartz-manager/community.svg)](https://gitter.im/quartz-manager/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

# QUARTZ MANAGER
Quartz Manager is a library you can import in your spring webapp to easily enable the [Quartz Scheduler](http://www.quartz-scheduler.org/) and to control it by REST APIs or by a UI Manager Panel (angular-based). 

Your Spring Webapp should provide the java class of the job you want schedule. Importing the Quartz Manager your project will have the REST API and (optionally) the UI to launch and control the job.
The UI Dashboard is composed by a management panel to set the quartz trigger, to start/stop the scheduler and a log panel with a progress bar to display the job output. 

![](https://github.com/fabioformosa/quartz-manager/blob/master/quartz-manager-parent/quartz-manager-web-showcase/src/main/resources/quartz-manager-2-screenshot_800.PNG)

## HOW IT WORKS
* Set up the trigger into the left sidebar in terms of: daily frequency and and max occurrences.
* Press the start button
* The GUI manager updates the progress bar and reports all logs of your quartz job.

## QUICK START

* **Requirements** 
  Java 8+

* **add the dependency**

MAVEN

```
<dependency>
  <groupId>it.fabioformosa.quartz-manager</groupId>
  <artifactId>quartz-manager-starter-api</artifactId>
  <version>3.0.1</version>
</dependency>

<!-- OPTIONALLY -->
<dependency>
  <groupId>it.fabioformosa.quartz-manager</groupId>
  <artifactId>quartz-manager-starter-ui</artifactId>
  <version>3.0.1</version>
</dependency>
```

GRADLE
  
```
compile group: 'it.fabioformosa.quartz-manager', name: 'quartz-manager-starter-api', version: '3.0.1'

//optionally
compile group: 'it.fabioformosa.quartz-manager', name: 'quartz-manager-starter-ui', version: '3.0.1'

```
Import  `quartz-manager-starter-ui` as well, if you want use the Quartz Manager API by the angular frontend.  

* **add a `quartz.properties` file in the classpath (`src/main/resources`)**

```
org.quartz.scheduler.instanceName=example
org.quartz.scheduler.instanceId=AUTO
org.quartz.threadPool.threadCount=1
```


* **Create the job class that you want to schedule**
 
 ```
 public class SampleJob extends AbstractLoggingJob {

    @Override
    public LogRecord doIt(JobExecutionContext jobExecutionContext) {
        return new LogRecord(LogType.INFO, "Hello from QuartManagerDemo!");
    }

}
```
Extend the super-class `AbstractLoggingJob`


* **Enable quartz-manager adding into the application.yml**

```
quartz:
  enabled: true

job: 
  frequency: 4000
  repeatCount: 19

quartz-manager:
  jobClass: <QUALIFIED NAME OF THE YOUR JOB CLASS>
```

* **REST API**  
You can access the REST API, through the swagger-ui. Open the URL:  
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

(Change the port and the contextPath accordingly with the setup of your webapp)

* **Frontend**  
If you've imported the `quartz-manager-starter-ui` you can open the UI at URL:  
[http://localhost:8080/quartz-manager-ui/index.html](http://localhost:8080/quartz-manager-ui/index.html)

(Change the port and the contextPath accordingly with the setup of your webapp)

* **Security**
If you want enable a security layer and allow the access to the REST API and to the UI only to authenticated users, add the dependency:

MAVEN

```
<dependency>
  <groupId>it.fabioformosa.quartz-manager</groupId>
  <artifactId>quartz-manager-starter-security</artifactId>
  <version>3.0.1</version>
</dependency>
```

GRADLE

```
compile group: 'it.fabioformosa.quartz-manager', name: 'quartz-manager-starter-security', version: '3.0.1'
```

and in your application.yml:

```
quartz-manager:
  security:
    login-model:
      form-login-enabled: true
      userpwd-filter-enabled : false
    jwt:
      enabled: true
      secret: "PLEASE_TYPE_HERE_A_SECRET"
      expiration-in-sec: 28800  # 8 hours
      header-strategy:
        enabled: false
        header: "Authorization"
      cookie-strategy:
        enabled: true
        cookie: AUTH-TOKEN
  accounts:
    in-memory:
      enabled: true
      users:
        - name: admin
          password: admin
          roles:
            - ADMIN      

```

* **DEMO**

Take a loot to the project [Quartz-Manager Demo](https://github.com/fabioformosa/quartz-manager-demo), it is an example of how-to:
 * import the quartz-manager-api library in your webapp
 * include the quartz-manager frontend (angular based) through a webjar
 * set properties into the application.yml
 * add a secure layer to allow the API only to logged users
 * schedule a custom job (a dummy `hello world`)

## ROADMAP
Open the [Project Roadmap](https://github.com/fabioformosa/quartz-manager/projects) to take a look at the plan of Quartz Manager.  

Next steps in the roadmap are:
* to add a persistent layer to save all job setup.
* to add a complete setup UI panel for quartz, in term of cronjobs and multiple jobs.
* to add CI/CD pipeline to ease the deploy pulling a docker container.
* Enabling adapters for integrations: kafka, etc.


## HOW-TO CONTRIBUTE  

### PROJECT STRUCTURE
* **quartz-parent/quartz-manager-starter-api** is the library that can be imported in webapp to have the quartz-manager API.
* **quartz-parent/quartz-manager-starter-ui** is a maven module to build and package the angular frontend in a webjar.
* **quartz-parent/quartz-manager-starter-security** is ther library that can be imported in a webapp to have a security layer (login) over the quartz-manager API.
* **quartz-parent/quartz-manager-web-showcase** is an example of webapp that imports quartz-manager-api. Useful to develop the frontend started locally with the webpack dev server.
* **quartz-frontend** is the angular app that interacts with the Quartz Manager API.

### PROJECT DETAILS
**[requirements]** Make sure you have installed
* [Java 8](https://java.com/download/) or greater
* [Maven](https://maven.apache.org/)
* [npm](https://www.npmjs.com/get-npm), [node](https://nodejs.org) and [angular-cli](https://cli.angular.io/)

To build&run quartz-manager in your machine:

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
1. Log in with **default credentials**: `admin/admin`

If you are not confident with maven CLI, you can start it by your IDE. For more details [spring boot ref.](http://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-running-your-application.html)


## HOW TO RUN YOUR SCHEDULED JOB
By default, `quartz-manager-web-showcase` executes the dummy job that logs "hello world!".
Replace the dummy job (class: `it.fabioformosa.quartzmanager.jobs.SampleJob`) with yours. Follow these steps:

1. Extend the super class `it.fabioformosa.quartzmanager.jobs.AbstractLoggingJob`
1. set property `quartz-manager.jobClass` with qualified name of your custom Job Class (default job is SampleJob.class)

## HOW TO CHANGE SETTINGS
* Num of Threads: `/quartz-manager-parent/quartz-manager-web/src/main/resources/quartz.properties`
* Credentials: To change admin's password, set ENV var `quartz-manager.account.pwd`
* quartz-manager backend context path (default `/quartz-manager`) and port (default `8080`): `/quartz-manager/src/main/resources/application.properties`

## HOW TO BROWSE REST API DOC
Swagger has been added as library. So, you can get REST API doc opening: [http://localhost:8080/quartz-manager/swagger-ui.html](http://localhost:8080/quartz-manager/swagger-ui.html)

## Tech Overview

**Backend Stack** Java 8, Spring Boot 2.1.4 (Spring MVC 5.1.6, Spring Security 5.1.5, Spring AOP 5.1.6), Quartz Scheduler 2.3.1

**Application Server** Tomcat (embedded)

**Frontend** Angular 9.1.4, Web-Socket (stompjs 2.3.3)

**Style** angular material, FontAwesome 5

From quartz manager ver 2.x.x, the new structure of project is:
 * REST API backend
 * Single Page Application frontend (angular 9)

(The previous version of quartz manager was a monolithic backend that provided also frontend developed with angularjs 1.6.x. You can find it at the branch 1.x.x)

## Contributes

Every contribution is welcome. Open a issue, so we can discuss about new features and implement them. 

## Credits

* this project has been created from [angular-spring-starter](https://github.com/bfwg/angular-spring-starter)


