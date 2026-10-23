-- MySQL 8.x. Apply ONCE using a schema migration procedure before deployment.
-- This project currently has no Flyway/Liquibase configuration in develop.
CREATE TABLE chatbot_approval_ledger (
    approval_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    approval_token VARCHAR(36) NOT NULL,
    request_hash VARCHAR(64) NOT NULL,
    proposal_hash VARCHAR(64) NOT NULL,
    decision VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    result_json TEXT NULL,
    completed_at DATETIME NULL,
    PRIMARY KEY (approval_id),
    INDEX idx_approval_token (approval_token),
    CONSTRAINT uk_approval_user_token UNIQUE (user_id, approval_token)
) ENGINE=InnoDB;

CREATE TABLE chatbot_approval_item (
    approval_item_id BINARY(16) NOT NULL,
    approval_id BINARY(16) NOT NULL,
    proposal_item_id BINARY(16) NOT NULL,
    target_type VARCHAR(10) NOT NULL,
    target_id BINARY(16) NOT NULL,
    PRIMARY KEY (approval_item_id),
    CONSTRAINT uk_approval_proposal_item UNIQUE (approval_id, proposal_item_id),
    CONSTRAINT fk_approval_item_ledger
        FOREIGN KEY (approval_id) REFERENCES chatbot_approval_ledger(approval_id)
) ENGINE=InnoDB;
