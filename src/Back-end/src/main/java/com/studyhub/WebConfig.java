package com.studyhub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Configuración de recursos estáticos.
 *
 * Expone la carpeta local de uploads como recurso HTTP accesible,
 * de modo que las fotos guardadas en disco sean servidas directamente
 * por Spring Boot en la ruta /uploads/fotos-perfil/{archivo}.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads/fotos-perfil}")
    private String uploadDir;

    /**
     * Mapea las peticiones GET /uploads/fotos-perfil/**
     * a la carpeta física en disco donde se guardan las imágenes.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String rutaAbsoluta = Paths.get(uploadDir).toAbsolutePath().toUri().toString();

        registry.addResourceHandler("/uploads/fotos-perfil/**")
                .addResourceLocations(rutaAbsoluta);
    }
}