package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class EchoService {

    @Value("${hello}")
    private String hello;

    @Autowired
    RestTemplate restTemplate;

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                //.setConnectTimeout(30)
                //.setReadTimeout(100)
                .build();
    }

    String getEcho() {
        return restTemplate.getForObject(URI.create(hello), String.class);
    }

}
