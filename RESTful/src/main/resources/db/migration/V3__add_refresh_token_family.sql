ALTER TABLE refresh_token_tbl
    ADD COLUMN family_id VARCHAR(36) NULL AFTER user_id;

UPDATE refresh_token_tbl
SET family_id = token_id
WHERE family_id IS NULL;

ALTER TABLE refresh_token_tbl
    MODIFY COLUMN family_id VARCHAR(36) NOT NULL;

CREATE UNIQUE INDEX uk_refresh_token_family_id
    ON refresh_token_tbl (family_id);
