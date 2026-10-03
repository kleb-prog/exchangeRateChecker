# AGENTS.md

## Project overview

ExchangeRateChecker is a Java 17 Spring Boot application for monitoring currency exchange rates. It provides a REST API, a Telegram bot, scheduled rate checks, PostgreSQL persistence, Redis-related infrastructure, and a React frontend in `frontend/`.

The backend entry point is `src/main/java/com/lebedev/exchangeRate/ExchangeRateApplication.java`.

## Repository structure

- `src/main/java/com/lebedev/exchangeRate/` - Spring Boot backend
- `src/main/resources/` - application configuration and runtime resources
- `src/test/java/` - backend tests
- `frontend/` - React client
- `build.gradle` - Gradle build and dependency configuration
- `private.properties` - local secrets and machine-specific settings; never commit it

## Development workflow

Before changing code:

1. Inspect the relevant classes, tests, configuration, and build file.
2. Keep the change focused and follow existing package, naming, and dependency patterns.
3. Add or update tests for changed behavior.
4. Run the narrowest relevant test first, then the full test suite when practical.

Useful commands on Windows:

```powershell
./gradlew.bat test
./gradlew.bat build
```

The frontend can be checked from `frontend/` with its existing npm scripts. Do not change frontend code for a backend-only task.

## Coding conventions

- Use Java 17 features only when they improve clarity.
- Keep controllers thin; put business logic in services.
- Prefer constructor injection for Spring components.
- Reuse existing DTOs, entities, repositories, and exception-handling patterns.
- Keep API changes backward-compatible unless the task explicitly requires a breaking change.
- Add comments only for non-obvious decisions or complex logic.

## Configuration and secrets

- Do not put API keys, bot tokens, passwords, or database credentials in tracked files.
- Keep local secrets in `private.properties` or environment variables, according to the existing configuration approach.
- Treat `application.properties` as shared configuration. Review `spring.config.import` and the active profile before changing configuration.
- Never print secrets in logs, tests, commits, or agent responses.

## Testing expectations

- Tests must be deterministic and independent of a developer's local machine where possible.
- Do not require live Telegram, exchange-rate, LLM, database, or Redis services in unit tests unless the test explicitly belongs to an integration-test suite.
- Mock external APIs and verify failure paths, validation, and security-sensitive behavior.
- If a test cannot be run because a required service or secret is unavailable, report that clearly.

## Safety rules for changes

- Preserve user changes already present in the working tree.
- Do not use destructive Git commands such as `git reset --hard` or `git checkout --`.
- Do not delete files or rewrite unrelated code to make a task pass.
- Do not add a new library when an existing project dependency or local abstraction is sufficient.
- Before finishing, review the diff and summarize changed files and verification results.

## LLM-agent integration

When adding an LLM feature to the application:

- Isolate provider-specific calls behind a service interface.
- Keep provider keys and model names configurable; do not hard-code them in Java code.
- Add timeouts, bounded retries, structured error handling, and request-size limits.
- Validate and constrain tool inputs on the server side; never let model output bypass authorization.
- Avoid sending secrets or unnecessary personal data to the provider.
- Add tests using mocked provider responses, including timeout, rate-limit, malformed-response, and refusal cases.
