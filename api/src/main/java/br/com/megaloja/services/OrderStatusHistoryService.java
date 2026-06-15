package br.com.megaloja.services;

import br.com.megaloja.dtos.OrderStatusHistoryResponse;
import br.com.megaloja.exceptions.ResourceNotFoundException;
import br.com.megaloja.mappers.OrderStatusHistoryMapper;
import br.com.megaloja.models.Order;
import br.com.megaloja.models.OrderStatusHistory;
import br.com.megaloja.models.enums.OrderStatus;
import br.com.megaloja.repositories.OrderStatusHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderStatusHistoryService {

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Autowired
    private OrderStatusHistoryMapper orderStatusHistoryMapper;

    @Transactional
    public OrderStatusHistory create(Order order, OrderStatus status) {
        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(status)
                .description("Status alterado para " + status.name())
                .build();
        return orderStatusHistoryRepository.save(history);
    }

    public Page<OrderStatusHistoryResponse> findByOrder(Long orderId, Pageable pageable) {
        Page<OrderStatusHistory> historyPage = orderStatusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(orderId, pageable);
        if (historyPage.isEmpty()) {
            throw new ResourceNotFoundException("Histórico não encontrado para o pedido: " + orderId);
        }
        return historyPage.map(orderStatusHistoryMapper::toResponse);
    }
}
