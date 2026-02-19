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
	docker compose -f docker-compose.cloud-run.yml up --build

## Stop and remove Docker Compose containers
docker-down:
	docker compose -f docker-compose.cloud-run.yml down

## Remove built binary and local DB
clean:
	rm -f server
	rm -f data/chess.db data/chess.db-shm data/chess.db-wal
