-- MySQL Schema

CREATE TABLE `{prefix}users` (
    `id`              INT AUTO_INCREMENT  NOT NULL,
    `name`            VARCHAR(24)         NOT NULL,
    `uuid`            VARCHAR(36)         NOT NULL,
    `data`            TEXT                NOT NULL,
    `updated_at`      TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `created_at`      TIMESTAMP           NOT NULL DEFAULT current_timestamp
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;

CREATE INDEX `{prefix}users_uuid` ON `{prefix}users_uuid` (`uuid`);