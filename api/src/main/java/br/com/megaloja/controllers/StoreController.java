package br.com.megaloja.controllers;

import br.com.megaloja.controllers.docs.StoreControllerDocs;
import br.com.megaloja.dtos.CreateStoreRequest;
import br.com.megaloja.dtos.StoreResponse;
import br.com.megaloja.dtos.UpdateStoreRequest;
import br.com.megaloja.filters.StoreFilter;
import br.com.megaloja.services.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController implements StoreControllerDocs {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<StoreResponse> create(@RequestBody @Valid CreateStoreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(storeService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<StoreResponse>> findAll(StoreFilter filter,
                                                       @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(storeService.findAll(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreResponse> update(@PathVariable Long id,
                                                @RequestBody @Valid UpdateStoreRequest request) {
        return ResponseEntity.ok(storeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        storeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
