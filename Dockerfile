FROM openjdk:8-jre-alpine

# ARG ELASTICMQ_VERSION
ENV ELASTICMQ_VERSION=v1.6.12

RUN apk add --no-cache curl ca-certificates
RUN mkdir -p /opt/elasticmq/log /opt/elasticmq/lib /opt/elasticmq/conf
RUN curl -sfLo /opt/elasticmq/lib/elasticmq.jar https://github.com/softwaremill/elasticmq/releases/download/v1.6.12/elasticmq-server-all-1.6.12.jar

COPY queue.conf /opt/elasticmq/conf/elasticmq.conf

WORKDIR /opt/elasticmq

EXPOSE 9324

ENTRYPOINT [ "/usr/bin/java", "-Dconfig.file=/opt/elasticmq/conf/elasticmq.conf", "-jar", "/opt/elasticmq/lib/elasticmq.jar" ]