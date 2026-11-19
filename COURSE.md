# Spring & Spring Boot Crash Course: presenter script

**Length:** 2 hours. **Format:** about 70% live demo, 30% short exercises.
**Audience:** developers who know some Java but have never used Spring.
**Result:** a REST API for users, stored in Postgres, with Liquibase migrations, validation, error handling and tests.

Every step is a **git branch** (`step-00` … `step-08`, plus `-solution` branches for the exercises). Each one builds and runs, so if a live demo goes wrong you can recover with one command:

```bash
git checkout -f step-05          # jump to a step (throws away local edits!)
git diff step-04-solution step-05  # show exactly what a step changed (great on the projector)
```

> This file is included in every step branch, so it stays open in your IDE while you switch steps.
> Don't commit on the step branches during the demo; `checkout -f` simply discards your live edits.

---

## Agenda

| Time | Min | Branch | Topic | Exercise |
|------|-----|-----|-------|----------|
| 0:00 | 5  | none | Why are we here? Spring vs Spring Boot | |
| 0:05 | 10 | `step-00` | **Plain Java, no Spring**: feel the pain of wiring by hand | |
| 0:15 | 5  | `step-01` | Create a project with Spring Initializr | |
| 0:20 | 12 | `step-02` | Beans and DI: annotation-based, Java-based (`@Bean`), XML (legacy) | |
| 0:32 | 10 | `step-03` | Configuration: `@Value`, `@ConfigurationProperties`, profiles | |
| 0:42 | 17 | `step-04` | REST controllers | ✅ GET by id (8 min) |
| 0:59 | 21 | `step-05` | Database: Postgres, Liquibase, Spring Data JDBC | ✅ new column (8 min) |
| 1:20 | 12 | `step-06` | Validation, `@Transactional`, error handling | |
| 1:32 | 14 | `step-07` | Testing: unit, `@WebMvcTest`, `@SpringBootTest` | ✅ 404 test (6 min) |
| 1:46 | 4  | `step-08` | Actuator | |
| 1:50 | 10 | none | Wrap-up, what's next, Q&A | |

If you run late, shorten step 08 to a one-minute mention and cut the step-07 exercise.

---

## Preparation (the day before)

**You:**
- [ ] JDK 25, IntelliJ IDEA (Community is fine), Docker running (`docker info` works without `sudo`).
- [ ] `docker pull postgres:17`. Venue Wi-Fi is never your friend.
- [ ] `git checkout step-08 && ./mvnw verify` downloads every dependency into `~/.m2` and runs all tests once.
- [ ] `docker compose down -v` gives you a clean, empty database before the session.
- [ ] `git checkout -f step-00`
- [ ] Font size in the IDE: big. Then bigger.

**Participants** (send this a few days before):
- [ ] JDK 25 + IntelliJ IDEA + Docker Desktop (or Docker Engine) installed.
- [ ] Clone this repo and run `./mvnw verify` once on `main`. If it passes, they're ready.
- [ ] Anyone without Docker can still follow and do the step-04 exercise.

---

## 0:00 Intro (5 min, no code)

- **Spring Framework** (since 2003): a dependency injection container, web MVC, transactions, data access… Powerful, but historically lots of XML and configuration.
- **Spring Boot**: an opinionated layer on top that adds:
  - **Starters**: one dependency pulls in a working, version-matched set of libraries.
  - **Auto-configuration**: "you have Postgres on the classpath? Then I'll create a DataSource for you."
  - **Embedded server**: Tomcat is inside your app; you get one runnable jar and no app-server installation.
  - **Externalised config**: `application.yml`, environment variables, profiles.
- It's the most common Java backend stack, so this knowledge transfers.
- Plan for today: first we build a tiny app **without** Spring, then we let Spring take the pain away step by step.

Optional whiteboard sketch: `HTTP → Controller → Service → Repository → DB`, the layers we'll build today.

---

## 0:05 `step-00`: plain Java, no Spring (10 min)

**Goal:** before showing any Spring, make everyone *feel* the problems Spring solves.

```bash
git checkout -f step-00
```

Run `Main.main()` (green ▶ in IntelliJ), then open http://localhost:8080/api/users (or use `requests.http`).

Walk through `Main.java` top to bottom. It has 4 blocks, and each one is a problem Spring will solve:

| Block in `Main.java` | The pain | Spring's answer (step) |
|---|---|---|
| 1. Read `app.properties` by hand | Parse, convert types, defaults, no per-environment overrides | `application.yml`, `@ConfigurationProperties`, profiles (step-03) |
| 2. Wiring with `new`, `new`, `new`… | Order matters; one new constructor parameter means editing `main`; swapping `EmailNotificationService` for a fake in tests means editing code | `@Component` + constructor injection (step-02) |
| 3. Demo data | n/a | n/a |
| 4. `HttpServer` + `UserHttpHandler` | Build JSON strings by hand, check HTTP methods yourself, no path variables, no POST body parsing (try it: POST returns 405), no validation, no error format | `@RestController`, Jackson, `@Valid`, `@RestControllerAdvice` (steps 04 and 06) |

And there's no database at all. With plain JDBC you'd add connection handling, SQL strings, mapping rows to objects, transactions and schema changes (all of step-05).

Then introduce the two key terms:
- **Inversion of Control (IoC):** instead of *you* calling `new` in `main`, a **container** (Spring's `ApplicationContext`) creates the objects. You describe *what* you need; it builds and connects everything.
  - Analogy: you don't build your own car to get to work; you order a taxi. You say where to go, someone else provides it.
- **Dependency Injection (DI):** *how* the container hands you your dependencies, through the constructor. Look at `WelcomeService`: it already *asks* for a `NotificationService` in its constructor instead of creating one. That's DI; we just did the injecting by hand in `main`.
  Objects managed by the container are called **beans**.

Leave `Main.java` open in a tab; you'll compare against it in step-02.

---

## 0:15 `step-01`: Spring Initializr (5 min)

**Goal:** see how little you need to start.

Demo:
1. Open **https://start.spring.io** live. Choose Maven, Java, Spring Boot 4.1.x, Java 25, group `com.example`, artifact `crashcourse`, dependency **Spring Web**. Click *Explore* to show the generated `pom.xml`.
2. Switch to the repo: `git checkout -f step-01`. It's the same thing. Notice the plain-Java classes from step-00 are gone; we'll bring them back *the Spring way* in step-02.
3. Walk through the files:
   - `pom.xml`: the **parent** pins versions of *everything*. Notice there's no version on the starters. `spring-boot-starter-webmvc` is a **starter**.
   - `CrashCourseApplication.java`: `@SpringBootApplication` = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`. **Component scan starts in this package**, a classic beginner pitfall when classes live outside it.
   - `application.yml`: almost empty. Everything has a sensible default.
4. Run it: ▶ in IntelliJ or `./mvnw spring-boot:run`. Point at the log line `Tomcat started on port 8080`. Open http://localhost:8080 to show the "Whitelabel Error Page": the server works, there's just nothing mapped yet.

---

## 0:20 `step-02`: beans and dependency injection (12 min)

```bash
git checkout -f step-02
git diff step-01 step-02 --stat
```

Files: `notification/NotificationService.java`, `EmailNotificationService.java`, `SmsNotificationService.java`, `WelcomeService.java`, `AppConfig.java`, `CrashCourseApplication.java`, and `docs/applicationContext.xml` (read-only example).

Demo flow:
1. **Compare with step-00.** The notification classes are back:
   `git diff step-00 step-02 -- src/main/java/com/example/crashcourse/notification`
   `WelcomeService` gained an **annotation** (`@Service`). `EmailNotificationService` is unchanged apart from a comment. All the `new` lines from step-00's `Main.java` are gone. Spring does them now.
2. **Annotation-based: `@Component` / `@Service`.** Put an annotation on the class and Spring creates *one* instance (a singleton) at startup. Here: `WelcomeService`.
3. **Constructor injection** in `WelcomeService`. One constructor means Spring calls it and passes in matching beans. No `@Autowired` needed. Stress that **we depend on the interface** `NotificationService`, not on the email class.
4. **Java-based: `@Configuration` + `@Bean`** in `AppConfig`. Two reasons to use it, one example each:
   - `clock()`: `java.time.Clock` is a JDK class, so **we can't annotate it**.
   - `chosenNotificationService(...)`: **creating it needs logic**. It reads the `notification.channel` setting and returns email *or* SMS, and `SmsNotificationService` needs a constructor argument (the sender name) Spring couldn't guess. An annotation on a class can't make that choice.
5. **Run it.** The `CommandLineRunner` prints the welcome email and lists our beans. Then switch the implementation **without touching code**:
   `./mvnw spring-boot:run -Dspring-boot.run.arguments=--notification.channel=sms` now prints `[SMS from CrashCourse …]`.
   Compare with step-00's `Main.java`: *"want SMS? Edit this line."* Point out:
   - Bean names are the class name in lowerCamelCase.
   - `...$$SpringCGLIB$$0` means Spring sometimes wraps beans in a **proxy** (subclass). We'll need that idea for `@Transactional` in step 06.

Live "break it" moment (2 min, very effective):
- Put `@Component` on `EmailNotificationService` and run. Now there are two `NotificationService` beans: `emailNotificationService` (the `@Component`) *and* `chosenNotificationService` (from `AppConfig`). Startup fails with **"required a single bean, but 2 were found"**. Read the error message out loud: Spring error messages are usually helpful.
- Mention the fix: `@Primary` on one of them (or `@Qualifier` at the injection point). Then remove the `@Component` again.
- Remove `@Service` from `WelcomeService` and the error becomes **"required a bean of type WelcomeService that could not be found"**. Put it back.

### Three ways to define beans

| Style | How | Use it for | In this repo |
|---|---|---|---|
| **Annotation-based** | `@Component`, `@Service`, `@Repository`, `@RestController` on the class + component scanning | **Your own classes.** The default today, used ~90% of the time | `WelcomeService` (and every controller/service later) |
| **Java-based** | `@Configuration` class with `@Bean` methods | Classes you **can't annotate** (JDK, libraries), or when creating the object **needs logic** | `AppConfig` |
| **XML-based** | `<bean id="…" class="…">` in an XML file | **Legacy only.** Read it, don't write new code with it | `docs/applicationContext.xml` |

Open `docs/applicationContext.xml` for 1 minute: the same beans described in XML. The app doesn't load it. It's there so XML doesn't surprise anyone in an older project. The beans and the injection are identical; only the *way you describe them* changed. You can mix all three styles in one app.

Takeaway: *you declare the pieces (annotations, `@Bean` methods, or XML); Spring assembles them.*

---

## 0:32 `step-03`: configuration and profiles (10 min)

```bash
git checkout -f step-03
git diff step-02 step-03
```

Demo:
1. `application.yml`: the `app:` section holds *our own* settings.
2. `AppProperties` record + `@ConfigurationProperties(prefix = "app")`. The YAML binds to a typed object. Kebab-case `welcome-message` maps to `welcomeMessage` (relaxed binding). The record is injected into `WelcomeService` like any other bean.
3. **`@Value` for a single setting.** In `AppConfig`, `@Value("${notification.channel:email}")` replaces the raw `Environment` lookup from step-02. The part after `:` is the default.

   | | `@Value("${…}")` | `@ConfigurationProperties` |
   |---|---|---|
   | Good for | One or two values in one place | A group of related settings |
   | Type safety | It's just a string key; typos only show up at runtime | Typed record, IDE auto-completion |
   | Where you'll see it | Everywhere in existing code | Recommended for new code |
4. Run it. The message comes from YAML.
5. **Profiles:** `application-dev.yml` overrides values and turns on debug logging.
   `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` (or IntelliJ run config → *Active profiles: dev*). Show the `[DEV]` message, now sent as an **SMS** because the dev profile sets `notification.channel: sms`, and the extra DEBUG line.
6. **Overriding without changing files** (works for *any* property):
   `./mvnw spring-boot:run -Dspring-boot.run.arguments=--app.welcome-message=Hi`
   As an environment variable it's `APP_WELCOMEMESSAGE=Hi`. This is how real deployments configure apps (Docker, Kubernetes, CI).
7. **Auto-configuration peek:** run with `--debug` (`-Dspring-boot.run.arguments=--debug`) and scroll to the **CONDITIONS EVALUATION REPORT**: "Positive matches" means configured because a class was on the classpath. This is "Boot magic" explained: it's just `if (class present && no bean defined by user) → create default bean`.

Takeaway: *same code, different config per environment; never hard-code URLs, passwords or ports.*

---

## 0:42 `step-04`: REST controllers (17 min = 9 demo + 8 exercise)

```bash
git checkout -f step-04
```

Files: `user/User.java` (record), `user/UserService.java` (in-memory map), `user/UserController.java`, `requests.http`.

Demo:
1. **Records** (quick Java refresher): `record User(Long id, String username, String email)` gives you a constructor, accessors `user.email()`, `equals`, `toString`. Jackson turns it into JSON for free.
2. **`@RestController` + `@RequestMapping("/api/users")`**
   - `@GetMapping` returns a `List<User>`, which becomes a JSON array.
   - `@PostMapping` + `@RequestBody` turns the JSON body into a `User`. `@ResponseStatus(CREATED)` sets 201.
   - `@DeleteMapping("/{id}")` + `@PathVariable` takes a value from the URL.
3. **Layering:** the controller only does HTTP ↔ Java. Logic lives in `UserService`. The service also calls `WelcomeService` from step 02, so the beans compose.
4. Run it and use **`requests.http`** in IntelliJ (click ▶ next to each request): create, list, delete. Watch the `[EMAIL …]` line in the console. (Or use curl/Postman.)
5. Restart the app and the users are **gone** (in-memory). That's the motivation for step 05.

### ✅ Exercise 1 (8 min): GET by id
> Add `GET /api/users/{id}` that returns one user, or **404** if it doesn't exist.
> Hint: add `Optional<User> findById(Long id)` to the service; in the controller return `ResponseEntity<User>`: `ResponseEntity.ok(user)` / `ResponseEntity.notFound().build()`.

Solution: `git diff step-04 step-04-solution`

---

## 0:59 `step-05`: database with Postgres, Liquibase and Spring Data JDBC (21 min = 13 demo + 8 exercise)

```bash
git checkout -f step-05
git diff step-04-solution step-05 --stat
```

Demo:
1. **New starters in `pom.xml`**: `data-jdbc`, `liquibase`, the `postgresql` driver, and `spring-boot-docker-compose`. Starters again: add a dependency and Boot configures it.
2. **`compose.yaml`**: a Postgres container. `spring-boot-docker-compose` **starts it automatically** on `spring-boot:run` and wires the datasource. That's why there is **no URL or password** in `application.yml`. (In production you'd set `spring.datasource.url/username/password` via environment variables.)
3. **Liquibase**: `db/changelog/db.changelog-master.yaml` → `changes/001-create-users-table.yaml`.
   - The database schema is *versioned code*, reviewed in PRs like any other code.
   - On startup Liquibase runs each **changeset exactly once**, and records it in the `DATABASECHANGELOG` table.
   - **Golden rule: never edit a changeset that has already run anywhere.** Add a new one. (Liquibase stores a checksum and fails at startup if an applied changeset changed.)
4. **Entity**: `@Table("users")` + `@Id` on the record. `id == null` means INSERT, `id != null` means UPDATE.
5. **Repository**: `interface UserRepository extends ListCrudRepository<User, Long>`. *There is no implementation class.* Spring generates it. `findByEmail(String email)` is a **derived query**, built from the method name.
6. **`UserService`**: the map is replaced by the repository. **The controller did not change at all.** That's what layering buys you.
7. Run it. Show the log: Docker Compose starting Postgres, then `Running Changeset: …001-create-users-table…`. Create users, **restart**, and they're still there.
8. Optional: look into the DB:
   `docker compose exec postgres psql -U demo -d crashcourse -c 'select * from users'`
   `... -c 'select id, filename from databasechangelog'`

### ✅ Exercise 2 (8 min): add a column the right way
> Add a `fullName` to users, stored in a `full_name` column.
> 1. Create `changes/002-add-full-name-to-users.yaml` with an `addColumn` changeset and include it in the master file.
> 2. Add `String fullName` to the `User` record and fix the compile errors.
> 3. Restart and see Liquibase run *only* 002.

Solution: `git diff step-05 step-05-solution`

Pitfall to mention: trying to *edit* 001 instead causes a checksum validation error on startup. That's on purpose.

---

## 1:20 `step-06`: validation, transactions, error handling (12 min)

```bash
git checkout -f step-06
git diff step-05-solution step-06 --stat
```

Demo:
1. **DTO** `CreateUserRequest`: what clients may send (no `id`). It's separate from `User` (the DB shape), so the API and the database can change independently.
2. **Bean Validation:** `@NotBlank`, `@Email`, `@Size` on the record, and `@Valid` in the controller. Invalid input never reaches your code. Run the *invalid input* request in `requests.http` and get a **400** listing each bad field.
3. **Exceptions instead of `ResponseEntity` juggling:** the service throws `UserNotFoundException` / `EmailAlreadyTakenException`. It knows nothing about HTTP.
4. **`@RestControllerAdvice`** (`GlobalExceptionHandler`): one place maps exceptions to HTTP status codes (404 / 409 / 400) as standard `ProblemDetail` JSON (RFC 9457).
5. **`@Transactional`** on `create`: check email, insert, send the welcome mail, all in one DB transaction. If anything throws a `RuntimeException`, the INSERT is **rolled back**. It works because of the proxy from step 02: Spring wraps the method in "begin … commit/rollback".
   - Pitfall: calling a `@Transactional` method from *inside the same class* bypasses the proxy, so there's no transaction.
6. Demo requests: duplicate email gives 409, unknown id gives 404.

---

## 1:32 `step-07`: testing (14 min = 8 demo + 6 exercise)

```bash
git checkout -f step-07
```

Three kinds of tests, from fast to slow (show them in this order):

| Test | What starts | Speed | File |
|------|-------------|-------|------|
| Plain unit test | Nothing, just `new UserService(mock, mock)` | ms | `UserServiceTest` |
| Web slice `@WebMvcTest` | Controllers, JSON, validation, advice. No DB | ~1 s | `UserControllerTest` |
| Full `@SpringBootTest` | Everything + real Postgres via **Testcontainers** | seconds | `UserApiIntegrationTest` |

Points:
- The unit test needs **no Spring at all**. That's the payoff of constructor injection.
- `@MockitoBean` puts a mock into the Spring context in place of the real service.
- `MockMvc` sends fake HTTP requests; `jsonPath("$.username")` checks the response JSON.
- `TestcontainersConfiguration` + `@ServiceConnection`: each test run gets a fresh Postgres container, and Liquibase runs against it. The same migrations run in tests and in production.
- Run `./mvnw verify` (or right-click → Run tests in IntelliJ).

### ✅ Exercise 3 (6 min): 404 test
> In `UserControllerTest`, add a test: when `userService.getById(99L)` throws `UserNotFoundException`, `GET /api/users/99` returns **404**.
> Hint: `when(...).thenThrow(new UserNotFoundException(99L))`.

Solution: `git diff step-07 step-07-solution`

---

## 1:46 `step-08`: Actuator (4 min)

```bash
git checkout -f step-08
```

- One starter (`spring-boot-starter-actuator`) gives you production endpoints.
- `GET /actuator/health` shows UP/DOWN, including the DB check. Kubernetes and load balancers use this.
- `GET /actuator/beans` lists every bean in the container. Compare it with our tiny list from step 02: hundreds, mostly from auto-configuration.
- `GET /actuator/metrics/http.server.requests` shows request counts and timings.
- ⚠️ We expose a lot for the demo. In production, expose as little as possible and secure it.

---

## 1:50 Wrap-up (10 min)

Recap in one sentence per step:
1. **IoC/DI**: Spring creates and wires your objects (beans); you ask for them in the constructor.
2. **Boot**: starters + auto-configuration + embedded server = a running app with almost no setup.
3. **Config**: `application.yml` + profiles + env vars, bound to typed `@ConfigurationProperties`.
4. **Web**: `@RestController`, `@GetMapping`/`@PostMapping`, `@PathVariable`, `@RequestBody`.
5. **Data**: Liquibase owns the schema; Spring Data repositories are interfaces you don't implement.
6. **Robustness**: `@Valid`, `@RestControllerAdvice`, `@Transactional`.
7. **Tests**: unit, then slice, then full with Testcontainers.

Where to go next:
- Spring Security (authentication/authorization)
- Spring Data JPA (the more common ORM; compare with Data JDBC)
- `RestClient` for calling other HTTP APIs
- Official guides: https://spring.io/guides · Reference docs: https://docs.spring.io/spring-boot/

Homework idea: add `PUT /api/users/{id}` with validation, a `findByUsername` derived query, and tests for both.

---

## Cheat sheet (print / share)

| Annotation | Meaning |
|------------|---------|
| `@SpringBootApplication` | Entry point: config + auto-config + component scan from this package down |
| `@Component` / `@Service` / `@Repository` | "Make a bean of this class" (the names document the layer) |
| `@Configuration` + `@Bean` | Make a bean from a method (classes you don't own, or creation needs logic) |
| `@Primary` / `@Qualifier("name")` | Pick one of several beans of the same type |
| `@Value("${key:default}")` | Inject a single config value |
| `@ConfigurationProperties(prefix="app")` | Bind `app.*` config to a typed object |
| `@RestController` | Class handles HTTP; return values become JSON |
| `@RequestMapping("/path")` | Base URL for a controller |
| `@GetMapping` `@PostMapping` `@PutMapping` `@DeleteMapping` | Map an HTTP method + path to a Java method |
| `@PathVariable` / `@RequestParam` / `@RequestBody` | Read from URL path / query string / JSON body |
| `@ResponseStatus(HttpStatus.CREATED)` | Set the response status |
| `@Valid` + `@NotBlank`, `@Email`, `@Size` … | Validate input → 400 on failure |
| `@RestControllerAdvice` + `@ExceptionHandler` | Global exception → HTTP response mapping |
| `@Transactional` | Run method in a DB transaction; roll back on RuntimeException |
| `@Table` / `@Id` | Map a record/class to a table / primary key (Spring Data) |
| `@SpringBootTest` | Test with the full application context |
| `@WebMvcTest(X.class)` | Test only the web layer for controller X |
| `@MockitoBean` | Replace a bean with a Mockito mock in a Spring test |

## Troubleshooting during the session

| Symptom | Fix |
|---------|-----|
| `Port 8080 already in use` | An old run is still alive: stop it in IntelliJ, or `-Dspring-boot.run.arguments=--server.port=8081` |
| Docker/compose errors on startup | Is Docker running? `docker info`. Podman users: set `DOCKER_HOST` to the Podman socket |
| Liquibase `checksum` / `validation failed` | Someone edited an applied changeset. Revert it, or for the demo DB just reset: `docker compose down -v` |
| Weird DB state after jumping between steps | `docker compose down -v` (wipes the demo database) |
| `required a bean of type … could not be found` | Missing `@Component`/`@Service`, or the class is outside the `com.example.crashcourse` package |
| Tests fail with "Could not find a valid Docker environment" | Testcontainers needs Docker running |
