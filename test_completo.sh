#!/bin/bash
# Script de testes completo — Agendador Click & Collect API
# Uso: chmod +x test_completo.sh && ./test_completo.sh

BASE="http://localhost:8080"
PASS=0
FAIL=0
SKIP=0
RESP_FILE="/tmp/_resp.json"

SUFIXO=$(date +%s)
STORE_NAME="Megaloja Centro ${SUFIXO}"
PROD_1_NAME="Smartphone Galaxy S25 ${SUFIXO}"
PROD_2_NAME="Fone Bluetooth ${SUFIXO}"

# ===== AUTENTICAÇÃO — obtém token JWT =====
echo "── Autenticando para obter token JWT"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d "$(jq -n '{email: "cliente@teste.com", password: "senha123"}')")
BODY=$(cat "$RESP_FILE")
TOKEN=$(echo "$BODY" | jq -r '.token // empty')

if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
  echo "  ⚠️  Login do usuário default falhou — registrando novo usuário"
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -X POST "$BASE/auth/register" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --arg s "$SUFIXO" '{name: "Usuário Teste", email: ("teste_" + $s + "@teste.com"), password: "123456"}')")
  BODY=$(cat "$RESP_FILE")
  TOKEN=$(echo "$BODY" | jq -r '.token // empty')
  USER_ID=$(echo "$BODY" | jq -r '.user.id // empty')
  echo "  Token obtido via register (user_id=$USER_ID)"
else
  echo "  Token obtido via login do usuário default"
fi

AUTH_HEADER="Authorization: Bearer $TOKEN"

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

skip() {
    local desc="$1"
    echo "  ⏭️  $desc (pulado)"
    SKIP=$((SKIP + 1))
}

echo ""
echo "=============================================="
echo "  TESTES — AGENDADOR CLICK & COLLECT API"
echo "=============================================="
echo ""

# ===== BLOCO 1: HAPPY PATH =====
echo "─── BLOCO 1: HAPPY PATH ───"

echo "1. CRIAR LOJA"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X POST "$BASE/stores" \
  -H "Content-Type: application/json" \
  -d "$(jq -n --arg nome "$STORE_NAME" '{name: $nome, address: "Av. Paulista, 1000", city: "São Paulo", state: "SP"}')")
BODY=$(cat "$RESP_FILE")
STORE_ID=$(echo "$BODY" | jq -r '.id // empty')
check "Criar loja" 201 "$HTTP" "$BODY"

echo "2. CRIAR PRODUTO 1"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X POST "$BASE/products" \
  -H "Content-Type: application/json" \
  -d "$(jq -n --arg nome "$PROD_1_NAME" '{name: $nome, description: "256GB", price: 4299.99, imageUrl: "https://exemplo.com/s25.jpg"}')")
BODY=$(cat "$RESP_FILE")
PROD_1_ID=$(echo "$BODY" | jq -r '.id // empty')
check "Criar produto 1" 201 "$HTTP" "$BODY"

echo "3. CRIAR PRODUTO 2"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X POST "$BASE/products" \
  -H "Content-Type: application/json" \
  -d "$(jq -n --arg nome "$PROD_2_NAME" '{name: $nome, description: "Sem fio", price: 189.90, imageUrl: "https://exemplo.com/fone.jpg"}')")
BODY=$(cat "$RESP_FILE")
PROD_2_ID=$(echo "$BODY" | jq -r '.id // empty')
check "Criar produto 2" 201 "$HTTP" "$BODY"

echo "4. CRIAR ESTOQUE (loja $STORE_ID, produto $PROD_1_ID)"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X POST "$BASE/inventories" \
  -H "Content-Type: application/json" \
  -d "$(jq -n --argjson s "$STORE_ID" --argjson p "$PROD_1_ID" '{storeId: $s, productId: $p, quantity: 100}')")
BODY=$(cat "$RESP_FILE")
INV_1_ID=$(echo "$BODY" | jq -r '.id // empty')
check "Criar estoque (loja $STORE_ID, prod $PROD_1_ID)" 201 "$HTTP" "$BODY"

echo "5. CRIAR ESTOQUE (loja $STORE_ID, produto $PROD_2_ID)"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X POST "$BASE/inventories" \
  -H "Content-Type: application/json" \
  -d "$(jq -n --argjson s "$STORE_ID" --argjson p "$PROD_2_ID" '{storeId: $s, productId: $p, quantity: 50}')")
BODY=$(cat "$RESP_FILE")
INV_2_ID=$(echo "$BODY" | jq -r '.id // empty')
check "Criar estoque (loja $STORE_ID, prod $PROD_2_ID)" 201 "$HTTP" "$BODY"

# Busca o id do usuário default da migration (email: cliente@teste.com)
echo "── Buscar usuário default da migration"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/users?email=cliente@teste.com&page=0&size=1")
BODY=$(cat "$RESP_FILE")
USER_ID=$(echo "$BODY" | jq -r '.content[0].id // empty')

if [ -z "$USER_ID" ]; then
  echo "  ⚠️  Usuário não encontrado via GET /users — assumindo id=1"
  USER_ID="1"
fi

echo "6. CRIAR PEDIDO (customer $USER_ID, store $STORE_ID, product $PROD_1_ID)"
if [ -n "$STORE_ID" ] && [ -n "$PROD_1_ID" ] && [ "$STORE_ID" != "null" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X POST "$BASE/orders" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --argjson cid "$USER_ID" --argjson sid "$STORE_ID" --argjson pid "$PROD_1_ID" \
      '{customerId: $cid, storeId: $sid, items: [{productId: $pid, quantity: 2, unitPrice: 4299.99}]}')")
  BODY=$(cat "$RESP_FILE")
  ORDER_ID=$(echo "$BODY" | jq -r '.id // empty')
  check "Criar pedido" 201 "$HTTP" "$BODY"
else
  skip "Criar pedido (IDs vazios)"
  ORDER_ID=""
fi

echo "7. ATUALIZAR STATUS PEDIDO $ORDER_ID PARA APPROVED"
if [ -n "$ORDER_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X PATCH "$BASE/orders/$ORDER_ID/status" \
    -H "Content-Type: application/json" \
    -d '{"status":"APPROVED"}')
  BODY=$(cat "$RESP_FILE")
  check "Atualizar status para APPROVED" 200 "$HTTP" "$BODY"
else
  skip "Atualizar status (ORDER_ID vazio)"
fi

echo "8. CRIAR AUTORIZAÇÃO DE RETIRADA"
if [ -n "$ORDER_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X POST "$BASE/pickup-authorizations" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --argjson oid "$ORDER_ID" \
      '{orderId: $oid, authorizedName: "Maria da Silva", authorizedDocument: "123.456.789-00"}')")
  BODY=$(cat "$RESP_FILE")
  AUTH_ID=$(echo "$BODY" | jq -r '.id // empty')
  check "Criar autorização de retirada" 201 "$HTTP" "$BODY"
else
  skip "Criar autorização (ORDER_ID vazio)"
  AUTH_ID=""
fi

echo "9. CONSULTAS GET"
if [ -n "$PROD_1_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/products/$PROD_1_ID")
  check "GET /products/$PROD_1_ID" 200 "$HTTP"
else
  skip "GET /products (ID vazio)"
fi

if [ -n "$STORE_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/stores/$STORE_ID")
  check "GET /stores/$STORE_ID" 200 "$HTTP"
else
  skip "GET /stores (ID vazio)"
fi

if [ -n "$ORDER_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/orders/$ORDER_ID")
  check "GET /orders/$ORDER_ID" 200 "$HTTP"
else
  skip "GET /orders (ID vazio)"
fi

if [ -n "$STORE_ID" ] && [ -n "$PROD_1_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/inventories/store/$STORE_ID/product/$PROD_1_ID")
  check "GET /inventories/store/$STORE_ID/product/$PROD_1_ID" 200 "$HTTP"
else
  skip "GET /inventories (IDs vazios)"
fi

if [ -n "$AUTH_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/pickup-authorizations/$AUTH_ID")
  check "GET /pickup-authorizations/$AUTH_ID" 200 "$HTTP"
else
  skip "GET /pickup-authorizations (ID vazio)"
fi

echo "10. LISTAGENS COM PAGINAÇÃO"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/products?page=0&size=10")
check "GET /products (paginado)" 200 "$HTTP"

HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/stores?page=0&size=10")
check "GET /stores (paginado)" 200 "$HTTP"

HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/orders?page=0&size=10")
check "GET /orders (paginado)" 200 "$HTTP"

HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/inventories?page=0&size=10")
check "GET /inventories (paginado)" 200 "$HTTP"

HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/notifications?page=0&size=10")
check "GET /notifications (paginado)" 200 "$HTTP"

if [ -n "$ORDER_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/pickup-authorizations/order/$ORDER_ID?page=0&size=10")
  check "GET /pickup-authorizations/order/$ORDER_ID (paginado)" 200 "$HTTP"
else
  skip "GET /pickup-authorizations/order (ID vazio)"
fi

echo "11. ATUALIZAR PRODUTO $PROD_1_ID"
if [ -n "$PROD_1_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X PUT "$BASE/products/$PROD_1_ID" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --arg s "$SUFIXO" '{name: ("Smartphone Galaxy S25 Ultra " + $s), description: "512GB", price: 7299.99, imageUrl: "https://exemplo.com/s25-ultra.jpg", active: true}')")
  check "Atualizar produto" 200 "$HTTP"
else
  skip "Atualizar produto (ID vazio)"
fi

echo "12. ATUALIZAR LOJA $STORE_ID"
if [ -n "$STORE_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X PUT "$BASE/stores/$STORE_ID" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --arg s "$SUFIXO" '{name: ("Megaloja Centro Matriz " + $s), address: "Av. Paulista, 2000", city: "São Paulo", state: "SP", active: true}')")
  check "Atualizar loja" 200 "$HTTP"
else
  skip "Atualizar loja (ID vazio)"
fi

echo "13. ATUALIZAR ESTOQUE (loja $STORE_ID, produto $PROD_1_ID)"
if [ -n "$STORE_ID" ] && [ -n "$PROD_1_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X PUT "$BASE/inventories/store/$STORE_ID/product/$PROD_1_ID" \
    -H "Content-Type: application/json" \
    -d '{"quantity":80,"reservedQuantity":2}')
  check "Atualizar estoque" 200 "$HTTP"
else
  skip "Atualizar estoque (IDs vazios)"
fi

echo "14. Listar notificações do pedido $ORDER_ID"
if [ -n "$ORDER_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/notifications?orderId=$ORDER_ID")
  BODY=$(cat "$RESP_FILE")
  NOTIF_ID=$(echo "$BODY" | jq -r '.content[0].id // empty')
  check "Listar notificações do pedido $ORDER_ID" 200 "$HTTP" "$BODY"
else
  skip "Listar notificações (ORDER_ID vazio)"
  NOTIF_ID=""
fi

echo "15. MARCAR NOTIFICAÇÃO $NOTIF_ID COMO LIDA"
if [ -n "$NOTIF_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X PATCH "$BASE/notifications/$NOTIF_ID/read")
  check "Marcar notificação como lida" 200 "$HTTP"
else
  skip "Marcar notificação (ID vazio)"
fi

# ===== BLOCO 2: VALIDAÇÃO (400) =====
echo ""
echo "─── BLOCO 2: VALIDAÇÃO (400 BAD REQUEST) ───"

echo "16. POST /products — body vazio"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X POST "$BASE/products" \
  -H "Content-Type: application/json" \
  -d '{}')
BODY=$(cat "$RESP_FILE")
check "POST /products body vazio" 400 "$HTTP" "$BODY"

echo "17. POST /products — price negativo"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X POST "$BASE/products" \
  -H "Content-Type: application/json" \
  -d '{"price": -100}')
BODY=$(cat "$RESP_FILE")
check "POST /products price negativo" 400 "$HTTP" "$BODY"

echo "18. POST /stores — body vazio"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X POST "$BASE/stores" \
  -H "Content-Type: application/json" \
  -d '{}')
BODY=$(cat "$RESP_FILE")
check "POST /stores body vazio" 400 "$HTTP" "$BODY"

echo "19. POST /orders — items vazio"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X POST "$BASE/orders" \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"storeId":1,"items":[]}')
BODY=$(cat "$RESP_FILE")
check "POST /orders items vazio" 400 "$HTTP" "$BODY"

echo "20. PATCH /orders/$ORDER_ID/status — status inválido"
if [ -n "$ORDER_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X PATCH "$BASE/orders/$ORDER_ID/status" \
    -H "Content-Type: application/json" \
    -d '{"status":"INEXISTENTE"}')
  BODY=$(cat "$RESP_FILE")
  check "PATCH /orders/$ORDER_ID/status inválido" 400 "$HTTP" "$BODY"
else
  skip "PATCH status inválido (ORDER_ID vazio)"
fi

echo "21. POST /pickup-authorizations — sem orderId"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X POST "$BASE/pickup-authorizations" \
  -H "Content-Type: application/json" \
  -d '{"authorizedName":"Maria"}')
BODY=$(cat "$RESP_FILE")
check "POST /pickup-authorizations sem orderId" 400 "$HTTP" "$BODY"

echo "22. PUT /inventories — quantity negativo"
if [ -n "$STORE_ID" ] && [ -n "$PROD_1_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X PUT "$BASE/inventories/store/$STORE_ID/product/$PROD_1_ID" \
    -H "Content-Type: application/json" \
    -d '{"quantity": -5}')
  BODY=$(cat "$RESP_FILE")
  check "PUT /inventories quantity negativo" 400 "$HTTP" "$BODY"
else
  skip "PUT /inventories quantity negativo (IDs vazios)"
fi

echo "23. POST /products — Content-Type text/plain"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X POST "$BASE/products" \
  -H "Content-Type: text/plain" \
  -d 'dados invalidos')
BODY=$(cat "$RESP_FILE")
check "POST /products Content-Type errado" 400 "$HTTP" "$BODY"

echo "24. GET /products/abc — ID não numérico"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/products/abc")
BODY=$(cat "$RESP_FILE")
check "GET /products/abc path inválido" 400 "$HTTP" "$BODY"

echo "25. PUT /inventories/store/x/product/1 — storeId não numérico"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X PUT "$BASE/inventories/store/x/product/1" \
  -H "Content-Type: application/json" \
  -d '{"quantity":10}')
BODY=$(cat "$RESP_FILE")
check "PUT /inventories storeId inválido" 400 "$HTTP" "$BODY"

# ===== BLOCO 3: NÃO ENCONTRADO (404) =====
echo ""
echo "─── BLOCO 3: NÃO ENCONTRADO (404 NOT FOUND) ───"

echo "26. GET /products/9999"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/products/9999")
BODY=$(cat "$RESP_FILE")
check "GET /products/9999" 404 "$HTTP" "$BODY"

echo "27. GET /stores/9999"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/stores/9999")
BODY=$(cat "$RESP_FILE")
check "GET /stores/9999" 404 "$HTTP" "$BODY"

echo "28. GET /orders/9999"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/orders/9999")
BODY=$(cat "$RESP_FILE")
check "GET /orders/9999" 404 "$HTTP" "$BODY"

echo "29. GET /inventories/9999"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/inventories/9999")
BODY=$(cat "$RESP_FILE")
check "GET /inventories/9999" 404 "$HTTP" "$BODY"

echo "30. GET /inventories/store/999/product/999"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/inventories/store/999/product/999")
BODY=$(cat "$RESP_FILE")
check "GET /inventories/store/999/product/999" 404 "$HTTP" "$BODY"

echo "31. GET /notifications/9999"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/notifications/9999")
BODY=$(cat "$RESP_FILE")
check "GET /notifications/9999" 404 "$HTTP" "$BODY"

echo "32. GET /pickup-authorizations/9999"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" "$BASE/pickup-authorizations/9999")
BODY=$(cat "$RESP_FILE")
check "GET /pickup-authorizations/9999" 404 "$HTTP" "$BODY"

echo "33. PUT /products/9999"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X PUT "$BASE/products/9999" \
  -H "Content-Type: application/json" \
  -d '{"name":"X","price":10}')
BODY=$(cat "$RESP_FILE")
check "PUT /products/9999" 404 "$HTTP" "$BODY"

echo "34. PATCH /orders/9999/status"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X PATCH "$BASE/orders/9999/status" \
  -H "Content-Type: application/json" \
  -d '{"status":"CANCELED"}')
BODY=$(cat "$RESP_FILE")
check "PATCH /orders/9999/status" 404 "$HTTP" "$BODY"

echo "35. DELETE /products/9999"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X DELETE "$BASE/products/9999")
BODY=$(cat "$RESP_FILE")
check "DELETE /products/9999" 404 "$HTTP" "$BODY"

# ===== BLOCO 4: REGRA DE NEGÓCIO (422) =====
echo ""
echo "─── BLOCO 4: REGRA DE NEGÓCIO (422 UNPROCESSABLE ENTITY) ───"

echo "36. Pedido com produto inexistente na loja"
if [ -n "$STORE_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X POST "$BASE/orders" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --argjson cid "$USER_ID" --argjson sid "$STORE_ID" \
      '{customerId: $cid, storeId: $sid, items: [{productId: 9999, quantity: 1, unitPrice: 100}]}')")
  BODY=$(cat "$RESP_FILE")
  check "POST /orders produto sem estoque na loja" 422 "$HTTP" "$BODY"
else
  skip "POST /orders 422 (STORE_ID vazio)"
fi

echo "37. Pedido com quantidade maior que o estoque"
if [ -n "$STORE_ID" ] && [ -n "$PROD_1_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X POST "$BASE/orders" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --argjson cid "$USER_ID" --argjson sid "$STORE_ID" --argjson pid "$PROD_1_ID" \
      '{customerId: $cid, storeId: $sid, items: [{productId: $pid, quantity: 999, unitPrice: 4299.99}]}')")
  BODY=$(cat "$RESP_FILE")
  check "POST /orders qtd maior que estoque" 422 "$HTTP" "$BODY"
else
  skip "POST /orders 422 (IDs vazios)"
fi

# ===== BLOCO 5: DUPLICIDADE =====
echo ""
echo "─── BLOCO 5: DUPLICIDADE ───"

echo "38. Criar loja com nome duplicado (422)"
NEW_STORE_NAME="Megaloja Centro Matriz ${SUFIXO}"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X POST "$BASE/stores" \
  -H "Content-Type: application/json" \
  -d "$(jq -n --arg nome "$NEW_STORE_NAME" '{name: $nome, address: "Outro", city: "SP", state: "SP"}')")
BODY=$(cat "$RESP_FILE")
check "POST /stores nome duplicado" 422 "$HTTP" "$BODY"

echo "39. Criar produto com nome duplicado (422)"
NEW_PROD_NAME="Smartphone Galaxy S25 Ultra ${SUFIXO}"
HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X POST "$BASE/products" \
  -H "Content-Type: application/json" \
  -d "$(jq -n --arg nome "$NEW_PROD_NAME" '{name: $nome, description: "Outro", price: 3999.00}')")
BODY=$(cat "$RESP_FILE")
check "POST /products nome duplicado" 422 "$HTTP" "$BODY"

echo "40. POST /inventories — duplicado (unique → 409)"
if [ -n "$STORE_ID" ] && [ -n "$PROD_1_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X POST "$BASE/inventories" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --argjson s "$STORE_ID" --argjson p "$PROD_1_ID" '{storeId: $s, productId: $p, quantity: 50}')")
  BODY=$(cat "$RESP_FILE")
  check "POST /inventories duplicado" 409 "$HTTP" "$BODY"
else
  skip "POST /inventories duplicado (IDs vazios)"
fi



# ===== BLOCO 6: EXCLUSÕES (ordem correta para evitar FK) =====
echo ""
echo "─── BLOCO 6: EXCLUSÕES ───"

echo "41. DELETE inventário $INV_1_ID"
if [ -n "$INV_1_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X DELETE "$BASE/inventories/$INV_1_ID")
  check "DELETE inventário $INV_1_ID" 204 "$HTTP"
else
  skip "DELETE inventário (ID vazio)"
fi

echo "42. DELETE inventário $INV_2_ID"
if [ -n "$INV_2_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X DELETE "$BASE/inventories/$INV_2_ID")
  check "DELETE inventário $INV_2_ID" 204 "$HTTP"
else
  skip "DELETE inventário (ID vazio)"
fi

echo "43. DELETE /pickup-authorizations/$AUTH_ID"
if [ -n "$AUTH_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X DELETE "$BASE/pickup-authorizations/$AUTH_ID")
  check "DELETE /pickup-authorizations/$AUTH_ID" 204 "$HTTP"
else
  skip "DELETE /pickup-authorizations (ID vazio)"
fi

echo "44. DELETE /orders/$ORDER_ID"
if [ -n "$ORDER_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X DELETE "$BASE/orders/$ORDER_ID")
  check "DELETE /orders/$ORDER_ID" 204 "$HTTP"
else
  skip "DELETE /orders (ID vazio)"
fi

echo "45. DELETE /products/$PROD_2_ID"
if [ -n "$PROD_2_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X DELETE "$BASE/products/$PROD_2_ID")
  check "DELETE /products/$PROD_2_ID" 204 "$HTTP"
else
  skip "DELETE /products (ID vazio)"
fi

echo "46. DELETE /products/$PROD_1_ID"
if [ -n "$PROD_1_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X DELETE "$BASE/products/$PROD_1_ID")
  check "DELETE /products/$PROD_1_ID" 204 "$HTTP"
else
  skip "DELETE /products (ID vazio)"
fi

echo "47. DELETE /stores/$STORE_ID"
if [ -n "$STORE_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X DELETE "$BASE/stores/$STORE_ID")
  check "DELETE /stores/$STORE_ID" 204 "$HTTP"
else
  skip "DELETE /stores (ID vazio)"
fi

echo "48. DELETE duplicado /stores/$STORE_ID (já excluída)"
if [ -n "$STORE_ID" ]; then
  HTTP=$(curl -s -o "$RESP_FILE" -w "%{http_code}" -H "$AUTH_HEADER" -X DELETE "$BASE/stores/$STORE_ID")
  check "DELETE /stores/$STORE_ID duplicado" 404 "$HTTP"
else
  skip "DELETE /stores duplicado (ID vazio)"
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
