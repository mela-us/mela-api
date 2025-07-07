package com.hcmus.mela.token.interceptor;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class AiUsageInterceptorConfig implements WebMvcConfigurer {

    private AiUsageInterceptor aiUsageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(aiUsageInterceptor)
                .addPathPatterns("/api/chatbot/conversations/**")
                .addPathPatterns("/api/chatbot/lectures/**")
                .addPathPatterns("/api/chatbot/questions/**");
    }
}
