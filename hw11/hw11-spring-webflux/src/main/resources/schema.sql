CREATE TABLE authors (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL
);

CREATE TABLE genres (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author_id BIGINT NOT NULL,
    CONSTRAINT fk_book_author FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE RESTRICT
);

CREATE TABLE books_genres (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    CONSTRAINT fk_bg_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CONSTRAINT fk_bg_genre FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE,
    CONSTRAINT uq_book_genre UNIQUE (book_id, genre_id)
);

CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    book_id BIGINT NOT NULL,
    CONSTRAINT fk_comment_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);
