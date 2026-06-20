package br.com.megaloja.controllers.docs;

import br.com.megaloja.dtos.CreateUserRequest;
import br.com.megaloja.dtos.UpdateUserRequest;
import br.com.megaloja.dtos.UserResponse;
import br.com.megaloja.filters.UserFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema")
public interface UserControllerDocs {

    @Operation(summary = "Criar usuário", description = "Registra um novo usuário no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "422", description = "Email já cadastrado")
    })
    ResponseEntity<UserResponse> create(@RequestBody @Valid CreateUserRequest request);

    @Operation(summary = "Listar usuários", description = "Retorna uma lista paginada de usuários com filtros")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    ResponseEntity<Page<UserResponse>> findAll(UserFilter filter, Pageable pageable);

    @Operation(summary = "Buscar usuário por ID", description = "Retorna os detalhes de um usuário específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    ResponseEntity<UserResponse> findById(@PathVariable Long id);

    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "422", description = "Email já cadastrado")
    })
    ResponseEntity<UserResponse> update(@PathVariable Long id,
                                        @RequestBody @Valid UpdateUserRequest request);

    @Operation(summary = "Excluir usuário", description = "Remove um usuário do sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    ResponseEntity<Void> delete(@PathVariable Long id);
}
