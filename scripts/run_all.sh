#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o nounset
set -o pipefail

pkill -9 -f spring-pricehunt || echo "Не удалось отключить ни одно приложение"

docker-compose kill || echo "Никакие контейнеры docker не запущены"

echo "Запускаем grafana, prometheus, tracing сервера"
docker-compose up -d grafana-server prometheus-server tracing-server

echo "Ждём запуска приложений"
mkdir -p target
nohup java -jar spring-pricehunt-config-server/target/*.jar --server.port=8888 --spring.profiles.active=chaos-monkey > target/config-server.log 2>&1 &
echo "Ждём запуска config сервера"
sleep 20
nohup java -jar spring-pricehunt-discovery-server/target/*.jar --server.port=8761 --spring.profiles.active=chaos-monkey > target/discovery-server.log 2>&1 &
echo "Ждём запуска discovery сервера"
sleep 20
nohup java -jar spring-pricehunt-api-gateway/target/*.jar --server.port=8080 --spring.profiles.active=chaos-monkey > target/gateway-service.log 2>&1 &
nohup java -jar spring-pricehunt-auth-service/target/*.jar --server.port=8081 --spring.profiles.active=chaos-monkey > target/auth-service.log 2>&1 &
nohup java -jar spring-pricehunt-assortment-service/target/*.jar --server.port=8082 --spring.profiles.active=chaos-monkey > target/auth-service.log 2>&1 &
nohup java -jar spring-pricehunt-pleerru-service/target/*.jar --server.port=8083 --spring.profiles.active=chaos-monkey > target/admin-server.log 2>&1 &
nohup java -jar spring-pricehunt-admin-server/target/*.jar --server.port=9090 --spring.profiles.active=chaos-monkey > target/admin-server.log 2>&1 &
nohup java -jar spring-pricehunt-tracing-server/target/*.jar --server.port=9411 --spring.profiles.active=chaos-monkey > target/tracing-server.log 2>&1 &
echo "Ждём запуска всех сервисов"
sleep 60
