<div align="center">

  <img src="code/src/main/webapp/images/favicon.ico" width="90" alt="ChessBookWeb" />

  <h1>ChessBookWeb</h1>

  <p>
    A full-stack, browser-based chess platform built from scratch —<br/>
    complete engine, real-time multiplayer, timers, and an admin interface.
  </p>

  [![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
  [![Tomcat](https://img.shields.io/badge/Tomcat-9-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black)](https://tomcat.apache.org/)
  [![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
  [![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
  [![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
  [![License: MIT](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

  <br/>

  > **You are on the `main` branch** — the original Java + MySQL implementation.<br/>
  > For the cloud-native Go rewrite, see the [`for-cloud-run`](../../tree/for-cloud-run) branch.

</div>

---

## Table of Contents

- [About](#about)
- [Goals](#goals)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Routes](#routes)
- [Gameplay](#gameplay)
- [Admin Dashboard](#admin-dashboard)
- [Authors](#authors)

---

## About

**ChessBookWeb** is a fully functional online chess application built entirely from scratch as a university project for the *Construção de Sistemas de Software* course at [ISCTE-IUL](https://www.iscte-iul.pt/), within the [Licenciatura em Tecnologias de Informação](https://www.iscte-iul.pt/curso/7/licenciatura-em-tecnologias-de-informacao) programme.

Every part of the system was hand-built — from the chess rules engine in Java, to the session management, real-time board updates, and the deployment pipeline. No chess library was used; all move generation, validation, and game-state logic is original code.

The project has two independent implementations sharing the same concept:

| Branch | Stack | Storage | Deployment |
|---|---|---|---|
| [`main`](../../tree/main) | Java 17 · Tomcat 9 · JSP · Servlets | MySQL 8 | Docker Compose (WAR) |
| [`for-cloud-run`](../../tree/for-cloud-run) | Go · html/template · SSE | SQLite | Single container · ~12 MB image |

---

## Goals

The project set out to achieve the following:

- **Complete chess rules** — implement every legal move, including edge cases like castling, en passant, and pawn promotion, without relying on any external engine.
- **Real-time multiplayer** — allow two players to play against each other live in the browser, with the board updating automatically as moves are made.
- **Full game lifecycle** — handle not just playing but also draw offers, resignation, timers, and post-game replay.
- **Admin visibility** — provide an administrative interface to manage all players and ongoing/finished games.
- **Production-ready packaging** — containerise the entire stack (app + database) so anyone can run it locally with a single command.
- **Cloud-native rewrite** — explore a leaner, single-binary alternative (Go branch) deployable to serverless platforms like Google Cloud Run.

---

## Features

| | Feature | Description |
|---|---|---|
| ♟ | **Full chess engine** | Legal move generation and validation for all piece types |
| 🏰 | **Special moves** | Castling (kingside & queenside), en passant, pawn promotion |
| ⚡ | **Live updates** | Board refreshes in real time via AJAX polling — no manual refresh needed |
| ⏱ | **Per-player timers** | Move timer and cumulative time tracked independently for each player |
| 🤝 | **Draw system** | Offer, accept, or refuse a draw at any point during the game |
| 🏳 | **Resign** | Concede the game at any time |
| 🔄 | **Replay** | Step through any completed game move by move |
| 🎨 | **Board flip** | Rotate the board to either player's perspective |
| 🔍 | **Move input** | Click a piece to see legal moves highlighted, or type in algebraic notation |
| 🛡 | **Admin dashboard** | View, manage, and delete all players and games |
| 🔐 | **Authentication** | Register and log in with name + email (no password required) |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Web layer | Apache Tomcat 9 · JSP · Servlets |
| Persistence | EclipseLink JPA · MySQL 8 |
| Frontend | HTML · CSS · Vanilla JavaScript |
| Build tool | Apache Maven |
| Containerisation | Docker · Docker Compose |

---

## Getting Started

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/) and Docker Compose (or [Docker Desktop](https://www.docker.com/products/docker-desktop/))
- `make` (optional — all commands have a manual equivalent)

### 1 · Configure environment

Copy the example and fill in your values:

```bash
cp .env.example .env
```

| Variable | Description | Default |
|---|---|---|
| `DB_NAME` | MySQL database name | `chess` |
| `DB_USER` | MySQL user | — |
| `DB_PASSWORD` | MySQL password | — |
| `DB_PORT` | Host port exposed for MySQL | `3306` |

### 2 · Start the stack

```bash
make up          # Build images and start app + database in the background
```

The app will be available at **[http://localhost:8080](http://localhost:8080)**.

### Other Make targets

```bash
make down        # Stop and remove containers
make logs        # Tail live logs from all services (Ctrl+C to stop)
make build       # Rebuild images without starting containers
make clean       # Stop everything and wipe the database volume
```

### Manual alternative

```bash
docker compose up --build -d
```

> **Port conflict?** If `8080` is already in use, stop the conflicting service or change the `ports` mapping in `docker-compose.yml`.

---

## Routes

| Route | Page | Auth required |
|---|---|---|
| `/Registo` | Register · Login | No |
| `/GameList` | Your games dashboard | Yes |
| `/Game?Id=X` | Play or review game `X` | Yes |
| `/ManageDB` | Admin dashboard | Yes |
| `/Erro` | Error page | No |

---

## Gameplay

### Game List

Three panels on a single page:

- **Left** — active games showing opponent name, move count, and whose turn it is
- **Centre** — start a new game: search for an opponent and choose your colour (White / Black / Random)
- **Right** — finished game history with result, move count, and a link to replay

### Playing a game

- The board is always oriented from your perspective
- Click any of your pieces to highlight its legal destination squares, then click a destination to move
- Alternatively, type a move in algebraic notation (e.g. `e2 e4`) and press **Introduzir Jogada**
- A promotion dialog appears automatically when a pawn reaches the back rank
- Use the arrow buttons to step through all past moves in replay mode

### Timers

Each player has an individual move timer and a cumulative total. Both are displayed live on the game page and are recorded at the end of the game.

---

## Admin Dashboard

Accessible at `/ManageDB`. Provides two views:

- **Partidas** — all games (active and finished) with players, start date, winner, and full move list
- **Jogadores** — all registered players with their win / draw / loss record and the option to delete an account

---

## Authors

Built by students of **Licenciatura em Tecnologias de Informação** at ISCTE-IUL.

| Name | Student ID |
|---|---|
| Afonso Benedito | 54937 |
| Afonso Telles | 54945 |
| Tomás Ndlate | 54970 |

---

<div align="center">
  <img src="code/src/main/webapp/images/favicon.ico" width="32" alt="" />
  <br/>
  <sub>ChessBookWeb — LTI · ISCTE-IUL · 2024</sub>
</div>
