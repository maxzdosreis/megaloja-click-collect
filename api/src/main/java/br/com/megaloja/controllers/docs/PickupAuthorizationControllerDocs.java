package br.com.megaloja.controllers.docs;

import br.com.megaloja.dtos.CreatePickupAuthorizationRequest;
import br.com.megaloja.dtos.PickupAuthorizationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "Autorizações de Retirada", description = "Autorizações para terceiros retirarem pedidos")
public interface PickupAuthorizationControllerDocs {

    @Operation(summary = "Criar autorização de retirada",
            description = "Cria uma autorização para que um terceiro possa retirar um pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Autorização criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    ResponseEntity<PickupAuthorizationResponse> create(CreatePickupAuthorizationRequest request);

    @Operation(summary = "Buscar autorização por ID", description = "Retorna os dados de uma autorização específica")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autorização encontrada"),
            @ApiResponse(responseCode = "404", description = "Autorização não encontrada")
    })
    ResponseEntity<PickupAuthorizationResponse> findById(Long id);

    @Operation(summary = "Listar autorizações por pedido",
            description = "Retorna lista paginada de autorizações de retirada de um pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de autorizações retornada com sucesso")
    })
    ResponseEntity<Page<PickupAuthorizationResponse>> findByOrder(Long orderId, Pageable pageable);

    @Operation(summary = "Excluir autorização de retirada", description = "Remove uma autorização de retirada do sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Autorização removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Autorização não encontrada")
    })
    ResponseEntity<Void> delete(Long id);
}
