package br.com.megaloja.controllers.docs;

import br.com.megaloja.dtos.CreateOrderRequest;
import br.com.megaloja.dtos.OrderResponse;
import br.com.megaloja.dtos.UpdateOrderStatusRequest;
import br.com.megaloja.filters.OrderFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "Pedidos", description = "Gerenciamento de pedidos e agendamentos de retirada")
public interface OrderControllerDocs {

    @Operation(summary = "Criar pedido", description = "Cria um novo pedido com agendamento de retirada")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "422", description = "Estoque insuficiente ou regra de negócio violada")
    })
    ResponseEntity<OrderResponse> create(CreateOrderRequest request);

    @Operation(summary = "Listar pedidos", description = "Retorna lista paginada de pedidos com suporte a filtros")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso")
    })
    ResponseEntity<Page<OrderResponse>> findAll(OrderFilter filter, Pageable pageable);

    @Operation(summary = "Buscar pedido por ID", description = "Retorna os dados de um pedido específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    ResponseEntity<OrderResponse> findById(Long id);

    @Operation(summary = "Atualizar status do pedido", description = "Atualiza apenas o status de um pedido (ex: PENDENTE, APROVADO, CANCELADO)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    ResponseEntity<OrderResponse> updateStatus(Long id, UpdateOrderStatusRequest request);

    @Operation(summary = "Excluir pedido", description = "Remove um pedido do sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    ResponseEntity<Void> delete(Long id);
}
