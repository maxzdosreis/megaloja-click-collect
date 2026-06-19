package br.com.megaloja.controllers;

import br.com.megaloja.controllers.docs.PickupAuthorizationControllerDocs;
import br.com.megaloja.dtos.CreatePickupAuthorizationRequest;
import br.com.megaloja.dtos.PickupAuthorizationResponse;
import br.com.megaloja.services.PickupAuthorizationService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pickup-authorizations")
@RequiredArgsConstructor
public class PickupAuthorizationController implements PickupAuthorizationControllerDocs {

    private final PickupAuthorizationService pickupAuthorizationService;

    @PostMapping
    public ResponseEntity<PickupAuthorizationResponse> create(@RequestBody @Valid CreatePickupAuthorizationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pickupAuthorizationService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PickupAuthorizationResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(pickupAuthorizationService.findById(id));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Page<PickupAuthorizationResponse>> findByOrder(@PathVariable Long orderId,
                                                                         @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(pickupAuthorizationService.findByOrder(orderId, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pickupAuthorizationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
