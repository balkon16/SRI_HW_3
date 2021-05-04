package edu.pja.sri.plonca.sri3jms.receiver;

import edu.pja.sri.plonca.sri3jms.config.JmsConfig;
import edu.pja.sri.plonca.sri3jms.model.AnalyzerMessage;
import edu.pja.sri.plonca.sri3jms.model.CarStatusMessage;
import edu.pja.sri.plonca.sri3jms.producer.CockpitAnalyzerQueueProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class CarEngineStatusAnalyzer {

    private final JmsTemplate jmsTemplate;

    public void sendMessage(String messageContent) {
        AnalyzerMessage message = AnalyzerMessage.builder()
                .id(AnalyzerMessage.nextId())
                .createdAt(LocalDateTime.now())
                .message("Sent from CarEngineStatusAnalyzer: " + messageContent)
                .build();
        jmsTemplate.convertAndSend(JmsConfig.QUEUE_COCKPIT_ANALYZER, message);

    }

    private String analyzeMessage(CarStatusMessage message) {
        // TODO: dodać logikę, która będzie zwracała wartość inną niż null, gdy pojawi się ostrzeżenie lub zagrożenie.
        String result = "Analyzed message " + message.getId() + ".";
        return result;
    }

    @JmsListener(destination = JmsConfig.TOPIC_CAR_ENGINE_STATUS, containerFactory =
            "topicConnectionFactory")
    public void receiveMessage(@Payload CarStatusMessage convertedMessage,
                               @Headers MessageHeaders messageHeaders,
                               Message message) {
        System.out.println("Analyzer received a message: " + convertedMessage);
        String analyzedMessage = analyzeMessage(convertedMessage);
        // debug
        Random rand = new Random(); //instance of random class
        int upperbound = 25;
        //generate random values from 0-24
        int int_random = rand.nextInt(upperbound);
        // debug
        sendMessage(String.valueOf(int_random));
    }

}
