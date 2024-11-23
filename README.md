# Receipt Processor

### Requirements for local build/run

- Java 21

### How to run locally

(In Root)

1. `./gradlew build`
1. `java -jar build/libs/api-0.0.1-SNAPSHOT.jar`

### How to run tests

(In Root)

1. `./gradlew integrationTest`

### Bruno API Client Examples

- Located in BrunoApiExamples
- Load with [bruno](https://www.usebruno.com/)

### How to run with Docker Compose

(In Root)

1. `docker-compose up -d`

### How to build and run with Docker

(In Root)

1. `docker build -t receipt-api:latest .`
1. `docker run -d -p 7979:7979 --name receipt-api receipt-api:latest`
