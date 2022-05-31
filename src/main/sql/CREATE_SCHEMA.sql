DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS todos;

CREATE TABLE todos (
    id              SERIAL PRIMARY KEY,
    name            text,
    description     text
);

CREATE TABLE tasks (
    todo_id         integer REFERENCES todos (id) ON DELETE CASCADE,
    name            text
);
