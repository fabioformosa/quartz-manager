[![Java CI with Maven](https://github.com/fabioformosa/quartz-manager/actions/workflows/maven.yml/badge.svg)](https://github.com/fabioformosa/quartz-manager/actions/workflows/maven.yml)
[![npm CI](https://github.com/fabioformosa/quartz-manager/actions/workflows/npm.yml/badge.svg)](https://github.com/fabioformosa/quartz-manager/actions/workflows/npm.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/it.fabioformosa.quartz-manager/quartz-manager-starter-api/badge.svg)](https://maven-badges.herokuapp.com/maven-central/it.fabioformosa.quartz-manager/quartz-manager-starter-api)  
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=fabioformosa_quartz-manager&metric=coverage)](https://sonarcloud.io/summary/new_code?id=fabioformosa_quartz-manager) [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=fabioformosa_quartz-manager&metric=bugs)](https://sonarcloud.io/summary/new_code?id=fabioformosa_quartz-manager) [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=fabioformosa_quartz-manager&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=fabioformosa_quartz-manager) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=fabioformosa_quartz-manager&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=fabioformosa_quartz-manager)


[QUARTZ MANAGER](https://github.com/fabioformosa/quartz-manager#quartz-manager)  
    &nbsp;&nbsp;&nbsp;&nbsp;[Quartz Manager UI](https://github.com/fabioformosa/quartz-manager#quartz-manager-ui)  
    &nbsp;&nbsp;&nbsp;&nbsp;[Quartz Manager API](https://github.com/fabioformosa/quartz-manager#quartz-manager-api)    
[HOW IT WORKS](https://github.com/fabioformosa/quartz-managerhttps://github.com/fabioformosa/quartz-manager#get-started)  
    &nbsp;&nbsp;&nbsp;&nbsp;[Quartz Manager Starter API Lib](https://github.com/fabioformosa/quartz-manager#quartz-manager-starter-api-lib)  
    &nbsp;&nbsp;&nbsp;&nbsp;[Quartz Manager Starter UI Lib](https://github.com/fabioformosa/quartz-manager#quartz-manager-starter-ui-lib)   
    &nbsp;&nbsp;&nbsp;&nbsp;[Quartz Manager Starter Security Lib](https://github.com/fabioformosa/quartz-manager#quartz-manager-starter-security-lib)  
    &nbsp;&nbsp;&nbsp;&nbsp;[Quartz Manager Persistence Lib](https://github.com/fabioformosa/quartz-manager#quartz-manager-starter-persistence-lib)  
[EXAMPLES](https://github.com/fabioformosa/quartz-manager#examples)  
[LIMITATIONS](https://github.com/fabioformosa/quartz-manager#limitations)  
[ROADMAP](https://github.com/fabioformosa/quartz-manager#roadmap)  
[REPOSITORY](https://github.com/fabioformosa/quartz-manager#repository)  
[HOW TO CONTRIBUTE](https://github.com/fabioformosa/quartz-manager#how-to-contribute)  
[SUPPORT THE PROJECT](https://github.com/fabioformosa/quartz-manager#%EF%B8%8F-support-the-project-%EF%B8%8F)

# QUARTZ MANAGER
In the last decade, the [Quartz Scheduler](http://www.quartz-scheduler.org/) has become the most adopted opensource job scheduling library for Java applications.  

**Quartz Manager** enriches it with a **REST API** layer and a handy **UI console** to easily control and monitor a Quartz Scheduler.  

Quartz Manager is a Java library you can import in your Spring-Based Web Application to run scheduled jobs, start&stop them and get the job outcomes. You can do it through HTTP calls to the the Quartz Manager API or in a visual manner through the Quartz Manager UI dashboard.  


## QUARTZ MANAGER UI
The **Quartz Manager UI** is a dashboard in the form of a single-page-application provided by the Quartz Manager Java library itself. You can have it embedded in your project, as well as you get embedded the Swagger UI.  
It leverages the websockets to receive in real-time the trigger updates and the outcomes of the job executions.  

![](https://github.com/fabioformosa/quartz-manager/blob/master/quartz-manager-parent/quartz-manager-web-showcase/src/main/resources/quartz-manager-4-screenshot.png)

## QUARTZ MANAGER API
Quart-Manager exposes REST endpoints to interact with the Quartz Scheduler. This endpoints are invoked by Quartz Manager UI also.
The REST API are documented by an OpenAPI Specification interface. 

![](https://github.com/fabioformosa/quartz-manager/blob/master/quartz-manager-parent/quartz-manager-web-showcase/src/main/resources/quartz-manager-4-swagger.png)


# HOW IT WORKS
Quartz Manager can either coexist with your existing instance of Quartz or it can import itself the Quartz dependency.   

In the first case, Quartz Manager is compatible with Quartz v2.3.x . Quartz Manager creates and configures its own instance of Quartz Scheduler and it manages only the jobs and the triggers created through it. Your other jobs and triggers, running in the existing quartz instance, are out of the scope of Quartz Manager.  

In the latter case, in which there isn't an existing quartz instance, you can rely on Quartz Manager to speed up the setup of a Quartz instance, with a persistent storage also if you need it. Futhermore, if you start a bare project from scratch, just to run scheduled jobs, Quartz Manager comes with the option to enable a security layer to protect the API and the UI with an authentication model based on [JWT](https://jwt.io).

**FEATURES**
* You can schedule a [Quartz Simple Trigger](http://www.quartz-scheduler.org/documentation/quartz-2.3.0/tutorials/tutorial-lesson-05.html) which allows you to start a job now or in a specific date-time, to set it as a recurring job with a certain time frequency, unlimited or limited on the number of fires and within a certain end date.
* You can start, pause and resume the quartz scheduler via API or clicking the start/stop buttons at the UI console.
* Leveraging on an active web-socket, the `Quartz-Manager-UI` updates in real time the progress bar and it displays the list of the latest logs produced by your quartz job.
* You can enable a secure layer, if your project doesn't have any, to give access at the API and the UI only to authenticated users.
* You can enable a persistent layer, to persist the config and the progress of your trigger, in a postgresql database.

# GET STARTED

**Requirements** 
  Java 9+, Spring Framework 5+ (Spring Boot 2.x)
  
Quart Manager is a modular library:
* quartz-manager-starter-api (mandatory)
* quartz-manager-starter-ui (optional)
* quartz-manager-starter-security (optional)
* quartz-manager-starter-persistence (optional)

In order to decrease the overall configuration time for the project, all modules of the library follow the approach of Spring Starters. Thus, it's enough to import the dependency to get started.

Below the list of the quartz-manager modules you can import.

## Quartz Manager Starter API Lib
This is the only mandatory module of the library.   
Add the dependency, make eligible for Quart Manager the job classes and set the props in your `application.properties` file.

### Step 1. Dependency

#### Maven
```
<dependency>
  <groupId>it.fabioformosa.quartz-manager</groupId>
  <artifactId>quartz-manager-starter-api</artifactId>
  <version>4.0.9</version>
</dependency>
```
#### Gradle
```
implementation group: 'it.fabioformosa.quartz-manager', name: 'quartz-manager-starter-api', version: '4.0.9'
```

### Step 2. Quartz Manager Job Classes
The job classes, which can be eligible for triggers managed by Quartz Manager, must extend the super-class `AbstractLoggingJob`. 
In this way, Quartz Manager is able to collect and display the outcomes at the UI console.

 ```
 public class SampleJob extends AbstractLoggingJob {

    @Override
    public LogRecord doIt(JobExecutionContext jobExecutionContext) {
        ... do stuff ...
        return new LogRecord(LogType.INFO, "Hello from QuartManagerDemo!");
    }

}
```

### Step 3. Quartz Manager API - App Props

| Property                           | Values   |Mandatory | Default | Description                                                               |
| :---                               |:---      |:---      |:---     |:--                                                                        |
| quartz-manager.jobClassPackages    | string   | Yes      |         |java base package which contains your job classes                          |
| quartz-manager.oas.enabled         | boolean  | No       | false   |whether to create an OpenAPI instance to expose the OAS and the Swagger UI |


### REST API & OpenAPI Specification
Set the app prop `quartz-manager.oas.enabled=true` if you want to expose the OpenApi Specification of the Quartz Manager APIs.  
Reach out the swagger-ui at the URL:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

If your project has already an OpenAPI instance and you've set `quartz-manager.oas.enabled=true`, then make sure to add an OpenApiGroup to group the API of your application. Quart Manager exposes its API in group called "Quartz Manager". If you use OAS Annotations:
```
  @Bean
  public GroupedOpenApi simplySpringDemoGroupedOpenApi() {
    return GroupedOpenApi.builder().group("myapp").packagesToScan("com.example.myapp").build();
  }
```

### QUARTZ SETTINGS
Quartz Manager creates its own instance of a [Quartz Scheduler](http://www.quartz-scheduler.org/). 

By default, the managed quartz instance is instantiated with the following props:

```
org.quartz.scheduler.instanceName=quartz-manager-scheduler
org.quartz.threadPool.threadCount=1
```

You can customize the configuration of the Quartz managed by Quartz Manager creating a file `managed-quartz.properties` in the classpath (`src/main/resources`).   
For further details about the quartz properties, click [here](http://www.quartz-scheduler.org/documentation/quartz-2.3.0/configuration/).

#### Existing Quartz Instance
Quarz Manager imports transitively the [Quartz Scheduler library](https://mvnrepository.com/artifact/org.quartz-scheduler/quartz) ver 2.3.2.
However, Quartz Manager can be imported even thought you've already imported the quartz scheduler lib. Indeed Quartz Manager coexists with the existing Quarz Scheduler Instance you've created in your project. In that case, Quartz Manager will manage the triggers created by it and it won't interfere with the other quartz instances.
The prerequesite is that you've imported a quartz scheduler ver 2.3.x.

You can configure the Quartz instance managed by Quartz Manager through the file `managed-quartz.properties` and your own Quartz instance through the file  `quartz.properties`.

If you've created a `SchedulerFactoryBean`, tag it as `@Primary` to avoid conflicts in-type, since Quartz Manager creates another bean of the same type.

```
    @Primary
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean( JobFactory jobFactory, Properties quartzProperties) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        ...
        return factory;
    }
```


## Quartz Manager Starter UI Lib
You can optionally import the following dependency to have the UI Dashboard to interact with the Quartz Manager API.

### Dependency

#### Maven
```
<dependency>
  <groupId>it.fabioformosa.quartz-manager</groupId>
  <artifactId>quartz-manager-starter-ui</artifactId>
  <version>4.0.9</version>
</dependency>
```
#### Gradle
```
implementation group: 'it.fabioformosa.quartz-manager', name: 'quartz-manager-starter-ui', version: '4.0.9'
``` 

### Reach out the UI Console at URL
if you run locally [http://localhost:8080/quartz-manager-ui/index.html](http://localhost:8080/quartz-manager-ui/index.html)  



## Quartz Manager Starter Security Lib

Import this optional dependency, if you want to enable a security layer and allow the access to the REST API and UI only to authenticated users.  
The authentication model of Quartz Manager is based on [JWT](https://jwt.io/).

If you're going to import Quartz Manager in a project with an existing configuration of Spring Security, consider the following:
- Only if your existing security is cookie-based, actually you don't need to import the module `quartz-manager-security-lib`. Simply, Quartz Manager will be under the hat of your security setup. In all other cases (based on HTTP headers, query params, etc) Quartz Manager is not aware about your auth token and it will implement its own authentication model.
- Quartz Manager Security relies on Spring Security upon a dedicated *HTTP Spring Security Chain* applied to the base path `/quartz-manager`. So it doesn't interfere with your existing security setup.
- Quartz Manager Security keeps simple the authentication, adopting the InMemory Model. You have to define the users (in terms of username/credentials passed via `application.properties`) can access to Quartz Manager.
- By default, the UI attaches the JWT Token to each request in the authorization header in the "Bearer" format.

Future development: the Quart Manager Security OAuth2 client.


### Dependency

#### Maven

```
<dependency>
  <groupId>it.fabioformosa.quartz-manager</groupId>
  <artifactId>quartz-manager-starter-security</artifactId>
  <version>4.0.9</version>
</dependency>
```

#### Gradle

```
compile group: 'it.fabioformosa.quartz-manager', name: 'quartz-manager-starter-security', version: '4.0.9'
```


### Quartz Manager Security Lib - App Props

| Property                                                    | Values   |Mandatory         | Default | Description     |
| :---                                                        |:---      |:---              |:---     |:--              |
| quartz-manager.security.jwt.secret                          | string   |                  |         | Secret to sign the JWT Token |          
| quartz-manager.security.jwt.expiration-in-sec               | number   | no               | 28800   |                              |
| quartz-manager.security.accounts.in-memory.enabled          | boolean  | no               | true    |                              |
|quartz-manager.security.accounts.in-memory.users[0].username | string   | yes (if enabled) |         |                              | 
|quartz-manager.security.accounts.in-memory.users[0].password | string   | yes              |         |                              |
|quartz-manager.security.accounts.in-memory.users[0].roles[0] | string   | yes              |         | set the value ADMIN          |


## Quart Manager Starter Persistence Lib

By default, Quartz Manager runs with a `org.quartz.simpl.RAMJobStore` that stores your managed scheduler in memory. The RAMJobStore is the simplest store and also the most performant. However it comes with the drawback that all scheduling data are lost if your applications ends or crashes. In case of a restarting of your app, you can't resume the scheduler from the point it stopped.  
Import the Quartz Manager Persistence Module if you want to persist your scheduler data.  
The pre-requesite is the availability of Postgresql database where Quartz Manager can store its information. You have to provide it a bare database and a postresql user granted for DDL and DML queries. About the DDL, consider that Quartz Manager Persistence will create all tables, it needs to work, at the bootstrap.

### Dependency

#### Maven

```
<dependency>
  <groupId>it.fabioformosa.quartz-manager</groupId>
  <artifactId>quartz-manager-starter-persistence</artifactId>
  <version>4.0.9</version>
</dependency>
```

#### Gradle

```
compile group: 'it.fabioformosa.quartz-manager', name: 'quartz-manager-starter-persistence', version: '4.0.9'
```

### Quartz Manager Persistence Lib - App Props

| Property                                                    | Values   |Mandatory         | Default | Description     |
| :---                                                        |:---      |:---              |:---     |:--              |
| quartz-manager.persistence.quartz.datasource.url            | string   | yes              |         |eg. jdbc:postgresql://localhost:5432/quartzmanager |          
| quartz-manager.persistence.quartz.datasource.user           | string   | yes              |         |                              |
| quartz-manager.persistence.quartz.datasource.password       | string   | yes              |         |                              |



## Examples

You can find some examples of different scenarios of projects which import Quartz Manager at the repository [quartz-manager-use-cases](https://github.com/fabioformosa/quartz-manager-use-cases)
* *simply-spring* - tipical scenario in which you create a minimal spring project from scratch dedicated to launch your scheduled jobs. Imported libraries: Quartz Manager API, Quartz Manager UI and Quartz Manager Security.
* *simply-spring-no-security* - as simple-spring, without the security. Imported libraries: Quartz Manager API, Quartz Manager UI.
* *existing-security-cookie-based* - It demonstrates how Quartz Manager stays under the security of your project, in case of an auth model based on cookies. Imported libraries: Quartz Manager API, Quartz Manager UI. 
* *existing-security-header-based* - It demonstrates how Quartz Manager Security can coexists with another Spring Security Config present in your project. Imported libraries: Quartz Manager API, Quartz Manager UI and Quartz Manager Security.
* *existing-quartz* - It demonstrates how to Quartz Manager can coexist with a Quartz instance already present in your project Imported libraries: Quartz Manager API, Quartz Manager UI.
* *existing-quartz-and-storage* - It demonstrates how to Quartz Manager Persistence can coexist with a Quartz instance already present in your project. Imported libraries: Quartz Manager API, Quartz Manager UI and Quartz Manager Persistence.
* *with-persistence* - It demonstrates how to import the Quartz Manager Persistence and get created the quartz tables automatically at the bootstrap


## Limitations

> Step by step, day by day

Quartz Manager has a work-in-progress roadmap to be full-fledged library to manage a [Quartz Scheduler](http://www.quartz-scheduler.org/).

At this stage of the roadmap, these are the limitations:
* Currently you cannot start multiple triggers or multiple jobs. At the moment a workaround is to launch multiple projects based on Quartz Manager.
* Currently you can only start [Quartz Simple Trigger](http://www.quartz-scheduler.org/documentation/quartz-2.3.0/tutorials/tutorial-lesson-05.html). The support to other kind of triggers will come soon: [Calendar Interval Trigger](https://www.quartz-scheduler.org/api/2.3.0/org/quartz/CalendarIntervalTrigger.html), [Cron Interval Trigger](https://www.quartz-scheduler.org/api/2.3.0/org/quartz/CronTrigger.html), [Daily Interval Trigger](https://www.quartz-scheduler.org/api/2.3.0/org/quartz/DailyTimeIntervalTrigger.html)
* Currently the cluster mode is not supported
* Currently the persistence of Quartz Manager supports only the PostgreSQL. The support to other king of triggers will come soon: MySQL, MariaDB, SqlServer, Oracle, H2.  

## ROADMAP

Take a look a the [Project Roadmap](https://github.com/users/fabioformosa/projects/1).  
Don't hesitate to give your feedback, your opinion is important to understand the priority.

Next steps in the roadmap are:
* Manage multiple triggers and jobs
* Cluster mode support
* Support to other all types of Quartz Triggers:  [Calendar Interval Trigger](https://www.quartz-scheduler.org/api/2.3.0/org/quartz/CalendarIntervalTrigger.html), [Cron Interval Trigger](https://www.quartz-scheduler.org/api/2.3.0/org/quartz/CronTrigger.html), [Daily Interval Trigger](https://www.quartz-scheduler.org/api/2.3.0/org/quartz/DailyTimeIntervalTrigger.html)
* UI Re-styling
* OAuth Client
* Support to other DBMS than PostreSQL: MySQL, MariaDB, SqlServer, Oracle, H2.

## Repository

Checkout the **master branch** to get the sourcecode of the latest released versions.  
Checkout the **develop branch** to take a look at the sourcecode of the incoming release.

## HOW-TO CONTRIBUTE  

For tech details, how-to run locally the project and how-to contribute, reach out this other [README.md](https://github.com/fabioformosa/quartz-manager/blob/develop/quartz-manager-parent/README.md)

## ❤️ SUPPORT THE PROJECT ❤️

Sometimes it's a matter of a kind action. You can support Quartz Manager and its continuous improvement turning on a github star on this project ⭐
