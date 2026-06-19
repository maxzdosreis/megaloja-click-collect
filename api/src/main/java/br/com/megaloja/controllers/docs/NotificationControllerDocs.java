package br.com.megaloja.controllers.docs;

import br.com.megaloja.dtos.NotificationResponse;
import br.com.megaloja.filters.NotificationFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "Notificações", description = "Notificações relacionadas aos pedidos")
public interface NotificationControllerDocs {

    @Operation(summary = "Listar notificações", description = "Retorna lista paginada de notificações com suporte a filtros")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de notificações retornada com sucesso")
    })
    ResponseEntity<Page<NotificationResponse>> findAll(NotificationFilter filter, Pageable pageable);

    @Operation(summary = "Buscar notificação por ID", description = "Retorna os dados de uma notificação específica")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificação encontrada"),
            @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
    })
    ResponseEntity<NotificationResponse> findById(Long id);

    @Operation(summary = "Marcar notificação como lida", description = "Marca uma notificação como lida")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificação marcada como lida"),
            @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
    })
    ResponseEntity<NotificationResponse> markAsRead(Long id);

    @Operation(summary = "Excluir notificação", description = "Remove uma notificação do sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Notificação removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
    })
    ResponseEntity<Void> delete(Long id);
}
