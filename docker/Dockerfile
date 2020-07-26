FROM postgres:9.6-alpine

ADD setup.sql /docker-entrypoint-initdb.d/setup.sql
ENV POSTGRES_PASSWORD=accounts