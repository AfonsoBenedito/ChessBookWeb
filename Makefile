.PHONY: up down logs build clean

## Start the app and database (builds images if needed)
up:
	docker compose up --build -d

## Stop the app and database
down:
	docker compose down

## Show live logs (Ctrl+C to exit)
logs:
	docker compose logs -f

## Build images without starting
build:
	docker compose build

## Stop and remove containers, networks, and database volume
clean:
	docker compose down -v
