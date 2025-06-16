package com.hcmus.mela.shared.configuration;

import com.hcmus.mela.token.interceptor.AiUsageInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private AiUsageInterceptor aiUsageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(aiUsageInterceptor)
                .addPathPatterns("/api/chatbot/conversations/**")
                .addPathPatterns("/api/chatbot/lectures/**")
                .addPathPatterns("/api/chatbot/questions/**");
    }
}
