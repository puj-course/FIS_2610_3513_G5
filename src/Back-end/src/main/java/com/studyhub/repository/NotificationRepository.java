package com.studyhub.repository;

import com.studyhub.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /** Todas las notificaciones del usuario, más recientes primero */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    /** Solo las NO_LEIDAS, para el contador del badge */
    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);
}
