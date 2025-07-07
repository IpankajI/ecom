FROM maven:3-eclipse-temurin-21-alpine AS deps

WORKDIR /app
COPY pom.xml /app

RUN  mvn dependency:resolve


FROM maven:3-eclipse-temurin-21-alpine AS dev

WORKDIR /app
COPY --from=deps /root/.m2/repository /root/.m2/repository
COPY ./docker-entrypoint.sh /docker-entrypoint.sh

RUN apk add inotify-tools
RUN chmod +x /docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]
