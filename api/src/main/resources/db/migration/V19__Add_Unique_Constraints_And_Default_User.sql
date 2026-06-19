-- Remove duplicatas de stores (mantém a de menor id)
DELETE FROM stores s1 USING stores s2
WHERE s1.id > s2.id AND s1.name = s2.name;

-- Remove duplicatas de products (mantém a de menor id)
DELETE FROM products p1 USING products p2
WHERE p1.id > p2.id AND p1.name = p2.name;

-- Adiciona unique constraints
ALTER TABLE stores ADD CONSTRAINT uk_stores_name UNIQUE (name);
ALTER TABLE products ADD CONSTRAINT uk_products_name UNIQUE (name);

-- Insere usuário default para testes
INSERT INTO users (name, email, password, role, created_at)
VALUES ('Cliente Teste', 'cliente@teste.com', 'senha123', 'CUSTOMER', NOW())
ON CONFLICT (email) DO NOTHING;
