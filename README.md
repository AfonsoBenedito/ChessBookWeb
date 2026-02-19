<div align="center">

<img src="static/images/favicon.ico" width="96" alt="ChessBookWeb" />

# ChessBookWeb

**A cloud-native, turn-based chess platform — single binary, zero dependencies.**

Play, watch, and review chess games in real time directly from your browser.

<br/>

[![Deploy to Cloud Run](https://github.com/AfonsoBenedito/ChessBookWeb/actions/workflows/deploy.yml/badge.svg?branch=for-cloud-run)](https://github.com/AfonsoBenedito/ChessBookWeb/actions/workflows/deploy.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
![Go](https://img.shields.io/badge/Go-1.23-00ADD8?logo=go&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-embedded-003B57?logo=sqlite&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-~12MB_image-2CA5E0?logo=docker&logoColor=white)
![Cloud Run](https://img.shields.io/badge/Google_Cloud_Run-deployed-4285F4?logo=googlecloud&logoColor=white)

> **`for-cloud-run` branch** — full rewrite in Go + SQLite, built to run for free on Google Cloud Run.
> The original Java + Tomcat + MySQL implementation lives on the [`main`](../../tree/main) branch.

</div>

---

## Table of Contents

- [Overview](#overview)
- [Why a rewrite?](#why-a-rewrite)
- [Features](#features)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [CI/CD](#cicd)
- [Project Structure](#project-structure)
- [Pages](#pages)
- [Authors](#authors)

---

## Overview

ChessBookWeb started as a university project in 2022 — a full-stack chess web app where two registered players can challenge each other, play turn-by-turn, and review their game history move by move.

The goal of this branch was to take the original Java application and rethink it from the ground up with one constraint: **it must run on Google Cloud Run at zero cost**. That meant no VM, no managed database, no idle servers — just a single, self-contained Go binary that boots in milliseconds and sleeps when nobody is playing.

<div align="center">
  <img src="static/images/info_4.png" width="160" alt="ChessBookWeb mascot" />
</div>

---

## Why a rewrite?

The original Java + Tomcat + MySQL stack required a persistent VM and a separate database server to run. This branch replaces all of that with a single Go binary and an embedded SQLite database.

| | `main` (Java) | `for-cloud-run` (Go) |
|:--|:--:|:--:|
| Language | Java 17 | Go 1.23 |
| Web layer | Tomcat 9 · JSP | `net/http` (stdlib) |
| Database | MySQL 8 | SQLite (embedded) |
| Docker image size | ~500 MB | ~12 MB |
| Infrastructure | VM + DB server | Single container |
| Live board updates | AJAX polling (300ms) | Server-Sent Events |
| Deployment | Manual | Automatic (push to deploy) |
| Session storage | Server-side | HMAC-SHA256 signed cookies |

---

## Features

**Game engine**
- ♟️ Full legal move validation — check, checkmate, stalemate detection
- 🏰 Special moves — castling (kingside & queenside), en passant, pawn promotion
- 👁️ Move preview — click any piece to highlight its valid squares instantly
- 🔄 Replay — step through every move of any completed game

**Live experience**
- ⚡ Real-time updates via [Server-Sent Events](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events) — board changes pushed instantly to all watchers, no polling
- ⏱️ Per-player timers — individual move time and cumulative game time

**Player controls**
- 🤝 Draw system — offer, accept, or refuse a draw mid-game
- 🏳️ Resign — concede at any point
- 🎨 Board flip — view the board from either colour's perspective

**Platform**
- 🔐 Stateless sessions — HMAC-SHA256 signed cookies, no server-side session store needed
- 🛡️ Admin dashboard — manage all players and games

---

## Architecture

```
┌──────────────────────────────────────────┐
│            Google Cloud Run              │
│                                          │
│  ┌────────────────────────────────────┐  │
│  │          Go HTTP Server            │  │
│  │                                    │  │
│  │  ┌──────────┐   ┌──────────────┐  │  │
│  │  │  chess/  │   │  handlers/   │  │  │
│  │  │  engine  │   │  + SSE hub   │  │  │
│  │  └──────────┘   └──────────────┘  │  │
│  │                                    │  │
│  │         ┌──────────────┐           │  │
│  │         │     db/      │  SQLite   │  │
│  │         └──────────────┘           │  │
│  └────────────────────────────────────┘  │
└──────────────────────────────────────────┘
              ▲  auto-deploy on git push
┌──────────────────────────────────────────┐
│              GitHub Actions              │
│                                          │
│  checkout → auth (WIF) → docker build    │
│  → push to Artifact Registry             │
│  → deploy to Cloud Run                   │
└──────────────────────────────────────────┘
```

> **Persistence note:** Cloud Run instances are ephemeral — the SQLite database lives at `/tmp/chess.db` and resets on cold start. This is intentional for a zero-cost deployment. For a production setup, mount a persistent volume or swap SQLite for a managed database.

---

## Getting Started

### Prerequisites

- [Go 1.23+](https://go.dev/dl/)
- [Docker](https://docs.docker.com/get-docker/) *(optional, for container workflow)*

### Run locally with Go

```bash
make run
```

Starts the server at **http://localhost:8080** and creates `./data/chess.db` automatically.

### Run locally with Docker

```bash
make docker-up    # Build image and start container
make docker-down  # Stop and remove container
```

### Seed with mock data

```bash
make seed     # Populate DB with sample players and games
make reseed   # Wipe DB and re-seed from scratch
```

### All Makefile targets

| Target | Description |
|:--|:--|
| `make run` | Run with `go run` (no compile step) |
| `make build` | Compile binary to `./server` |
| `make start` | Build then run the binary |
| `make docker-up` | Build Docker image and start container |
| `make docker-down` | Stop and remove container |
| `make seed` | Populate DB with mock players and games |
| `make reseed` | Wipe DB and re-seed |
| `make clean` | Remove binary and local database |

### Environment variables

| Variable | Required | Default | Description |
|:--|:--:|:--|:--|
| `SESSION_SECRET` | ✅ | — | Secret key for HMAC-SHA256 session signing |
| `DB_PATH` | | `./data/chess.db` | Path to the SQLite database file |
| `PORT` | | `8080` | HTTP listen port |

---

## CI/CD

Every push to this branch triggers a GitHub Actions pipeline that:

1. **Authenticates** to GCP via [Workload Identity Federation](https://cloud.google.com/iam/docs/workload-identity-federation) — no stored service account keys
2. **Builds** a multi-stage Docker image with layer caching (`~12 MB` final size)
3. **Pushes** to Google Artifact Registry (`europe-southwest1`)
4. **Deploys** to Google Cloud Run with zero downtime

### First-time GCP setup

```bash
bash setup-gcp.sh
```

Run this once from a machine with `gcloud` authenticated as project owner. It provisions the Artifact Registry repository, service account, and Workload Identity Pool, then prints the three secrets to add to your GitHub repo:

| GitHub Secret | Description |
|:--|:--|
| `WIF_PROVIDER` | Workload Identity Federation provider resource name |
| `WIF_SERVICE_ACCOUNT` | Service account email for deployments |
| `SESSION_SECRET` | Secret key for HMAC session signing |

---

## Project Structure

```
.
├── main.go                  # HTTP server entry point, routing, embedded assets
├── chess/
│   ├── board.go             # Board state, move execution, check detection
│   ├── game.go              # Game logic, move conversion
│   └── types.go             # Piece types, colours, move structs
├── db/
│   ├── db.go                # SQLite init and schema migrations
│   ├── games.go             # Game CRUD operations
│   └── players.go           # Player CRUD operations
├── handlers/
│   ├── auth.go              # Register / login / logout
│   ├── game.go              # Game page and move handling
│   ├── gamelist.go          # Game list and new game creation
│   ├── admin.go             # Admin dashboard
│   ├── sse.go               # Server-Sent Events broadcaster
│   ├── session.go           # HMAC-signed cookie sessions
│   └── templates.go         # Template rendering helpers
├── templates/               # html/template views (server-rendered)
├── static/                  # Embedded CSS, JavaScript, images
├── cmd/seed/                # Dev tool: populate DB with mock data
├── Dockerfile               # Multi-stage build → distroless, ~12 MB
├── docker-compose.yml       # Local container setup
├── setup-gcp.sh             # One-time GCP provisioning script
├── Makefile                 # Developer shortcuts
└── .github/workflows/
    └── deploy.yml           # CD pipeline (build → push → deploy)
```

---

## Pages

| Route | Page | Auth |
|:--|:--|:--:|
| `/Registo` | Register & Login | — |
| `/GameList` | Your games dashboard | ✅ |
| `/Game?Id=X` | Play or replay game `X` | — |
| `/ManageDB` | Admin dashboard | ✅ |
| `/Erro` | Error page | — |

---

## Authors

Built at [ISCTE – Instituto Universitário de Lisboa](https://www.iscte-iul.pt/) in 2022.

| Name | Student ID |
|:--|:--|
| Afonso Benedito | 54937 |
| Afonso Telles | 54945 |
| Tomás Ndlate | 54970 |

---

<div align="center">

Released under the [MIT License](LICENSE) · © 2022 Afonso Benedito

</div>
