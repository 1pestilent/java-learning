package com.example.demo.producer;


import com.example.demo.dto.UserDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserEventProducer {
    private final KafkaTemplate<String, UserDto> kafkaTemplate;

    public UserEventProducer(KafkaTemplate<String, UserDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserCreated(UserDto user) {
        kafkaTemplate.send("new-users", user);
    }

    public void sendUserDeleted(UserDto user) {
        kafkaTemplate.send("deleted-users", user);
    }
}
