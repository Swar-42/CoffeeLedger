CREATE TABLE people (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name CHARACTER VARYING,
    bought NUMERIC(11, 2),
    paid NUMERIC(11, 2)
);

CREATE TABLE orders (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name CHARACTER VARYING,
    price NUMERIC(5, 2)
);

CREATE TABLE group_orders (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name CHARACTER VARYING
);

CREATE TABLE group_order_details (
    group_order_id INTEGER,
    person_id INTEGER,
    order_id INTEGER,
    PRIMARY KEY(group_order_id, person_id, order_id),
    FOREIGN KEY(group_order_id) REFERENCES group_orders(id) ON DELETE CASCADE,
    FOREIGN KEY(person_id) REFERENCES people(id) ON DELETE CASCADE,
    FOREIGN KEY(order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE TABLE stored_vars (
    person_to_pay INTEGER REFERENCES people(id) ON DELETE SET NULL
);

INSERT INTO stored_vars (person_to_pay) VALUES
    (NULL);

INSERT INTO people (name, bought, paid) VALUES
    ('Bob', 0, 0),
    ('Jeremy', 0, 0);