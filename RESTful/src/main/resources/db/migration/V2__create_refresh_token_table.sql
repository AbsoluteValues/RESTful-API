CREATE TABLE IF NOT EXISTS refresh_token_tbl (
    token_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(10) NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (token_id),
    INDEX idx_refresh_token_user_id (user_id),
    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id) REFERENCES user_tbl (id)
        ON DELETE CASCADE
);
