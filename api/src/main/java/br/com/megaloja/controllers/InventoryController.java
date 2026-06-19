package br.com.megaloja.controllers;

import br.com.megaloja.controllers.docs.InventoryControllerDocs;
import br.com.megaloja.dtos.InventoryResponse;
import br.com.megaloja.dtos.UpdateInventoryRequest;
import br.com.megaloja.filters.InventoryFilter;
import br.com.megaloja.services.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventories")
@RequiredArgsConstructor
public class InventoryController implements InventoryControllerDocs {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<Page<InventoryResponse>> findAll(InventoryFilter filter,
                                                           @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(inventoryService.findAll(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.findById(id));
    }

    @GetMapping("/store/{storeId}/product/{productId}")
    public ResponseEntity<InventoryResponse> findByStoreAndProduct(@PathVariable Long storeId,
                                                                   @PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.findByStoreAndProduct(storeId, productId));
    }

    @PutMapping("/store/{storeId}/product/{productId}")
    public ResponseEntity<InventoryResponse> updateStock(@PathVariable Long storeId,
                                                         @PathVariable Long productId,
                                                         @RequestBody @Valid UpdateInventoryRequest request) {
        return ResponseEntity.ok(inventoryService.updateStock(storeId, productId, request));
    }
}
