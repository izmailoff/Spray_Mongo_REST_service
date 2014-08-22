# Build Status
[![Build Status](https://travis-ci.org/izmailoff/Spray_Mongo_REST_service.png?branch=master)]
(https://travis-ci.org/izmailoff/Spray_Mongo_REST_service)

# Spray_Mongo_REST_service - TEMPLATE
A complete example of a RESTful architecture that uses:

 * [Spray](https://github.com/spray/spray)
 * [Akka](https://github.com/akka/akka)
 * [Rogue](https://github.com/foursquare/rogue)
 * [Lift-Record](https://github.com/lift/framework)
 * [MongoDB](http://www.mongodb.org/)
 * [Fongo](https://github.com/fakemongo/fongo)
 * [SBT-assembly](https://github.com/softprops/assembly-sbt)
 * [Specs2](http://etorreborre.github.io/specs2/)
 * [Mongeez](https://github.com/secondmarket/mongeez)
 
You can use this project as a template for building REST services based on Spray and MongoDB.

To learn more about these technologies see the list of [references]
(https://github.com/izmailoff/Spray_Mongo_REST_service/blob/master/docs/REFERENCES.md).

Big thanks for the [SBT script](https://github.com/paulp/sbt-extras) and
[Run Mongo script](https://github.com/foursquare/rogue) to the linked repos.

# Installation
This section describes steps required to build, configure and run the application.

## Building

### Pre-Installation Steps
Install these applications on your dev machine in order to be able to build the src code:

 * Java Development Kit (JDK) >= 1.7
 * Optionally install SBT, or use one provided with the project
 ([sbt](https://github.com/izmailoff/Spray_Mongo_REST_service/blob/master/sbt))

### Run SBT to generate executable JAR
SBT is a build tool that downloads source code dependencies, compiles code, runs tests,
generates scaladocs, and produces executables.

Start up SBT from OS shell:

    > sbt

or if it's not on a PATH:

    > ./sbt

In SBT shell type (note semicolons):

    ;clean; assembly

You can also run it as a single command from OS shell:

    > sbt clean assembly

This will run all tests and generate a single jar file named similar to: `rest-assembly-0.1.jar`.

Here is a full list of commands in case you want to generate IDE projects, documentation, etc:

    > sbt clean compile test doc assembly eclipse
    
Look at the output to find where docs, jars, etc goes. You can open projects with Eclipse or IntelliJ
afterwards. New versions of IntelliJ IDEA do not require generation of project files and can open
SBT projects directly using Scala plugin.

## System Requirements
To run compiled JAR file you should have installed:

 * Java Runtime Environment (JRE) >= 1.7
 * MongoDB >= 2.4 or use [start-test-mongo.sh]
 (https://github.com/izmailoff/Spray_Mongo_REST_service/blob/master/scripts/mongodb/start-test-mongo.sh)
 which can download and start MongoDB without installing it. Make sure to check that port number in
 [application.conf](https://github.com/izmailoff/Spray_Mongo_REST_service/blob/master/rest/src/main/resources/application.conf)
 matches the one in that script.

## Running
Run the JAR from your OS shell:

    > java -jar rest-assembly-0.1.jar
	
This will run the application. In particular it will start a web server and will be ready to receive
HTTP requests. Please use run scripts in production environment. They take care of runtime settings
and environment, so that you don't have to.

### Advanced Configuration
TODO

### Accessing the Service
You can send requests to the web service with tools like `curl` on Unix systems,
or you can open URLs in a browser.

Example shell scripts are provided to show sample requests you can do. They are located
[here](https://github.com/izmailoff/Spray_Mongo_REST_service/tree/master/scripts/requests).
