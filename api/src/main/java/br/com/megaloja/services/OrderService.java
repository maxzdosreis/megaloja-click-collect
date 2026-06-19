package br.com.megaloja.services;

import br.com.megaloja.dtos.CreateOrderRequest;
import br.com.megaloja.dtos.OrderResponse;
import br.com.megaloja.dtos.UpdateOrderStatusRequest;
import br.com.megaloja.exceptions.BusinessException;
import br.com.megaloja.exceptions.ResourceNotFoundException;
import br.com.megaloja.filters.OrderFilter;
import br.com.megaloja.mappers.OrderMapper;
import br.com.megaloja.models.Inventory;
import br.com.megaloja.models.Order;
import br.com.megaloja.models.OrderItem;
import br.com.megaloja.models.enums.OrderStatus;
import br.com.megaloja.repositories.InventoryRepository;
import br.com.megaloja.repositories.NotificationRepository;
import br.com.megaloja.repositories.OrderRepository;
import br.com.megaloja.repositories.OrderStatusHistoryRepository;
import br.com.megaloja.specifications.OrderSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private OrderStatusHistoryService orderStatusHistoryService;

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        Order order = orderMapper.toEntity(request);

        validateStockAndReserve(request.storeId(), order.getItems());
        BigDecimal total = calculateTotal(order.getItems());

        order.setStatus(OrderStatus.PENDING);
        order.setPickupCode(generatePickupCode());
        order.setTotalAmount(total);

        order = orderRepository.save(order);

        orderStatusHistoryService.create(order, OrderStatus.PENDING);

        return orderMapper.toResponse(order);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = findEntityById(id);

        order.setStatus(request.status());
        order = orderRepository.save(order);

        orderStatusHistoryService.create(order, request.status());

        return orderMapper.toResponse(order);
    }

    public OrderResponse findById(Long id) {
        Order order = findEntityById(id);
        return orderMapper.toResponse(order);
    }

    public Page<OrderResponse> findAll(OrderFilter filter, Pageable pageable) {
        return orderRepository.findAll(OrderSpecification.withFilters(filter), pageable)
                .map(orderMapper::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        Order order = findEntityById(id);
        orderStatusHistoryRepository.deleteByOrderId(id);
        notificationRepository.deleteByOrderId(id);
        orderRepository.delete(order);
    }

    protected Order findEntityById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com id: " + id));
    }

    private void validateStockAndReserve(Long storeId, List<OrderItem> items) {
        for (OrderItem item : items) {
            Inventory inventory = inventoryRepository.findByStoreIdAndProductId(storeId, item.getProduct().getId())
                    .orElseThrow(() -> new BusinessException(
                            "Produto " + item.getProduct().getId() + " não disponível nesta loja"));

            int available = inventory.getQuantity() - inventory.getReservedQuantity();
            if (available < item.getQuantity()) {
                throw new BusinessException(
                        "Estoque insuficiente para o produto " + item.getProduct().getId()
                                + ". Disponível: " + available + ", solicitado: " + item.getQuantity());
            }

            inventory.setReservedQuantity(inventory.getReservedQuantity() + item.getQuantity());
            inventoryRepository.save(inventory);
        }
    }

    private BigDecimal calculateTotal(List<OrderItem> items) {
        return items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String generatePickupCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
