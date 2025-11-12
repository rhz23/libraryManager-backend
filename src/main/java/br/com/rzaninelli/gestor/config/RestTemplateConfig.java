package br.com.rzaninelli.gestor.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@Slf4j
public class RestTemplateConfig {

    @Value("${google.books.api.timeout:5000}")
    private int timeout;

    @Bean
    public RestTemplate restTemplate() {

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setConnectionRequestTimeout(timeout);

        RestTemplate restTemplate = new RestTemplate(factory);

        // 👇 Adiciona suporte a JSON
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        // 👇 Interceptor para log das requisições
        restTemplate.getInterceptors().add((request, body, execution) -> {
            log.debug("HTTP Request: {} {}", request.getMethod(), request.getURI());
            ClientHttpResponse response = execution.execute(request, body);
            log.debug("HTTP Response: {}", response.getStatusCode());
            return response;
        });

        return restTemplate;
    }
}
