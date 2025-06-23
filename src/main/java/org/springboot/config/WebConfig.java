package org.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Define the upload directory. This should match the UPLOAD_DIR in UserProfileService.java
    private static final String UPLOAD_DIR = "uploads/profile_photos/";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                
                .allowedOriginPatterns(
                    "http://localhost:3000",                  
                    "https://examportal-w6lh.onrender.com",  
                    "https://your-react-frontend-on-hostinger.com" 
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        registry.addResourceHandler("/" + UPLOAD_DIR + "**") 
                .addResourceLocations("file:" + UPLOAD_DIR); 
    }
}