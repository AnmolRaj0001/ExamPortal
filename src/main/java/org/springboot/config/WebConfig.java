// Path: src/main/java/org/springboot/config/WebConfig.java
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
            .allowedOriginPatterns("http://localhost:3000")  // ✅ must be specific
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true); // ✅ you are using cookies or frontend auth
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // This maps requests starting with /uploads/profile_photos/
        // to the file system path where your images are stored.
        // It's important to use "file:" prefix for file system paths.
        // Ensure this path is correct relative to your Spring Boot application's root directory
        // or provide an absolute path if necessary (e.g., "file:/path/to/your/project/uploads/profile_photos/").
        registry.addResourceHandler("/" + UPLOAD_DIR + "**") // URL path pattern
                .addResourceLocations("file:" + UPLOAD_DIR); // File system path
    }
    
    
}