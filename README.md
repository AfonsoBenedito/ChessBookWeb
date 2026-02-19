<div align="center">
  <img src="static/images/favicon.ico" width="72" alt="ChessBookWeb logo" />
  <h1>ChessBookWeb</h1>
  <p>A cloud-native, turn-based chess platform — single binary, zero dependencies</p>

  ![Go](https://img.shields.io/badge/Go-1.23-00ADD8?style=for-the-badge&logo=go&logoColor=white)
  ![SQLite](https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)
  ![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)
  ![Cloud Run](https://img.shields.io/badge/Cloud_Run-4285F4?style=for-the-badge&logo=googlecloud&logoColor=white)
  ![GitHub Actions](https://img.shields.io/badge/CI%2FCD-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)

  [![Deploy to Cloud Run](https://github.com/AfonsoBenedito/ChessBookWeb/actions/workflows/deploy.yml/badge.svg?branch=for-cloud-run)](https://github.com/AfonsoBenedito/ChessBookWeb/actions/workflows/deploy.yml)

  > **This is the `for-cloud-run` branch** — a full rewrite in Go with SQLite, built for Google Cloud Run.
  > For the original Java + MySQL implementation, see the [`main`](../../tree/main) branch.
</div>

---

## Table of Contents

- [Why a rewrite?](#why-a-rewrite)
- [Features](#features)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [CI/CD](#cicd)
- [Project Structure](#project-structure)
- [Pages](#pages)
- [Authors](#authors)

---

## Why a rewrite?

The original Java + Tomcat + MySQL stack required a full VM to run. This branch is a ground-up rewrite with a single goal: **deploy for free on Google Cloud Run**.

| | `main` (Java) | `for-cloud-run` (Go) |
|---|---|---|
| Language | Java 17 | Go 1.23 |
| Web layer | Tomcat 9 · JSP | `net/http` (stdlib) |
| Database | MySQL 8 | SQLite (embedded) |
| Image size | ~500 MB | ~12 MB |
| Infrastructure | VM + DB server | Single container |
| Live updates | AJAX polling | Server-Sent Events |
| Deployment | Manual | Automatic (push to deploy) |

---

## Features

- ♟️ Full chess engine — legal move validation, check, checkmate, stalemate
- 🏰 Special moves — castling (short & long), en passant, pawn promotion
- ⚡ Live updates — Server-Sent Events push board changes instantly to all watchers
- 👁️ Move preview — click a piece to highlight its legal squares in real time
- ⏱️ Per-player timers — move time and total time per player
- 🤝 Draw system — offer, accept, or refuse a draw mid-game
- 🏳️ Resign — concede at any point
- 🔄 Replay — step through any game move by move
- 🎨 Board flip — rotate the board to either perspective
- 🛡️ Admin dashboard — manage all players and games
- 🔐 Sessions — HMAC-SHA256 signed cookies, no server-side session store

---

## Architecture

```
┌─────────────────────────────────────┐
│           Google Cloud Run          │
│                                     │
│  ┌───────────────────────────────┐  │
│  │        Go HTTP server         │  │
│  │  ┌─────────┐  ┌───────────┐  │  │
│  │  │  chess/ │  │ handlers/ │  │  │
│  │  │ engine  │  │  + SSE    │  │  │
│  │  └─────────┘  └───────────┘  │  │
│  │         ┌────────┐           │  │
│  │         │   db/  │ SQLite    │  │
│  │         └────────┘           │  │
│  └───────────────────────────────┘  │
└─────────────────────────────────────┘
         ↑ auto-deploy on push
┌─────────────────────────────────────┐
│          GitHub Actions             │
│  build → push to Artifact Registry  │
│       → deploy to Cloud Run         │
└─────────────────────────────────────┘
```

> **Note on persistence:** Cloud Run instances are ephemeral. The SQLite database lives at `/tmp/chess.db` and resets when the instance restarts. For a production setup, this should be replaced with a persistent volume or a managed database.

---

## Getting Started

### Prerequisites

- [Go 1.23+](https://go.dev/dl/)
- [Docker](https://docs.docker.com/get-docker/) (for container workflow)

### Run locally with Go

```bash
make run
```

This creates `./data/chess.db` and starts the server at **http://localhost:8080**.

### Run locally with Docker

```bash
make docker-up    # Build image and start container
make docker-down  # Stop container
```

### All Make targets

```bash
make run          # Run with go run (hot-reload friendly)
make build        # Compile binary to ./server
make start        # Build then run the binary
make docker-up    # Build Docker image and run container
make docker-down  # Stop and remove container
make seed         # Populate DB with mock players and games
make reseed       # Wipe DB and re-seed
make clean        # Remove binary and local database
```

### Environment variables

| Variable | Default | Description |
|---|---|---|
| `DB_PATH` | `./data/chess.db` | Path to SQLite database file |
| `SESSION_SECRET` | — | Secret key for HMAC session signing (**required**) |
| `PORT` | `8080` | HTTP port |

---

## CI/CD

Every push to this branch triggers a GitHub Actions workflow that:

1. **Authenticates** to GCP via Workload Identity Federation (no stored keys)
2. **Builds** the Docker image with layer caching
3. **Pushes** to Google Artifact Registry (`europe-southwest1`)
4. **Deploys** to Google Cloud Run

To set this up from scratch, run `bash setup-gcp.sh` (requires `gcloud` authenticated as project owner), then add the three printed secrets to your GitHub repo settings.

---

## Project Structure

```
.
├── main.go                  # HTTP server, routing, template setup
├── chess/
│   ├── board.go             # Board state and move execution
│   ├── game.go              # Game logic and move conversion
│   └── types.go             # Piece types, colours, move structs
├── db/
│   ├── db.go                # SQLite init and schema
│   ├── games.go             # Game CRUD
│   └── players.go           # Player CRUD
├── handlers/
│   ├── auth.go              # Register / login / logout
│   ├── game.go              # Game page and move handling
│   ├── gamelist.go          # Game list and new game creation
│   ├── admin.go             # ManageDB dashboard
│   ├── sse.go               # Server-Sent Events broadcaster
│   ├── session.go           # HMAC-signed cookie sessions
│   └── templates.go         # Template rendering helper
├── templates/               # html/template views
├── static/                  # Embedded CSS, JS, images
├── cmd/seed/                # Mock data generator
├── Dockerfile               # Multi-stage build → ~12 MB image
├── docker-compose.yml       # Local container setup
├── setup-gcp.sh             # One-time GCP provisioning script
└── .github/workflows/
    └── deploy.yml           # CD pipeline
```

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

## Authors

| Name | Student ID |
|---|---|
| Afonso Benedito | 54937 |
| Afonso Telles | 54945 |
| Tomás Ndlate | 54970 |
