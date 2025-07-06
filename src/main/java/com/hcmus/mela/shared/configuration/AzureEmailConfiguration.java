package com.hcmus.mela.shared.configuration;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Set;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "azure")
public class AzureEmailConfiguration {

    private Communication communication;
    private Email email;

    @Setter
    @Getter
    public static class Communication {
        private String connectionString;
    }

    @Setter
    @Getter
    public static class Email {
        private String senderAddress;
    }

    @Bean
    public EmailClient emailClient() {
        return new EmailClientBuilder()
                .connectionString(communication.getConnectionString())
                .buildClient();
    }

    @Bean
    public TemplateEngine emailTemplateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(emailTemplateResolver());
        return templateEngine;
    }

    private ITemplateResolver emailTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(1);
        templateResolver.setResolvablePatterns(Set.of("email-*"));
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        return templateResolver;
    }
}
