package:
	./mvnw clean package -DskipTests

dbuild:
	docker build -t tth/base-rest-api-service .

drun:
	docker run --name base-rest-api-service --env-file docker.env -p 8091:8080 tth/base-rest-api-service
