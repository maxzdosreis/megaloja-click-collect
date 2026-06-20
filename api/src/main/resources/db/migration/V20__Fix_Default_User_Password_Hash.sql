-- Atualiza a senha do usuário padrão para um hash BCrypt válido
-- A migration V19 inseriu a senha em texto plano ('senha123'), o que impede o login
-- pois o AuthenticationService usa BCryptPasswordEncoder.matches()
UPDATE users
SET password = '$2b$12$HbM7ad.Y.uOs8PpsnyzPwuoUZmJxkC/x0HXAz0bPfDF0a5C6vfi0m'
WHERE email = 'cliente@teste.com' AND password = 'senha123';
