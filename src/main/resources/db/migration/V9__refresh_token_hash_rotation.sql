DELETE FROM refresh_tokens;

ALTER TABLE refresh_tokens
    DROP INDEX uk_refresh_tokens_token,
    DROP COLUMN token,
    ADD COLUMN token_id VARCHAR(36) NOT NULL,
    ADD COLUMN token_hash VARCHAR(255) NOT NULL,
    ADD UNIQUE KEY uk_refresh_tokens_token_id (token_id);
