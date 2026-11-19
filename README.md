# Spring Boot crash course

A 2-hour, demo-driven introduction to Spring and Spring Boot for developers with some Java experience.
We build a small REST API for users backed by Postgres, step by step.

**Presenter script, timings and exercises: [COURSE.md](COURSE.md)**

## Requirements

- JDK 25
- Docker (Postgres runs in a container, started automatically)
- An IDE; IntelliJ IDEA is recommended (`requests.http` runs in its HTTP client)

## Run

```bash
./mvnw spring-boot:run   # starts Postgres via compose.yaml, runs Liquibase, serves http://localhost:8080
./mvnw verify            # runs all tests (Testcontainers starts its own Postgres)
```

## Steps

Each step is a git branch. Each one builds and runs on its own.

| Branch | Topic |
|-----|-------|
| `step-00` | Plain Java, no Spring: everything wired by hand |
| `step-01` | Empty project from start.spring.io |
| `step-02` | Beans and dependency injection |
| `step-03` | Configuration, `@ConfigurationProperties`, profiles |
| `step-04` / `step-04-solution` | REST controller (in-memory) + exercise: GET by id |
| `step-05` / `step-05-solution` | Postgres, Liquibase, Spring Data JDBC + exercise: new column |
| `step-06` | Validation, `@Transactional`, global error handling |
| `step-07` / `step-07-solution` | Unit, `@WebMvcTest` and Testcontainers tests + exercise: 404 test |
| `step-08` | Actuator |

```bash
git checkout -f step-04                 # jump to a step
git diff step-04 step-04-solution      # see what a step changed
docker compose down -v                  # reset the demo database
```
