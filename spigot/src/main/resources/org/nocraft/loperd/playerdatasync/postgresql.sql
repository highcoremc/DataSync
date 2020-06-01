-- PostgreSQL Schema

CREATE TABLE "{prefix}users" (
    "id"         SERIAL PRIMARY KEY NOT NULL,
    "name"       VARCHAR(24)        NOT NULL,
    "uuid"       VARCHAR(36)        NOT NULL,
    "data"       TEXT               NOT NULL,
    "updated_at" TIMESTAMP          NOT NULL DEFAULT current_timestamp,
    "created_at" TIMESTAMP          NOT NULL DEFAULT current_timestamp
);
CREATE INDEX "{prefix}users_uuid" ON "{prefix}users" ("uuid");
