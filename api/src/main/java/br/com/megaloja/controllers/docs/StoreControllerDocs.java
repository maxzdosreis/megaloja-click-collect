package br.com.megaloja.controllers.docs;

import br.com.megaloja.dtos.CreateStoreRequest;
import br.com.megaloja.dtos.StoreResponse;
import br.com.megaloja.dtos.UpdateStoreRequest;
import br.com.megaloja.filters.StoreFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "Lojas", description = "Gerenciamento de lojas físicas")
public interface StoreControllerDocs {

    @Operation(summary = "Criar loja", description = "Cadastra uma nova loja no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Loja criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    ResponseEntity<StoreResponse> create(CreateStoreRequest request);

    @Operation(summary = "Listar lojas", description = "Retorna lista paginada de lojas com suporte a filtros")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de lojas retornada com sucesso")
    })
    ResponseEntity<Page<StoreResponse>> findAll(StoreFilter filter, Pageable pageable);

    @Operation(summary = "Buscar loja por ID", description = "Retorna os dados de uma loja específica")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loja encontrada"),
            @ApiResponse(responseCode = "404", description = "Loja não encontrada")
    })
    ResponseEntity<StoreResponse> findById(Long id);

    @Operation(summary = "Atualizar loja", description = "Atualiza os dados de uma loja existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loja atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Loja não encontrada")
    })
    ResponseEntity<StoreResponse> update(Long id, UpdateStoreRequest request);

    @Operation(summary = "Excluir loja", description = "Remove uma loja do sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Loja removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Loja não encontrada")
    })
    ResponseEntity<Void> delete(Long id);
}
