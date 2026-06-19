package br.com.megaloja.controllers.docs;

import br.com.megaloja.dtos.InventoryResponse;
import br.com.megaloja.dtos.UpdateInventoryRequest;
import br.com.megaloja.filters.InventoryFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "Inventário", description = "Consulta e atualização do estoque por loja e produto")
public interface InventoryControllerDocs {

    @Operation(summary = "Listar inventário", description = "Retorna lista paginada do estoque geral com suporte a filtros")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de inventário retornada com sucesso")
    })
    ResponseEntity<Page<InventoryResponse>> findAll(InventoryFilter filter, Pageable pageable);

    @Operation(summary = "Buscar item de inventário por ID", description = "Retorna os dados de um item específico do estoque")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item encontrado"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    ResponseEntity<InventoryResponse> findById(Long id);

    @Operation(summary = "Buscar estoque por loja e produto",
            description = "Retorna o estoque de um produto específico em uma loja específica")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estoque encontrado"),
            @ApiResponse(responseCode = "404", description = "Estoque não encontrado para esta loja e produto")
    })
    ResponseEntity<InventoryResponse> findByStoreAndProduct(Long storeId, Long productId);

    @Operation(summary = "Atualizar estoque", description = "Atualiza a quantidade e dados de estoque de um produto em uma loja")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estoque atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Estoque não encontrado")
    })
    ResponseEntity<InventoryResponse> updateStock(Long storeId, Long productId, UpdateInventoryRequest request);
}
