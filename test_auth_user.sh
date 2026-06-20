#!/bin/bash
# Script de testes — Autenticação JWT e CRUD de Usuários
# Uso: chmod +x test_auth_user.sh && ./test_auth_user.sh

BASE="http://localhost:8080"
PASS=0
FAIL=0
SKIP=0
RESP_FILE="/tmp/_resp_auth.json"

SUFIXO=$(date +%s)
EMAIL_1="cliente${SUFIXO}@teste.com"
EMAIL_2="cliente2${SUFIXO}@teste.com"
EMAIL_3="cliente3${SUFIXO}@teste.com"
SENHA="123456"

check() {
    local desc="$1" expected="$2" actual="$3"
    if [ "$expected" = "$actual" ]; then
        echo "  ✅ $desc (HTTP $actual)"
        PASS=$((PASS + 1))
    else
        echo "  ❌ $desc (esperado $expected, obtido $actual)"
        if [ -f "$RESP_FILE" ] && [ -s "$RESP_FILE" ]; then
            echo "     Resposta: $(cat "$RESP_FILE")"
        fi
        FAIL=$((FAIL + 1))
    fi
}

check_role() {
    local desc="$1" expected_role="$2"
    local actual_role
    actual_role=$(echo "$BODY" | jq -r '.user.role // empty')
    if [ "$expected_role" = "$actual_role" ]; then
        echo "  ✅ $desc (role = $actual_role)"
        PASS=$((PASS + 1))
    else
        echo "  ❌ $desc (esperado $expected_role, obtido $actual_role)"
        if [ -f "$RESP_FILE" ] && [ -s "$RESP_FILE" ]; then
            echo "     Resposta: $(cat "$RESP_FILE")"
        fi
        FAIL=$((FAIL + 1))
    fi
}

skip() {
    local desc="$1"
    echo "  ⏭️  $desc (pulado)"
    SKIP=$((SKIP + 1))
}

echo ""
echo "=============================================="
echo "  TESTES — AUTENTICAÇÃO JWT E USUÁRIOS"
echo "=============================================="
echo ""

# ===== BLOCO 1: HAPPY PATH — AUTENTICAÇÃO =====
echo "─── BLOCO 1: HAPPY PATH — AUTENTICAÇÃO ───"

echo "1. POST /auth/register — criar usuário"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/register" \
  -H "Content-Type: application/json" \
  -d "$(jq -n --arg email "$EMAIL_1" --arg senha "$SENHA" '{name: "Cliente Teste", email: $email, password: $senha}')")
BODY=$(cat "$RESP_FILE")
TOKEN=$(echo "$BODY" | jq -r '.token // empty')
REFRESH_TOKEN=$(echo "$BODY" | jq -r '.refreshToken // empty')
USER_ID=$(echo "$BODY" | jq -r '.user.id // empty')
check "Register" 201 "$HTTP"

echo "2. POST /auth/register — verificar role padrão CUSTOMER"
check_role "Role default" "CUSTOMER"

echo "3. POST /auth/register — 3º email (para teste de duplicidade)"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/register" \
  -H "Content-Type: application/json" \
  -d "$(jq -n --arg email "$EMAIL_3" --arg senha "$SENHA" '{name: "Cliente Três", email: $email, password: $senha}')")
BODY=$(cat "$RESP_FILE")
check "Register 3º email" 201 "$HTTP"

echo "4. POST /auth/login — login com credenciais válidas"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d "$(jq -n --arg email "$EMAIL_1" --arg senha "$SENHA" '{email: $email, password: $senha}')")
BODY=$(cat "$RESP_FILE")
TOKEN_LOGIN=$(echo "$BODY" | jq -r '.token // empty')
REFRESH_LOGIN=$(echo "$BODY" | jq -r '.refreshToken // empty')
check "Login" 200 "$HTTP"

echo "5. POST /auth/refresh — refresh token válido"
if [ -n "$REFRESH_LOGIN" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/refresh" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --arg rt "$REFRESH_LOGIN" '{refreshToken: $rt}')")
  BODY=$(cat "$RESP_FILE")
  NOVO_TOKEN=$(echo "$BODY" | jq -r '.token // empty')
  check "Refresh token" 200 "$HTTP"
else
  skip "Refresh token (REFRESH_LOGIN vazio)"
fi

echo "6. GET /users (autenticado) — listar usuários"
if [ -n "$TOKEN_LOGIN" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X GET "$BASE/users?page=0&size=10" \
    -H "Authorization: Bearer $TOKEN_LOGIN")
  BODY=$(cat "$RESP_FILE")
  check "GET /users autenticado" 200 "$HTTP"
else
  skip "GET /users (TOKEN_LOGIN vazio)"
fi

# ===== BLOCO 2: HAPPY PATH — CRUD USUÁRIOS =====
echo ""
echo "─── BLOCO 2: HAPPY PATH — CRUD USUÁRIOS ───"

echo "7. GET /users/$USER_ID (autenticado)"
if [ -n "$USER_ID" ] && [ -n "$TOKEN_LOGIN" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X GET "$BASE/users/$USER_ID" \
    -H "Authorization: Bearer $TOKEN_LOGIN")
  BODY=$(cat "$RESP_FILE")
  check "GET /users/$USER_ID" 200 "$HTTP"
else
  skip "GET /users/$USER_ID (IDs vazios)"
fi

echo "8. PUT /users/$USER_ID (autenticado) — atualizar nome e email"
NOVO_EMAIL="cliente_alt${SUFIXO}@teste.com"
if [ -n "$USER_ID" ] && [ -n "$TOKEN_LOGIN" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X PUT "$BASE/users/$USER_ID" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN_LOGIN" \
    -d "$(jq -n --arg email "$NOVO_EMAIL" '{name: "Cliente Alterado", email: $email}')")
  BODY=$(cat "$RESP_FILE")
  check "PUT /users/$USER_ID" 200 "$HTTP"

  # Re-login com o novo email para obter token válido (PUT alterou o email)
  if [ "$HTTP" = "200" ]; then
    HTTP_LOGIN=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/login" \
      -H "Content-Type: application/json" \
      -d "$(jq -n --arg email "$NOVO_EMAIL" --arg senha "$SENHA" '{email: $email, password: $senha}')")
    BODY_LOGIN=$(cat "$RESP_FILE")
    TOKEN_LOGIN=$(echo "$BODY_LOGIN" | jq -r '.token // empty')
  fi
else
  skip "PUT /users/$USER_ID (IDs vazios)"
fi

echo "9. GET /users?email=$NOVO_EMAIL (autenticado) — filtro por email"
if [ -n "$TOKEN_LOGIN" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X GET "$BASE/users?email=$NOVO_EMAIL&page=0&size=10" \
    -H "Authorization: Bearer $TOKEN_LOGIN")
  BODY=$(cat "$RESP_FILE")
  check "GET /users filtro por email" 200 "$HTTP"
else
  skip "GET /users filtro (TOKEN vazio)"
fi

# ===== BLOCO 3: PROTEÇÃO 401 — ENDPOINTS SEM TOKEN =====
echo ""
echo "─── BLOCO 3: PROTEÇÃO 401 — ENDPOINTS SEM TOKEN ───"

echo "11. GET /users — sem token"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" "$BASE/users?page=0&size=10")
check "GET /users sem token" 401 "$HTTP"

echo "12. GET /users/1 — sem token"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" "$BASE/users/1")
check "GET /users/1 sem token" 401 "$HTTP"

echo "13. POST /users — sem token"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/users" \
  -H "Content-Type: application/json" \
  -d "$(jq -n '{name: "Invasor", email: "invasor@teste.com", password: "123456"}')")
check "POST /users sem token" 401 "$HTTP"

echo "14. PUT /users/1 — sem token"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X PUT "$BASE/users/1" \
  -H "Content-Type: application/json" \
  -d '{"name":"X"}')
check "PUT /users/1 sem token" 401 "$HTTP"

echo "15. DELETE /users/1 — sem token"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X DELETE "$BASE/users/1")
check "DELETE /users/1 sem token" 401 "$HTTP"

# ===== BLOCO 4: TOKEN INVÁLIDO/EXPIRADO =====
echo ""
echo "─── BLOCO 4: TOKEN INVÁLIDO/EXPIRADO ───"

echo "16. GET /users — token inválido 'Bearer xyz123'"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X GET "$BASE/users?page=0&size=10" \
  -H "Authorization: Bearer xyz123")
check "GET /users token inválido" 401 "$HTTP"

echo "17. GET /users — header sem prefixo 'Bearer '"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X GET "$BASE/users?page=0&size=10" \
  -H "Authorization: xyz123")
check "GET /users sem prefixo Bearer" 401 "$HTTP"

echo "18. POST /auth/refresh — refresh token inválido"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/refresh" \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"token_invalido"}')
check "POST /auth/refresh token inválido" 401 "$HTTP"

# ===== BLOCO 5: CREDENCIAIS INVÁLIDAS =====
echo ""
echo "─── BLOCO 5: CREDENCIAIS INVÁLIDAS ───"

echo "19. POST /auth/login — senha errada"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d "$(jq -n --arg email "$EMAIL_1" '{email: $email, password: "senha_errada"}')")
check "Login senha errada" 401 "$HTTP"

echo "20. POST /auth/login — email inexistente"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"nao_existe@teste.com","password":"123456"}')
check "Login email inexistente" 401 "$HTTP"

echo "21. POST /auth/login — email vazio"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"","password":"123456"}')
check "Login email vazio" 400 "$HTTP"

# ===== BLOCO 6: VALIDAÇÃO (400 BAD REQUEST) =====
echo ""
echo "─── BLOCO 6: VALIDAÇÃO (400 BAD REQUEST) ───"

echo "22. POST /auth/register — body vazio"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/register" \
  -H "Content-Type: application/json" \
  -d '{}')
check "Register body vazio" 400 "$HTTP"

echo "23. POST /auth/register — email inválido"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/register" \
  -H "Content-Type: application/json" \
  -d "$(jq -n '{name: "X", email: "email_invalido", password: "123456"}')")
check "Register email inválido" 400 "$HTTP"

echo "24. POST /auth/register — senha < 6 caracteres"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/register" \
  -H "Content-Type: application/json" \
  -d "$(jq -n '{name: "X", email: "curta@teste.com", password: "123"}')")
check "Register senha curta" 400 "$HTTP"

echo "25. POST /auth/register — nome vazio"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/register" \
  -H "Content-Type: application/json" \
  -d "$(jq -n '{name: "", email: "sem_nome@teste.com", password: "123456"}')")
check "Register nome vazio" 400 "$HTTP"

echo "26. POST /auth/login — body vazio"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d '{}')
check "Login body vazio" 400 "$HTTP"

echo "27. POST /auth/refresh — body vazio"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/refresh" \
  -H "Content-Type: application/json" \
  -d '{}')
check "Refresh body vazio" 400 "$HTTP"

# ===== BLOCO 7: DUPLICIDADE (422) =====
echo ""
echo "─── BLOCO 7: DUPLICIDADE (422 UNPROCESSABLE ENTITY) ───"

echo "28. POST /auth/register — email já cadastrado (EMAIL_3)"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/register" \
  -H "Content-Type: application/json" \
  -d "$(jq -n --arg email "$EMAIL_3" '{name: "Outro", email: $email, password: "123456"}')")
check "Register email duplicado" 422 "$HTTP"

# Cria um 2º usuário via register para testar duplicidade no POST /users
echo "29. Criar 2º usuário via register (para testes de duplicidade)"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/register" \
  -H "Content-Type: application/json" \
  -d "$(jq -n --arg email "$EMAIL_2" --arg senha "$SENHA" '{name: "Segundo Cliente", email: $email, password: $senha}')")
BODY=$(cat "$RESP_FILE")
TOKEN_2=$(echo "$BODY" | jq -r '.token // empty')
USER_2_ID=$(echo "$BODY" | jq -r '.user.id // empty')
check "Register 2º usuário" 201 "$HTTP"

echo "30. POST /users (autenticado) — email duplicado (EMAIL_2)"
if [ -n "$TOKEN_2" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/users" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN_2" \
    -d "$(jq -n --arg email "$EMAIL_2" '{name: "Terceiro", email: $email, password: "123456"}')")
  check "POST /users email duplicado" 422 "$HTTP"
else
  skip "POST /users duplicado (TOKEN_2 vazio)"
fi

echo "31. PUT /users/$USER_2_ID (autenticado) — email já usado por outro user"
if [ -n "$USER_2_ID" ] && [ -n "$TOKEN_2" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X PUT "$BASE/users/$USER_2_ID" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN_2" \
    -d "$(jq -n '{name: "Segundo Alterado", email: "cliente@teste.com"}')")
  check "PUT /users/$USER_2_ID email outro user" 422 "$HTTP"
else
  skip "PUT /users/$USER_2_ID duplicado (IDs vazios)"
fi

# ===== BLOCO 8: VALIDAÇÃO NO PUT (400) =====
echo ""
echo "─── BLOCO 8: VALIDAÇÃO NO PUT (400 BAD REQUEST) ───"

echo "32. PUT /users/$USER_2_ID (autenticado) — email inválido"
if [ -n "$USER_2_ID" ] && [ -n "$TOKEN_2" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X PUT "$BASE/users/$USER_2_ID" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN_2" \
    -d '{"name":"Válido","email":"invalido"}')
  check "PUT /users/$USER_2_ID email inválido" 400 "$HTTP"
else
  skip "PUT /users email inválido (TOKEN_2 vazio)"
fi

echo "33. PUT /users/$USER_2_ID (autenticado) — nome vazio"
if [ -n "$USER_2_ID" ] && [ -n "$TOKEN_2" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X PUT "$BASE/users/$USER_2_ID" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN_2" \
    -d '{"name":"","email":"valido@teste.com"}')
  check "PUT /users/$USER_2_ID nome vazio" 400 "$HTTP"
else
  skip "PUT /users nome vazio (TOKEN_2 vazio)"
fi

# ===== BLOCO 9: NÃO ENCONTRADO (404) =====
echo ""
echo "─── BLOCO 9: NÃO ENCONTRADO (404 NOT FOUND) ───"

echo "34. GET /users/9999 (autenticado)"
if [ -n "$TOKEN_2" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X GET "$BASE/users/9999" \
    -H "Authorization: Bearer $TOKEN_2")
  check "GET /users/9999" 404 "$HTTP"
else
  skip "GET /users/9999 (TOKEN_2 vazio)"
fi

echo "35. PUT /users/9999 (autenticado)"
if [ -n "$TOKEN_2" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X PUT "$BASE/users/9999" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN_2" \
    -d '{"name":"X","email":"x@teste.com"}')
  check "PUT /users/9999" 404 "$HTTP"
else
  skip "PUT /users/9999 (TOKEN_2 vazio)"
fi

echo "36. DELETE /users/9999 (autenticado)"
if [ -n "$TOKEN_2" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X DELETE "$BASE/users/9999" \
    -H "Authorization: Bearer $TOKEN_2")
  check "DELETE /users/9999" 404 "$HTTP"
else
  skip "DELETE /users/9999 (TOKEN_2 vazio)"
fi

# ===== BLOCO 10: FLUXO COMPLETO 2º USUÁRIO =====
echo ""
echo "─── BLOCO 10: FLUXO COMPLETO — 2º USUÁRIO ───"

echo "37. POST /auth/login (2º usuário) — login"
if [ -n "$EMAIL_2" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/login" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --arg email "$EMAIL_2" --arg senha "$SENHA" '{email: $email, password: $senha}')")
  BODY=$(cat "$RESP_FILE")
  TOKEN_2_LOGIN=$(echo "$BODY" | jq -r '.token // empty')
  REFRESH_2=$(echo "$BODY" | jq -r '.refreshToken // empty')
  check "Login 2º usuário" 200 "$HTTP"
else
  skip "Login 2º usuário (EMAIL_2 vazio)"
fi

echo "38. POST /auth/refresh (2º usuário) — refresh"
if [ -n "$REFRESH_2" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/refresh" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --arg rt "$REFRESH_2" '{refreshToken: $rt}')")
  check "Refresh 2º usuário" 200 "$HTTP"
else
  skip "Refresh 2º usuário (REFRESH_2 vazio)"
fi

echo "39. DELETE /users/$USER_2_ID (autenticado com token do 1º)"
if [ -n "$USER_2_ID" ] && [ -n "$TOKEN_LOGIN" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X DELETE "$BASE/users/$USER_2_ID" \
    -H "Authorization: Bearer $TOKEN_LOGIN")
  check "DELETE /users/$USER_2_ID" 204 "$HTTP"
else
  skip "DELETE /users/$USER_2_ID (IDs vazios)"
fi

echo "40. DELETE /users/$USER_ID (autenticado com token do 1º)"
if [ -n "$USER_ID" ] && [ -n "$TOKEN_LOGIN" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X DELETE "$BASE/users/$USER_ID" \
    -H "Authorization: Bearer $TOKEN_LOGIN")
  check "DELETE /users/$USER_ID" 204 "$HTTP"
else
  skip "DELETE /users/$USER_ID (IDs vazios)"
fi

# ===== RESUMO =====
echo ""
echo "=============================================="
echo "  RESUMO FINAL"
echo "  ✅ Passou: $PASS"
echo "  ❌ Falhou: $FAIL"
echo "  ⏭️  Pulados: $SKIP"
echo "  📊 Total:  $((PASS + FAIL + SKIP))"
echo "=============================================="
