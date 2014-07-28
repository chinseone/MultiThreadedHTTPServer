Li Cheng
liche@adobe.com

MultiThreadedHTTPServer
=======================

A simple multi-threaded web server implemented in Java.
Methods Supported: GET, HEAD.
HTTPdLight does not support keep-alive or chunked transfer encoding.

Prerequisites:
-------------------------

The project is delivered as a Maven project. To build and execute you will need:

* JDK 1.5 or higher
* Maven

Build Instructions:
-------------------------

Building an Executable Jar:
> mvn package

The excutable jar can be found at: ./target/httpdlight-1.0-SNAPSHOT.jar

Usage:
-------------------------

Example Usage - Running as an executable jar:

> java -jar multithreadedserver-0.0.1-SNAPSHOT.jar 8080
