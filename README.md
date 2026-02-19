<div align="center">
  <img src="code/src/main/webapp/images/favicon.ico" width="72" alt="ChessBookWeb logo" />
  <h1>ChessBookWeb</h1>
  <p>A full-stack, turn-based chess platform playable in the browser</p>

  ![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
  ![Tomcat](https://img.shields.io/badge/Tomcat-9-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black)
  ![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
  ![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)

  > **This is the `main` branch** — the original Java + MySQL implementation.
  > For the cloud-native Go rewrite, see the [`for-cloud-run`](../../tree/for-cloud-run) branch.
</div>

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Pages](#pages)
- [Admin Dashboard](#admin-dashboard)
- [Authors](#authors)

---

## Features

- ♟️ Full chess engine — legal move validation, check, checkmate, stalemate
- 🏰 Special moves — castling (short & long), en passant, pawn promotion
- ⚡ Live updates — real-time board refresh via SSE; move previews via AJAX
- ⏱️ Per-player timers — move time, total time per player
- 🤝 Draw system — offer, accept, or refuse a draw mid-game
- 🏳️ Resign — concede at any point
- 🔄 Replay — step through any game move by move
- 🎨 Board flip — rotate the board to either perspective
- 🛡️ Admin dashboard — manage all players and games
- 🔐 Authentication — register and login with name + email

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Web layer | Tomcat 9 · JSP · Servlets |
| Persistence | EclipseLink JPA · MySQL 8 |
| Frontend | HTML · CSS · JavaScript |
| Build | Maven |
| Container | Docker · Docker Compose |

---

## Getting Started

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/) and Docker Compose

### Run with Make

```bash
make up        # Build images and start app + database in the background
make down      # Stop and remove containers
make logs      # Tail live logs from all services (Ctrl+C to exit)
make build     # Rebuild images without starting
make clean     # Stop everything and wipe the database volume
```

The app will be available at **http://localhost:8080**.

### Run manually

```bash
docker compose up --build -d
```

> **Note:** If port `8080` is already in use, stop the conflicting service or change the port in `docker-compose.yml`.

### Environment variables

Configuration is loaded from `.env` (copy from `.env.example`):

| Variable | Description |
|---|---|
| `DB_NAME` | MySQL database name |
| `DB_USER` | MySQL user |
| `DB_PASSWORD` | MySQL password |
| `DB_PORT` | Host port for MySQL |

---

## Pages

| Route | Description | Auth required |
|---|---|---|
| `/Registo` | Register / Login | No |
| `/GameList` | Your games dashboard | Yes |
| `/Game?Id=X` | Play or review game `X` | No |
| `/ManageDB` | Admin dashboard | Yes |
| `/Erro` | Error page | No |

---

## Gameplay

### Game List

Three panels on one page:

- **Left** — active games with opponent name, move count, and whose turn it is
- **Centre** — start a new game: search for an opponent, choose your colour (white / black / random)
- **Right** — finished game history with result, move count, and replay link

### Game

- The board is always oriented from your perspective
- Click a piece to see its legal moves highlighted, then click a destination to move
- Or type a move in algebraic notation (e.g. `e2 e4`) and press **Introduzir Jogada**
- A promotion popup appears automatically when a pawn reaches the back rank
- Use the arrow buttons to step through past moves in replay mode

---

## Admin Dashboard

Access at `/ManageDB`. Shows:

- **Partidas** — all games (active and finished) with players, date, winner, and move list
- **Jogadores** — all registered players with win/draw/loss counts; individual deletion

---

## Authors

| Name | Student ID |
|---|---|
| Afonso Benedito | 54937 |
| Afonso Telles | 54945 |
| Tomás Ndlate | 54970 |
