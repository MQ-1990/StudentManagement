package vn.edu.hcmut.cse.adse.lab;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Chuyển hướng tự động từ Root (/) sang (/students)
        registry.addRedirectViewController("/", "/students");
    }
}
