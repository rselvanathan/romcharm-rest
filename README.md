##Personal REST API

[![Build Status](https://travis-ci.org/rselvanathan/romcharm-rest.svg?branch=master)](https://travis-ci.org/rselvanathan/romcharm-rest)

A Rest Service implemented using Spring Boot. This service powers a few of my private Projects (_my_page_ and _romcharm-app_ 
currently)

##### Swagger URL 
https://api.romandcharmi.com/swagger-ui.html - 
(For viewing only; cannot be used to play around with API's)

####Features
 - Integrated with AWS SNS to send notifications for automated mailing
 - JWT token authentication
 - Role based API security (with JWT token)
 - AWS DynamoDB data storage
 
####Tech Used 
 - Spring Boot, Spring Security, AWS DynamoDB, AWS SNS, JWT Tokens, Docker
 
####Docker Usage

The REST Service is built and deployed as a docker image currently. To run the service simply use
this command :

```bash
docker run -d --name romcharm-rest -p 8080:8080 \
-e jwtSecret= \
-e AWS_ACCESS_KEY_ID= \
-e AWS_SECRET_ACCESS_KEY= \
-e AWS_EMAIL_SNS_TOPIC= \
-e APP_TYPE=ROMCHARM \
-it rselvanathan/romcharm-rest:latest
```

The missing fields must be filled in by the user.

######Author:

Romesh Selvanathan