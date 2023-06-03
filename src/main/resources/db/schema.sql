-- temporary
DROP TABLE IF EXISTS mpa, films, users, genres, films_likes, film_genres, user_friends,
                     review_likes, reviews CASCADE;

DROP INDEX IF EXISTS only_like_or_dislike;

CREATE TABLE IF NOT EXISTS mpa
(
	mpa_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name VARCHAR(128) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS films
(
	film_id LONG GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name VARCHAR(128) NOT NULL,
	description VARCHAR(200),
	release_date DATE,
	duration_minutes INTEGER,
	mpa_id INTEGER,
	FOREIGN KEY (mpa_id) REFERENCES mpa(mpa_id)
);

CREATE TABLE IF NOT EXISTS users (
	user_id LONG GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	email VARCHAR(128) NOT NULL UNIQUE,
	login VARCHAR(128) NOT NULL UNIQUE,
	name VARCHAR(128),
	birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
	genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name VARCHAR(128) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS films_likes (
	film_id LONG NOT NULL,
	user_id LONG NOT NULL,
	PRIMARY KEY (film_id, user_id),
	FOREIGN KEY (film_id) REFERENCES films(film_id),
	FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS film_genres (
	film_id LONG NOT NULL,
	genre_id INTEGER NOT NULL,
	PRIMARY KEY (film_id, genre_id),
	FOREIGN KEY (film_id) REFERENCES films(film_id),
	FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
);

CREATE TABLE IF NOT EXISTS user_friends (
	user_id LONG NOT NULL,
	friend_id LONG NOT NULL,
	PRIMARY KEY (user_id, friend_id),
	FOREIGN KEY (user_id) REFERENCES users(user_id),
	FOREIGN KEY (friend_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS reviews (
	review_id LONG GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	content VARCHAR NOT NULL,
	is_positive BOOLEAN,
	user_id LONG,
	film_id LONG,
	useful INTEGER,
	FOREIGN KEY (user_id) REFERENCES users(user_id),
	FOREIGN KEY (film_id) REFERENCES films(film_id)
);

CREATE TABLE IF NOT EXISTS review_likes (
	review_id LONG,
	user_id LONG,
	is_like BOOLEAN,
	FOREIGN KEY (review_id) REFERENCES reviews(review_id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS only_like_or_dislike ON review_likes (review_id, user_id);