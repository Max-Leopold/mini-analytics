# Mini Analytics [![Actions Status](https://github.com/Max-Leopold/mini-analytics/workflows/Java%20CI/badge.svg)](https://github.com/Max-Leopold/mini-analytics/actions)
Mini Analytics is a small scale project I've done as an internship introduction project.

This project can be used to search Twitter for user written Lucene queries.

## Installation

To install this project on your system you need [Maven](https://maven.apache.org/download.cgi) and [Docker](https://www.docker.com/get-started).
If you want to fetch tweets from Twitter you additionally need a [Twitter Developer Account](https://developer.twitter.com/en/apply-for-access.html) (optional).  

To download the project just use `git clone https://github.com/Max-Leopold/mini-analytics.git` to clone the project onto your machine.

## Configuration
You can configure the project to only use your desired sources. Currently available sources are:
- Twitter
- Reddit

In order to start the project you have to specify which sources you want to use. To do this, open the `mini-analytics/docker/sources.txt` file.
Add the sources you want to scrape to the end of first line, separated by one whitespace.

Example `services.txt` for all services:
```
zoo1 kafka1 kafka2 kafka3 postgres solr1 solr2 create-collection mention-storer mentions-generator Twitter Reddit
```

If you want to start the project and include the Twitter Module you have to specify your [Twitter Developer Keys](https://developer.twitter.com/en/docs/basics/authentication/guides/access-tokens.html) in the `mini-analtics/twitterpuller/src/main/resources/twitter4j.properties` file
```
oauth.consumerKey= your consumer key
oauth.consumerSecret= your consumer secret
oauth.accessToken= your access token
oauth.accessTokenSecret= your access token secret
```
You can find more information about configuration [here](http://twitter4j.org/en/configuration.html#fileconfiguration).

## Running

To run the project navigate to your project directory and use the command `mvn package` in your command line interface. 

Now navigate to the `mini-analytics/Docker` directory and start the application with the command `docker-compose up $(<services.txt) --build`.

To interact with the application just visit `localhost:8080`.