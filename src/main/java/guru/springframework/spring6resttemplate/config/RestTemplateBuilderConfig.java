package guru.springframework.spring6resttemplate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateBuilderConfig {

    @Bean
    RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer restTemplateBuilderConfigurer,
                                            @Value("${rest.template.rootUrl}") String rootUrl,
                                            @Value("${rest.template.user}") String user,
                                            @Value("${rest.template.password}") String password) {
        return restTemplateBuilderConfigurer.configure(new RestTemplateBuilder())
                .uriTemplateHandler(new DefaultUriBuilderFactory(rootUrl))
                .basicAuthentication(user, password);
    }
}
