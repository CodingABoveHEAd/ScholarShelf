package com.niloy.scholarshelf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC configuration.
 * Book images are now hosted on Cloudinary and served directly via Cloudinary URLs,
 * so no local /uploads/** resource handler is needed.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // Add any additional MVC configuration here if needed.
}
