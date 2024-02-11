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
    user_id INTEGER,
    order_id INTEGER,
    PRIMARY KEY(group_order_id, user_id, order_id),
    FOREIGN KEY(group_order_id) REFERENCES group_orders(id),
    FOREIGN KEY(user_id) REFERENCES people(id),
    FOREIGN KEY(order_id) REFERENCES orders(id)
)