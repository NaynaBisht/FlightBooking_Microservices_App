@echo off

echo Starting Eureka Server...
start "Eureka Server" java -jar eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=local

timeout /t 5

echo Starting API Gateway...
start "API Gateway" java -jar api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar --spring.profiles.active=local

echo Starting Flight Service...
start "Flight Service" java -jar flight-service/target/flight-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local

echo Starting Booking Service...
start "Booking Service" java -jar booking-service/target/booking-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local

echo Starting User Service...
start "User Service" java -jar user-service/target/user-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
