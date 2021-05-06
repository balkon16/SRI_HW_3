package edu.pja.sri.plonca.sri3jms.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.pja.sri.plonca.sri3jms.config.JmsConfig;
import edu.pja.sri.plonca.sri3jms.model.GeneralMessage;
import edu.pja.sri.plonca.sri3jms.model.PitStopRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class CockpitPitStopRequestProducer {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;


    @Scheduled(fixedRate = 2000)
    public void sendAndReceive() throws JMSException, JsonProcessingException {
        PitStopRequestMessage message = PitStopRequestMessage.builder()
                .id(PitStopRequestMessage.nextId())
                .createdAt(LocalDateTime.now())
                .ETASeconds(new Random().nextInt(360) + 10)
                .build();
        TextMessage responseMessage = (TextMessage) jmsTemplate.sendAndReceive(
                JmsConfig.QUEUE_PITSTOP_REQUEST, session -> {
                    TextMessage plainMessage = session.createTextMessage();
                    try {
                        plainMessage.setText(objectMapper.writeValueAsString(message));
                        plainMessage.setStringProperty("_type",
                                PitStopRequestMessage.class.getName());
                        return plainMessage;
                    } catch (JsonProcessingException e) {
                        throw new JMSException("conversion to json failed: " +
                                e.getMessage());
                    }
                });
        String responseText = responseMessage.getText();
        GeneralMessage responseConverted = objectMapper.readValue(responseText,
                GeneralMessage.class);
        System.out.println("CockpitPitStopRequestProducer.sendAndReceive got response: "
                + responseText + "\n\tStatus of your request: " + responseConverted.getMessage());
    }

}
