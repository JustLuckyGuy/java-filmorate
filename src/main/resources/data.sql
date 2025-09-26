INSERT INTO genre (name)
SELECT name
FROM (VALUES
    ('Комедия'),
    ('Драма'),
    ('Мультфильм'),
    ('Триллер'),
    ('Документальный'),
    ('Боевик')
    ) AS new_genres(name)
WHERE NOT EXISTS (SELECT 1 FROM genre WHERE genre.name = new_genres.name);

INSERT INTO mpa (code)
SELECT code
FROM (VALUES
    ('G'),
    ('PG'),
    ('PG-13'),
    ('R'),
    ('NC-17')
    ) AS new_codes(code)
WHERE NOT EXISTS (SELECT 1 FROM mpa WHERE mpa.code = new_codes.code);
