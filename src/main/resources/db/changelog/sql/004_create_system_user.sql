INSERT INTO auth.users (email, password, registered_at, last_login_at, last_activity_at)
VALUES ('${SYSTEM_USER_EMAIL}', '${SYSTEM_USER_PASSWORD}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

INSERT INTO auth.user_roles (user_id, role_id)
SELECT u.id, 'ROLE_SYSTEM'
FROM auth.users u
WHERE u.email = '${SYSTEM_USER_EMAIL}'
ON CONFLICT DO NOTHING;

INSERT INTO tbank.tokens (user_id, token, token_name)
SELECT u.id, '${TBANK_SYSTEM_TOKEN}', 'System Token'
FROM auth.users u
WHERE u.email = '${SYSTEM_USER_EMAIL}'
ON CONFLICT (token) DO NOTHING;

INSERT INTO tbank.active_tokens (user_id, token_id)
SELECT u.id, t.id
FROM auth.users u
         JOIN tbank.tokens t ON t.user_id = u.id
WHERE u.email = '${SYSTEM_USER_EMAIL}'
  AND t.token_name = 'System Token'
ON CONFLICT (user_id) DO NOTHING;
