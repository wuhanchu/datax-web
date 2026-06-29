# AGENTS.md

## Project

DataX-Web: web management console for Alibaba DataX data synchronization. Fork of xxl-job scheduler adapted for DataX jobs.

## Build

Java 8 required. On this machine:
```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-8.jdk/Contents/Home \
  mvn package -pl datax-executor -am -DskipTests   # executor jar only
mvn package -DskipTests                             # full build
```

`maven.test.skip=true` is set in root pom — tests don't run unless you override.

## Modules

| Module | Role | Entry point |
|---|---|---|
| `datax-admin` | Spring Boot web console, scheduler, DB layer | `DataXAdminApplication` |
| `datax-executor` | Job executor, runs DataX processes | `DataXExecutorApplication` |
| `datax-core` | Shared lib: job models, RPC client, utils | — |
| `datax-rpc` | Netty-based RPC framework (from xxl-job) | — |
| `datax-assembly` | Packaging only | — |

## Runtime

- **Java 8**, **Python 3.6** (base image `hametan/datax-web:2.1.2`)
- **Database**: PostgreSQL (`org.postgresql.Driver`). Configure via env vars `DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DATABASE`, `DB_SCHEMA`.
- Admin HikariCP: max pool size 10, connection timeout 120s.
- Base image paths: DataX at `/opt/datax/`, jars at `/opt/datax-executor-2.1.2.jar`.
- Executor needs `ADDRESSES` env pointing to admin service.

## Concurrency control

`ExecutorJobHandler` uses a shared `Semaphore` to limit concurrent DataX processes.

```bash
--datax.executor.maxConcurrent=2   # default 10
```

The Semaphore is **per JVM**. If you have N executor pods, total concurrent DataX = N × maxConcurrent.

`updateMaxConcurrent()` replaces the Semaphore object and **leaks permits** — do not call it dynamically. To change the limit, restart the pod with a new `--datax.executor.maxConcurrent` value.

Monitor via:
```bash
curl http://localhost:2020/executor/maxConcurrent
# {"maxConcurrent":2,"available":2}
```

## Docker

Multi-stage build in `Dockerfile` — builds jar in-container, no local Maven needed.

```bash
docker build -t ccr.ccs.tencentyun.com/baofeng/datax-web:master .
docker push ccr.ccs.tencentyun.com/baofeng/datax-web:master
```

VS Code tasks in `.vscode/tasks.json`: "Build Executor Image" and "Build & Push Executor Image".

## Key gotchas

- System-scope JARs in `datax-admin/src/main/lib/` (Oracle `ojdbc6`, SQL Server `sqljdbc4`). Required for admin build.
- `packages/` dir is created by `maven-assembly-plugin` on `mvn install` — `mvn package` alone won't produce it.
- DataX JSON config uses AES encryption for datasource passwords (`datasource.aes.key` in admin yml).
- `ExecutorController` (`/executor/maxConcurrent` GET) is new — not in the original `:2.1.2` image tag.
- No linter, formatter, or typecheck configured beyond Maven/Java compiler.
- Swagger UI at `/doc.html`.
