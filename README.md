# Eventsignup Backend
© Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2024.

Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.

Backend microservice for event signup system. Includes Keycloak integration for authentication.

## Used technologies

- Java 21
- Springboot 3.2.x
- Apache IO Commons
- Gradle
- MongoDB 5.0.5
- JUnit 5 & Mockito
- Project lombok
- Keycloak for authentication
- Quartz scheduler
- OpenApi
- Spotless for code formatting

## Development

- Import existing sources as a new project in your favorite IDE (e.g. IntelliJ IDEA)
- Import Gradle project
- Expects Mongodb at localhost:27017
- Server will be at localhost:8080
- OpenAPI definitions are available at http://localhost:8080/v1/api-docs.<yaml | json>
- OpenAPI swagger ui is available at http://localhost:8080/swagger-ui/index.html
- All text which is visible to the end user (e.g. email) must use i18n translations (messages*.properties files)
- All features and bug fixes are done in their own branches
- All Pull Requests should squash commits before merge so git history of develop (and master) remains clean and linear

Format code with Spotless:

    $ ./gradlew spotlessApply

To run:

    $ ./gradlew bootRun

## Environment variables
For production these variables are needed.

|              Variable               |                        Description                        |                Example                 |
|:-----------------------------------:|:---------------------------------------------------------:|:--------------------------------------:|
|             SERVER_PORT             |           Which port the server is listening in           |                  8080                  |
|             SERVER_HOST             |                         Hostname                          |               localhost                |
|          SERVER_ENABLE_SSL          |               Whether to enable SSL support               |                  true                  |
|             MONGO_HOST              |                Hostname of mongodb server                 |               localhost                |
|             MONGO_PORT              |               Which port mongodb is running               |                 27017                  |
|         MONGO_DATABASE_NAME         |                 Name of the used database                 |              databaseName              |
|           MONGO_USERNAME            |                User to connect to db with                 |                  user                  |
|           MONGO_PASSWORD            |                 Database user's password                  |                password                |
|              SMTP_HOST              |                  Hostname of SMTP server                  |               localhost                |
|              SMTP_PORT              |             Port what SMTP server listens to              |                   25                   |
|            SMTP_USERNAME            |                     SMTP servers user                     |                  user                  |
|            SMTP_PASSWORD            |                   SMTP user's password                    |                password                |
|        DEFAULT_SENDER_EMAIL         |             Email address for outgoing email              |          noreply@example.com           |
| DEFAULT_DAYS_TO_ARCHIVE_PAST_EVENTS | After how many days old events are automatically archived |                  180                   |
|         DEFAULT_IMAGE_PATH          |       Directory where uploaded pictures are stored        |                  /tmp                  |
|              BASE_URL               |              Used to generate urls in emails              |           http://exapmle.org           |
|         KEYCLOAK_ISSUER_URI         |              Url where Keycloak can be found              | http://localhost:9090/realms/realmName |
|        KEYCLOAK_CLIENT_NAME         |                  Client name in Keycloak                  |                example                 |
|         KEYCLOAK_CLIENT_ID          |                   Client id in Keycloak                   |                example                 |
|       KEYCLOAK_CLIENT_SECRET        |                 Client secret in Keycloak                 |                example                 |
|            KEYCLOAK_URL             |                     Url for Keycloak                      |         http://localhost:9090          |
|             LOGOUT_URL              |                 Url to redirect on logout                 |         http://localhost:3000          |

## Other info
- testFile.jpg is 'Imladris' by joewight (https://www.deviantart.com/joewight/art/Imladris-430740597). 
Distributed under  CC by-nc-nd (http://creativecommons.org/licenses/by-nc-nd/3.0/).
