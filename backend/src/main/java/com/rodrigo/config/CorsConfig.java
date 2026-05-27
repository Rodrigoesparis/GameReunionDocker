package com.rodrigo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//Configuracion para evitar bloqueos en web 
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String uploadPath = System.getProperty("user.dir").replace("\\", "/") + "/uploads/";
    System.out.println(">>> UPLOAD PATH: " + uploadPath);
    registry.addResourceHandler("/uploads/avatars/**")
            .addResourceLocations("file:///" + uploadPath + "avatars/");
    registry.addResourceHandler("/uploads/groups/**")
            .addResourceLocations("file:///" + uploadPath + "groups/");
}
}