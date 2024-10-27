# Eventsignup Backend
© Juhani Vähä-Mäkilä (juhani@fmail.co.uk) and contributors 2024.

Licenced under EUPL-1.2 or later.

Backend microservice for event signup system. Includes Keycloak integration for authentication.

## Used technologies

- Java 21
- Springboot 3.2.x
- Apache IO Commons
- Gradle
- Postgres 17
- JUnit 5 & Mockito
- Project lombok
- Keycloak for authentication
- Quartz scheduler
- OpenApi
- Spotless for code formatting
- [Flyway](https://docs.spring.io/spring-boot/docs/2.1.7.RELEASE/reference/html/howto-database-initialization.html#howto-use-a-higher-level-database-migration-tool)

## Development

- Expects Postgres at `localhost:5432`
- Server will be at `localhost:8080`
- OpenAPI definitions are available at http://localhost:8080/v1/api-docs.<yaml | json>
- OpenAPI swagger ui is available at http://localhost:8080/swagger-ui/index.html

To run the service:

    $ ./gradlew bootRunDev

### Setting up

1. Install OpenJDK21
   1. [Windows](https://adoptium.net/temurin/releases/?os=windows&version=21)
   2. Linux: Install from your distro's repo
   3. [macOs](https://adoptium.net/temurin/releases/?os=mac&version=21)
2. Install podman or docker. See [container setup](#running-locally)
   1. Run Postgres in a container: `podman run -dt --pod new:postgres17 -e POSTGRES_PASSWORD=1234 -p 5432:5432 postgres:17.0` or `docker run -d -p 5432:5432 --name postgres17 postgres:17.0`
3. Import existing sources as a new project in your favorite IDE (e.g. IntelliJ IDEA)
4. Import Gradle project

### Development guidelines

- All text which is visible to the end user (e.g. email) must use i18n translations (messages*.properties files).
- Main development happens in `develop` branch.
  - All features and bug fixes are done in their own branches (branched off from `develop`).
  - All Pull Requests should squash commits before merge so git history of develop (and ultimately `master`) remains clean and linear.
  - Delete source branch once PR is merged.
  - NEVER force-push to `develop`. This will break everyone else's work.
- Avoid commiting directly to `develop`. Only PRs will run CI pipelines.
- Only when a new version is to be released is `develop` merged into `master`.
- Use [Semantic versioning](https://en.wikipedia.org/wiki/Software_versioning#Semantic_versioning) for releases.
  - Use Git tags (created in `master`) for releases (builds containers) and the GitHub releases functionality.
- Use GitHub Issues for all features, bugs etc.
- Public interfaces need to have unit tests, and integration tests where appropriate.
- Use Project Lombok annotations for constructors, getters, setters etc.

### Code quality

- All code, comments, documentation, names of classes and variables, log messages, etc. must be in English.
- Maximum recommended line length is 120 characters.
- Indentation must be made with 4 spaces, not tabs.
  - Continuation indent is 4.
- Line feeds must be UNIX-like (\n).
- All source files should be UTF-8, except .properties files which should be ISO-8859-1.
- Spotless is used to automatically format code (using [palantirJavaFormat](https://github.com/palantir/palantir-java-format)).
  - CI pipeline will fail if not properly formated.
- All your code should follow the Java Code Conventions regarding variable/method/class naming.
- Be [DRY](https://en.wikipedia.org/wiki/Don%27t_repeat_yourself).
- Try to use [test-driven-development](https://en.wikipedia.org/wiki/Test-driven_development).

Automatically format code with Spotless:

    $ ./gradlew spotlessApply

## Migrations with Flyway

Hibernate is able to update the database schema by itself pretty well though there are things it cannot/won't do
e.g. remove a column from a table. For these instances we use [Flyway](https://docs.spring.io/spring-boot/docs/2.1.7.RELEASE/reference/html/howto-database-initialization.html#howto-use-a-higher-level-database-migration-tool)
to migrate the database schema.

## How to run with containers only

If you just need the service up and running e.g. for front-end development or prod deployment here's how to do it.

### Running locally

Note: Never use these commands in production!

1. Install Podman or docker
   1. Podman
      1. [Windows](https://podman-desktop.io/docs/installation/windows-install)
      2. Linux: install from your distro's repo
      3. [macOs](https://podman-desktop.io/docs/installation/macos-install)
   2. Docker Desktop: [Windows](https://docs.docker.com/desktop/install/windows-install/), [Linux](https://docs.docker.com/desktop/install/linux-install/), [MacOs](https://docs.docker.com/desktop/install/mac-install/)
2. Run Mongodb
   1. `docker run -d -p 5432:5432 --name postgresql17 postgres:17.0` or
   2. `podman run -dt --pod new:eventsignup -p 8080:8080 -e POSTGRES_PASSWORD=1234 -p 5432:5432 postgres:17.0`
3. Authenticate to [GHCR](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry#authenticating-with-a-personal-access-token-classic)
4. Download .env-dev file from this repo
5. (Only for podman) `podman run -d --pod=eventsignup --env-file=path/to/.env-dev ghcr.io/asteriskiry/eventsignup_backend:latest`
   1. Note `latest` gives you the most recent CI pipeline built container which can be a dev snapshot. Use a version tag to get a specific version e.g. 1.0.0.

TODO complete docker instructions.

After initial run described above with podman the whole eventsignup system pod can be controlled like this `podman pod start|stop|restart eventsignup`.

#### Troubleshooting

If Podman gives an error try adding `--network=slirp4netns:enable_ipv6=false` to the command.

### Running in production

Prerequisite: Postgres should be already running somewhere and the database exists.

1. Create a .env file somewhere with values for all the variables listed in [environment variables](#environment-variables) table.
2. Run container 
   1. `podman run -d --pod=new:eventsignup -p 8080:8080 --env-file=path/to/.env ghcr.io/asteriskiry/eventsignup_backend:<tag>` or
   2. `docker run -d -p 8080:8080 --env-file=path/to/.env ghcr.io/asteriskiry/eventsignup_backend:<tag>`

Note
- Substitute port numbers with the port you want to run the service with (must be the same as SERVER_PORT env variable).
- Substitute `<tag>` with a version tag e.g. 1.0.0.

## Environment variables
For production these variables are needed.

|              Variable               |                        Description                        |                Example                 |
|:-----------------------------------:|:---------------------------------------------------------:|:--------------------------------------:|
|             SERVER_PORT             |           Which port the server is listening in           |                  8080                  |
|             SERVER_HOST             |                         Hostname                          |               localhost                |
|          SERVER_ENABLE_SSL          |               Whether to enable SSL support               |                  true                  |
|               DB_HOST               |               Hostname of postgresql server               |               localhost                |
|               DB_PORT               |             Which port postgresql is running              |                  5432                  |
|               DB_NAME               |                 Name of the used database                 |              databaseName              |
|             DB_USERNAME             |                User to connect to db with                 |                  user                  |
|             DB_PASSWORD             |                 Database user's password                  |                password                |
|              SMTP_HOST              |                  Hostname of SMTP server                  |               localhost                |
|              SMTP_PORT              |             Port what SMTP server listens to              |                   25                   |
|            SMTP_USERNAME            |                     SMTP servers user                     |                  user                  |
|            SMTP_PASSWORD            |                   SMTP user's password                    |                password                |
|        DEFAULT_SENDER_EMAIL         |             Email address for outgoing email              |          noreply@example.com           |
| DEFAULT_DAYS_TO_ARCHIVE_PAST_EVENTS | After how many days old events are automatically archived |                  180                   |
|         DEFAULT_IMAGE_PATH          |       Directory where uploaded pictures are stored        |                  /tmp                  |
|              BASE_URL               |              Used to generate urls in emails              |           http://example.org           |
|         KEYCLOAK_ISSUER_URI         |              Url where Keycloak can be found              | http://localhost:9090/realms/realmName |
|        KEYCLOAK_CLIENT_NAME         |                  Client name in Keycloak                  |                example                 |
|         KEYCLOAK_CLIENT_ID          |                   Client id in Keycloak                   |                example                 |
|       KEYCLOAK_CLIENT_SECRET        |                 Client secret in Keycloak                 |                example                 |
|            KEYCLOAK_URL             |                     Url for Keycloak                      |         http://localhost:9090          |
|             LOGOUT_URL              |                 Url to redirect on logout                 |         http://localhost:3000          |

## Other info
- testFile.jpg is 'Imladris' by joewight (https://www.deviantart.com/joewight/art/Imladris-430740597). 
Distributed under  CC by-nc-nd (http://creativecommons.org/licenses/by-nc-nd/3.0/).
