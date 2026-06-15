package br.com.megaloja.services;

import br.com.megaloja.dtos.CreatePickupAuthorizationRequest;
import br.com.megaloja.dtos.PickupAuthorizationResponse;
import br.com.megaloja.exceptions.ResourceNotFoundException;
import br.com.megaloja.mappers.PickupAuthorizationMapper;
import br.com.megaloja.models.PickupAuthorization;
import br.com.megaloja.repositories.PickupAuthorizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PickupAuthorizationService {

    @Autowired
    private PickupAuthorizationRepository pickupAuthorizationRepository;

    @Autowired
    private PickupAuthorizationMapper pickupAuthorizationMapper;

    @Transactional
    public PickupAuthorizationResponse create(CreatePickupAuthorizationRequest request) {
        PickupAuthorization authorization = pickupAuthorizationMapper.toEntity(request);
        authorization = pickupAuthorizationRepository.save(authorization);
        return pickupAuthorizationMapper.toResponse(authorization);
    }

    public PickupAuthorizationResponse findById(Long id) {
        PickupAuthorization authorization = findEntityById(id);
        return pickupAuthorizationMapper.toResponse(authorization);
    }

    public Page<PickupAuthorizationResponse> findByOrder(Long orderId, Pageable pageable) {
        Page<PickupAuthorization> authorizations = pickupAuthorizationRepository.findByOrderId(orderId, pageable);
        return authorizations.map(pickupAuthorizationMapper::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        PickupAuthorization authorization = findEntityById(id);
        pickupAuthorizationRepository.delete(authorization);
    }

    protected PickupAuthorization findEntityById(Long id) {
        return pickupAuthorizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autorização não encontrada com id: " + id));
    }
}
