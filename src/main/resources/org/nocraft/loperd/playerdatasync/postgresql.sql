-- PostgreSQL Schema

CREATE TABLE "{prefix}users" (
  "id"              SERIAL PRIMARY KEY  NOT NULL,
  "name"            VARCHAR(24)         NOT NULL,
  "uuid"            VARCHAR(36)         NOT NULL,
  "health"          DOUBLE PRECISION    NOT NULL,
  "foodLevel"       SMALLINT            NOT NULL,
  "xpLevel"         SMALLINT            NOT NULL,
  "xpProgress"      FLOAT               NOT NULL,
  "gameMode"        VARCHAR(10)         NOT NULL,
  "potionEffects"   TEXT                NOT NULL,
  "inventory"       TEXT                NOT NULL,
  "enderChest"      TEXT                NOT NULL,
  "heldItemSlot"    INTEGER             NOT NULL,
  "flight"          BOOLEAN             NOT NULL
);
CREATE INDEX "{prefix}users_uuid" ON "{prefix}users" ("uuid");