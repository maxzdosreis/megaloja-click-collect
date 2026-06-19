package br.com.megaloja.services;

import br.com.megaloja.dtos.CreateInventoryRequest;
import br.com.megaloja.dtos.InventoryResponse;
import br.com.megaloja.dtos.UpdateInventoryRequest;
import br.com.megaloja.exceptions.ResourceNotFoundException;
import br.com.megaloja.filters.InventoryFilter;
import br.com.megaloja.mappers.InventoryMapper;
import br.com.megaloja.models.Inventory;
import br.com.megaloja.repositories.InventoryRepository;
import br.com.megaloja.specifications.InventorySpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Transactional
    public InventoryResponse create(CreateInventoryRequest request) {
        Inventory inventory = inventoryMapper.toEntity(request);
        inventory = inventoryRepository.save(inventory);
        return inventoryMapper.toResponse(inventory);
    }

    public InventoryResponse findById(Long id) {
        Inventory inventory = findEntityById(id);
        return inventoryMapper.toResponse(inventory);
    }

    public Page<InventoryResponse> findAll(InventoryFilter filter, Pageable pageable) {
        return inventoryRepository.findAll(InventorySpecification.withFilters(filter), pageable)
                .map(inventoryMapper::toResponse);
    }

    public InventoryResponse findByStoreAndProduct(Long storeId, Long productId) {
        Inventory inventory = findEntityByStoreIdAndProductId(storeId, productId);
        return inventoryMapper.toResponse(inventory);
    }

    @Transactional
    public InventoryResponse updateStock(Long storeId, Long productId, UpdateInventoryRequest request) {
        Inventory inventory = findEntityByStoreIdAndProductId(storeId, productId);
        inventoryMapper.updateFromDto(request, inventory);
        inventory = inventoryRepository.save(inventory);
        return inventoryMapper.toResponse(inventory);
    }

    @Transactional
    public void delete(Long id) {
        Inventory inventory = findEntityById(id);
        inventoryRepository.delete(inventory);
    }

    protected Inventory findEntityById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado com id: " + id));
    }

    protected Inventory findEntityByStoreIdAndProductId(Long storeId, Long productId) {
        return inventoryRepository.findByStoreIdAndProductId(storeId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estoque não encontrado para loja " + storeId + " e produto " + productId));
    }
}
