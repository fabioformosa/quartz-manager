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
