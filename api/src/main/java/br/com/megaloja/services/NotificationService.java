package br.com.megaloja.services;

import br.com.megaloja.dtos.NotificationResponse;
import br.com.megaloja.exceptions.ResourceNotFoundException;
import br.com.megaloja.filters.NotificationFilter;
import br.com.megaloja.mappers.NotificationMapper;
import br.com.megaloja.models.Notification;
import br.com.megaloja.repositories.NotificationRepository;
import br.com.megaloja.specifications.NotificationSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    public NotificationResponse findById(Long id) {
        Notification notification = findEntityById(id);
        return notificationMapper.toResponse(notification);
    }

    public Page<NotificationResponse> findAll(NotificationFilter filter, Pageable pageable) {
        return notificationRepository.findAll(NotificationSpecification.withFilters(filter), pageable)
                .map(notificationMapper::toResponse);
    }

    @Transactional
    public NotificationResponse markAsRead(Long id) {
        Notification notification = findEntityById(id);
        notification.setIsRead(true);
        notification = notificationRepository.save(notification);
        return notificationMapper.toResponse(notification);
    }

    @Transactional
    public void delete(Long id) {
        Notification notification = findEntityById(id);
        notificationRepository.delete(notification);
    }

    protected Notification findEntityById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada com id: " + id));
    }
}
