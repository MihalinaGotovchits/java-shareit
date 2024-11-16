CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description VARCHAR(1000) NOT NULL,
    requestor_id BIGINT REFERENCES users (id),
    created TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1012) NOT NULL,
    available BOOLEAN NOT NULL,
    owner_id BIGINT REFERENCES users (id),
    request_id BIGINT REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start TIMESTAMP NOT NULL,
    end TIMESTAMP NOT NULL,
    item BIGINT REFERENCES items (id),
    booker BIGINT REFERENCES users (id),
    status varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text VARCHAR(1000) NOT NULL,
    item_id BIGINT REFERENCES items (id),
    author_id BIGINT REFERENCES users (id),
    created TIMESTAMP NOT NULL
);