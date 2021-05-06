package edu.pja.sri.plonca.sri3jms.receiver;

import edu.pja.sri.plonca.sri3jms.carDimens.carDimens;
import edu.pja.sri.plonca.sri3jms.config.JmsConfig;
import edu.pja.sri.plonca.sri3jms.helpers.JsonReader;
import edu.pja.sri.plonca.sri3jms.model.AnalyzerMessage;
import edu.pja.sri.plonca.sri3jms.model.CarStatusMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class CarEngineStatusAnalyzer {

    private final JmsTemplate jmsTemplate;
//    private final Object carLimits;

    {
        try {
            final Object carLimits = JsonReader.readJsonFile(carDimens.LIMITS_FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        //  dla każdego wymiaru określić czy jest ostrzeżenie czy zagrożenie
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

        // TODO: metoda wywołana tylko, gdy analyzeMessage nie zwróci nulla.
//        sendMessage(String.valueOf(int_random));
    }

}
