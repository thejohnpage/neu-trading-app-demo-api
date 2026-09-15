# Neueda VM Deployment

The repository is the transfer mechanism into the Neueda VMs: clone/pull from GitHub, then run the checked-in scripts. No clipboard transfer is required.

## Recommended topology

The easiest reproducible reference deployment is to use the **Linux VM as the application host** and keep the demo isolated from PostgreSQL instances already used by course work.

```text
Windows VM                              Linux VM
----------                              --------
browser / API client  ----------------> Spring Boot API :8080
optional pgAdmin      -- SSH tunnel --> demo PostgreSQL container :55432
                                        Kafka :9092
                                        Docker
```

The Linux deployment creates its own PostgreSQL 16 container and persistent volume. Its host port defaults to **55432**, specifically to avoid colliding with an existing PostgreSQL service/container on 5432.

The existing PostgreSQL installations do not need to be modified.

## Linux VM — first install

Prerequisites: `git` and Docker with the Compose plugin. The script checks for them but deliberately does not use `sudo` or alter the VM's package manager.

```bash
git clone https://github.com/thejohnpage/neu-trading-app-demo-api.git
cd neu-trading-app-demo-api
chmod +x deploy/linux/*.sh
./deploy/linux/install.sh
./deploy/linux/deploy.sh
```

The install creates `.env` locally. `.env` is ignored by Git.

Useful commands:

```bash
docker compose -f deploy/docker-compose.vm.yml --env-file .env ps
docker compose -f deploy/docker-compose.vm.yml --env-file .env logs -f api
docker compose -f deploy/docker-compose.vm.yml --env-file .env down
```

Do **not** use `down -v` unless you intentionally want to delete the demo PostgreSQL volume.

### Linux redeployment

```bash
cd ~/neu-trading-app-demo-api
./deploy/linux/deploy.sh
```

That pulls `main`, rebuilds the API image, starts/updates the services and checks `/actuator/health`.

## Windows VM — native Java option

The Windows scripts are useful if the API needs to run on Windows while PostgreSQL is the existing local Windows PostgreSQL instance and Kafka runs on Linux.

Prerequisites on PATH:

- Git
- Java 21
- Maven

First install:

```powershell
git clone https://github.com/thejohnpage/neu-trading-app-demo-api.git
cd neu-trading-app-demo-api
.\deploy\windows\install.ps1
```

`install.ps1` creates `deploy\windows\vm.env.ps1`. Edit that file on the VM to match the local PostgreSQL database/user and the Linux VM address for Kafka. The file is intentionally not committed.

Then:

```powershell
.\deploy\windows\deploy.ps1
```

The deployment builds the JAR, stops the previously recorded demo API process if present, starts the new JAR, writes logs under `logs\`, and checks the health endpoint.

## Database choices

### Preferred: isolated Linux PostgreSQL container

Use the supplied Compose deployment. Default host port is 55432. Flyway creates the application schemas/tables when the API starts.

### Existing Windows PostgreSQL

Create a database and login for the demo using pgAdmin/psql according to the permissions available on the VM, then set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` in `deploy\windows\vm.env.ps1`. Flyway will create the schemas and tables.

### Existing Linux PostgreSQL

This is supported by overriding the API environment, but the isolated container is preferred for the reference demo because it avoids changing or depending on classroom databases.

## Ports

| Component | Default |
|---|---:|
| API | 8080 |
| Demo PostgreSQL host port | 55432 |
| Kafka | 9092 |
| PostgreSQL inside its container | 5432 |

All can be changed in `.env` on Linux.

## Connectivity warning

Kafka's advertised address matters if a client on the Windows VM connects to Kafka on Linux. Set `KAFKA_ADVERTISED_HOST` in Linux `.env` to the Linux VM hostname/IP reachable from Windows, then recreate Kafka:

```bash
docker compose -f deploy/docker-compose.vm.yml --env-file .env up -d --force-recreate kafka
```

If the API also runs in the Linux Compose stack, it connects internally to `kafka:9092` and does not need the host-advertised address.

## Security

These scripts are intended for the isolated training/reference environment. Default demo credentials are intentionally obvious and must not be reused for a production or internet-exposed deployment. Do not commit `.env`, `vm.env.ps1`, passwords, tokens, or private keys.
