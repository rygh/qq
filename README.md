[![Build Status](https://travis-ci.org/rygh/qq.svg?branch=master)](https://travis-ci.org/rygh/qq)

QQ
============

Simple database work queue. Enables defining workers and transactional borders in an application. Everything is based on the database so new jobs are emitted at when the transaction completes.


Modules
===========
* qq-core - basic classes and interfaces for the setup, doesn't do anything useful without actual implementations
* qq-spring - implements repositories and adds annotations for injecting workers and executing work
* qq-jpa - entity loader that allows using JPA entities in consumers
* qq-example-app - Example application that shows a setup using Spring and JPA

Example application
============
The application will start a service including a dockerized postgres instance on startup (discarded on shutdown).

Building and starting the application requires Docker installed, first execution will be slow as the image may need to be downloaded. The demo can be run against a different instance by modifying the application.yml.

Test application can be started using the spring-boot plugin or however you want

```sh
cd qq-example-app
mvn spring-boot:run
```

Create rock entities using 

```sh
curl -XPOST http://localhost:8080/api/rocks \
	-H "Content-Type: application/json" \
 	-d'{"name":"Ka"}' 
```

This will store the Rock entity and push it to the first queue based service. 
This service will then smash the rock into fragments and run the next queue on each fragment. 

List performed jobs with status using

```sh
curl http://localhost:8080/api/jobs 
```


TODO
==============
* Error handling
* More tests (oh my)
* Documentation etc etc 
* Making work in progress visible to outside transactions


Disclaimer
==============
Probably it doesn't work on your computer, but if it is any help it does work on mine! Have a great day!
