# Mini Analytics [![Build Status](https://travis-ci.com/Max-Leopold/mini-analytics.svg?branch=master)](https://travis-ci.com/Max-Leopold/mini-analytics)

Mini Analytics is a small scale project I've done as an internship introduction project.

This project can be used to search Twitter for user written Lucene queries.

## Installation

To install this project on your system you need [Maven](https://maven.apache.org/download.cgi), [Docker](https://www.docker.com/get-started) and a [Twitter Developer Account](https://developer.twitter.com/en/apply-for-access.html).

To download the project just use `git clone https://github.com/Max-Leopold/mini-analytics.git` to clone the project onto your machine.

In order to start the project you have to put your [Twitter Developer Keys](https://developer.twitter.com/en/docs/basics/authentication/guides/access-tokens.html) in the `mini-analtics/twitterpuller/src/main/resources/twitter4j.properties` file
```
oauth.consumerKey= your consumer key
oauth.consumerSecret= your consumer secret
oauth.accessToken= your access token
oauth.accessTokenSecret= your access token secret
```
You can find more information about configuration [here](http://twitter4j.org/en/configuration.html#fileconfiguration).

## Running

To run the project navigate to your project directory and use the command `mvn package` in your command line interface. 

Now navigate to the `mini-analytics/Docker` directory and start the application with the command `docker-compose up --build`.

To interact with the application just visit `localhost:8080`.