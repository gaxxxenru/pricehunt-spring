package ru.pricehunt.pleerru.service.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.pricehunt.pleerru.dto.ProductDTO;

@Slf4j
@Component
@RequiredArgsConstructor
public class Producer {
    @Value("${topic.name}")
    private String productTopic;

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public String sendMessage(ProductDTO product) throws JsonProcessingException {
        String productAsMessage = objectMapper.writeValueAsString(product);
        kafkaTemplate.send(productTopic, productAsMessage);

        log.info("Product sent: " + productAsMessage);

        return "Message sent successfully";
    }
}
