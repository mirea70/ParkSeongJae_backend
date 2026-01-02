-- 테이블 생성

CREATE TABLE IF NOT EXISTS `customer` (
  `customer_id` bigint NOT NULL,
  `name` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `update_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `account` (
  `account_id` bigint NOT NULL,
  `customer_id` bigint NOT NULL,
  `bank_code` varchar(255) NOT NULL,
  `account_number` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `balance` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `closed_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `transfer` (
  `transfer_id` bigint NOT NULL,
  `from_account_id` bigint NOT NULL,
  `to_account_id` bigint NOT NULL,
  `amount` bigint NOT NULL,
  `fee` bigint NOT NULL,
  `transferred_at` datetime(6) NOT NULL,
  PRIMARY KEY (`transfer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `account_transaction` (
  `account_transaction_id` bigint NOT NULL,
  `account_id` bigint NOT NULL,
  `transfer_id` bigint DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `transfer_type` varchar(255) NOT NULL,
  `amount` bigint NOT NULL,
  `balance_after` bigint NOT NULL,
  `transacted_at` datetime(6) NOT NULL,
  PRIMARY KEY (`account_transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 왜래키 제약 조건
ALTER TABLE `account` ADD CONSTRAINT `FK_account_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`);
ALTER TABLE `transfer` ADD CONSTRAINT `FK_transfer_from_account` FOREIGN KEY (`from_account_id`) REFERENCES `account` (`account_id`);
ALTER TABLE `transfer` ADD CONSTRAINT `FK_transfer_to_account` FOREIGN KEY (`to_account_id`) REFERENCES `account` (`account_id`);
ALTER TABLE `account_transaction` ADD CONSTRAINT `FK_transactions_account` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`);
ALTER TABLE `account_transaction` ADD CONSTRAINT `FK_transactions_transfer` FOREIGN KEY (`transfer_id`) REFERENCES `transfer` (`transfer_id`);

-- 인덱스
CREATE INDEX `idx_account_txn_stats` ON `account_transaction` (`account_id`, `type`, `transacted_at`);
CREATE INDEX `idx_transfer_from_date` ON `transfer` (`from_account_id`, `transferred_at`);
CREATE INDEX `idx_transfer_to_date` ON `transfer` (`to_account_id`, `transferred_at`);


-- 초기 고객 데이터
INSERT INTO `customer` (`customer_id`, `name`, `created_at`, `update_at`, `deleted_at`) VALUES
(1, '홍길동', NOW(), NOW(), NULL),
(2, '김철수', NOW(), NOW(), NULL),
(3, '이영희', NOW(), NOW(), NULL);
