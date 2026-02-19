.PHONY: run stop build docker-up docker-down clean

DB_PATH  ?= ./data/chess.db
PORT     ?= 8080
SECRET   ?= dev-secret

## Run directly with Go (foreground, Ctrl+C to stop)
run:
	mkdir -p data
	DB_PATH=$(DB_PATH) PORT=$(PORT) SESSION_SECRET=$(SECRET) go run .

## Build binary then run it (foreground, Ctrl+C to stop)
build:
	go build -o server .

## Run the built binary (foreground, Ctrl+C to stop)
start: build
	mkdir -p data
	DB_PATH=$(DB_PATH) PORT=$(PORT) SESSION_SECRET=$(SECRET) ./server

## Run with Docker Compose (background, use 'make docker-down' to stop)
docker-up:
	docker compose up --build

## Stop and remove Docker Compose containers
docker-down:
	docker compose down

## Populate the DB with mock players and games (run after 'make run' has created the DB)
seed:
	DB_PATH=$(DB_PATH) go run ./cmd/seed

## Wipe the DB and re-seed in one step
reseed: clean seed

## Remove built binary and local DB
clean:
	rm -f server
	rm -f data/chess.db data/chess.db-shm data/chess.db-wal
