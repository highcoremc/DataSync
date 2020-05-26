-- MySQL Schema

CREATE TABLE `{prefix}users` (
  `id`         INT AUTO_INCREMENT NOT NULL,
  `uuid`       VARCHAR(36)        NOT NULL,
  `server`     VARCHAR(36)        NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;
CREATE INDEX `{prefix}users_uuid` ON `{prefix}users_uuid` (`uuid`);