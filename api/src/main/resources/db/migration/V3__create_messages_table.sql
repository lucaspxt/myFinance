CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    memory_id BIGINT NOT NULL
);

CREATE INDEX idx_messages_memory_id ON messages (memory_id);
