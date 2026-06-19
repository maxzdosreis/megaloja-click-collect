package br.com.megaloja.controllers.docs;

import br.com.megaloja.dtos.CreateProductRequest;
import br.com.megaloja.dtos.ProductResponse;
import br.com.megaloja.dtos.UpdateProductRequest;
import br.com.megaloja.filters.ProductFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "Produtos", description = "Gerenciamento de produtos do catálogo")
public interface ProductControllerDocs {

    @Operation(summary = "Criar produto", description = "Cadastra um novo produto no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    ResponseEntity<ProductResponse> create(CreateProductRequest request);

    @Operation(summary = "Listar produtos", description = "Retorna lista paginada de produtos com suporte a filtros")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
    })
    ResponseEntity<Page<ProductResponse>> findAll(ProductFilter filter, Pageable pageable);

    @Operation(summary = "Buscar produto por ID", description = "Retorna os dados de um produto específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    ResponseEntity<ProductResponse> findById(Long id);

    @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    ResponseEntity<ProductResponse> update(Long id, UpdateProductRequest request);

    @Operation(summary = "Excluir produto", description = "Remove um produto do sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    ResponseEntity<Void> delete(Long id);
}
