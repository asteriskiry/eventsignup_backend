# Eventsignup Backend
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.

## Used technologies

- Java 17
- Springboot 2.6.x
- Apache IO Commons
- Gradle
- MongoDB 5.0.5
- JUnit 5 & Mockito
- Project lombok
- Spotless for code formatting

## Development

- Import existing sources as a new project in your favorite IDE (e.g. IntelliJ IDEA)
- Import Gradle project
- Expects Mongodb at localhost:27017
- Server will be at localhost:8080
- OpenAPI definitions are available at http://localhost:8080/api-docs.<yaml | json>
- OpenAPI swagger ui is available at http://localhost:8080/swagger-ui.html
- All text which is visible to the end user (e.g. email) must use i18n translations (messages*.properties files)
- All features and bug fixes are done in their own branches
- All Pull Requests need to squash commits before merge so git history of develop (and master) remains clean and linear

Format code with Spotless:

    $ ./gradlew spotlessApply

To run:

    $ ./gradlew bootRun

## Other info
- testFile.jpg is 'Imladris' by joewight (https://www.deviantart.com/joewight/art/Imladris-430740597). Distributed under  CC by-nc-nd (http://creativecommons.org/licenses/by-nc-nd/3.0/).
