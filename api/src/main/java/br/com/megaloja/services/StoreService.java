package br.com.megaloja.services;

import br.com.megaloja.dtos.CreateStoreRequest;
import br.com.megaloja.dtos.StoreResponse;
import br.com.megaloja.dtos.UpdateStoreRequest;
import br.com.megaloja.exceptions.ResourceNotFoundException;
import br.com.megaloja.filters.StoreFilter;
import br.com.megaloja.mappers.StoreMapper;
import br.com.megaloja.models.Store;
import br.com.megaloja.repositories.StoreRepository;
import br.com.megaloja.specifications.StoreSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StoreMapper storeMapper;

    @Transactional
    public StoreResponse create(CreateStoreRequest request) {
        Store store = storeMapper.toEntity(request);
        store = storeRepository.save(store);
        return storeMapper.toResponse(store);
    }

    @Transactional
    public StoreResponse update(Long id, UpdateStoreRequest request) {
        Store store = findEntityById(id);
        storeMapper.updateFromDto(request, store);
        store = storeRepository.save(store);
        return storeMapper.toResponse(store);
    }

    public StoreResponse findById(Long id) {
        Store store = findEntityById(id);
        return storeMapper.toResponse(store);
    }

    public Page<StoreResponse> findAll(StoreFilter filter, Pageable pageable) {
        return storeRepository.findAll(StoreSpecification.withFilters(filter), pageable)
                .map(storeMapper::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        Store store = findEntityById(id);
        storeRepository.delete(store);
    }

    protected Store findEntityById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loja não encontrada com id: " + id));
    }
}
