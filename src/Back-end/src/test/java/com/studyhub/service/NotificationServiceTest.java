package com.studyhub.service;

import com.studyhub.model.Notification;
import com.studyhub.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepo;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void publicar_guardaNotificacionYEnviaSSE() {
        // Arrange
        Notification n = new Notification();
        // n.setId(100L); // No existe setter para ID en el modelo
        when(notificationRepo.save(any(Notification.class))).thenReturn(n);

        // Act
        Notification result = notificationService.publicar(1L, "TEST", "Mensaje", "ALTA", "/url");

        // Assert
        assertNotNull(result);
        verify(notificationRepo).save(any(Notification.class));
    }

    @Test
    void suscribir_creaEmitter() {
        SseEmitter emitter = notificationService.suscribir(1L);
        assertNotNull(emitter);
    }
}
