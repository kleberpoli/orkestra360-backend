# Variables
APP_NAME=orkestra360-backend

.PHONY: help build up down test logs clean

help:
	@echo "Available Commands:"
	@echo "  make dev        - Runs the application in development mode"
	@echo "  make build      - Compiles the application and generates the Docker image"
	@echo "  make up         - Starts the application container"
	@echo "  make down       - Stops and removes the containers"
	@echo "  make test       - Runs unit and integration tests (using Maven Failsafe for ITs)"
	@echo "  make coverage   - Shows the overall test coverage via terminal (ensure you run `make test` first to generate the report)"
	@echo "  make logs       - Displays the logs in real time"
	@echo "  make clean      - Clears temporary Maven files"
	@echo "  make format     - Formats the code using Spotless plugin"

dev:
	mvn clean install -DskipTests
	mvn spring-boot:run -Dspring-boot.run.profiles=dev

build:
	docker-compose build --no-cache

up:
	docker-compose up -d

down:
	docker-compose down

test:
	mvn clean verify

coverage:
	python ./scripts/show-coverage.py

logs:
	docker logs -f $(APP_NAME)

clean:
	mvn clean -DskipTests

format:
	mvn spotless:apply
