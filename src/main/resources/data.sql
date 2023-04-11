MERGE INTO mpa KEY (mpa_id) VALUES (1, 'G');
MERGE INTO mpa KEY (mpa_id) VALUES (2, 'PG');
MERGE INTO mpa KEY (mpa_id) VALUES (3, 'PG-13');
MERGE INTO mpa KEY (mpa_id) VALUES (4, 'R');
MERGE INTO mpa KEY (mpa_id) VALUES (5, 'NC-17');

MERGE INTO GENRES KEY (genre_id) VALUES (1, 'Комедия');
MERGE INTO GENRES KEY (genre_id) VALUES (2, 'Драма');
MERGE INTO GENRES KEY (genre_id) VALUES (3, 'Мультфильм');
MERGE INTO GENRES KEY (genre_id) VALUES (4, 'Триллер');
MERGE INTO GENRES KEY (genre_id) VALUES (5, 'Документальный');
MERGE INTO GENRES KEY (genre_id) VALUES (6, 'Боевик');