INSERT INTO roles (authority) VALUES ('ADMIN'), ('USER');

INSERT INTO users (email, password)
VALUES ('admin@admin.com', '$2a$10$6JNL8yp7zMb5eubrguYKgOmgsDUcD9KUtgnyV6WXPdRb41GLmOVW2');

INSERT INTO user_role_junction (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u, roles r
WHERE u.email = 'admin@admin.com'
  AND r.authority IN ('ADMIN', 'USER');
