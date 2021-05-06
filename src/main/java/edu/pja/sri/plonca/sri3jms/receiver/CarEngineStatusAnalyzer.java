package edu.pja.sri.plonca.sri3jms.receiver;

import com.jayway.jsonpath.JsonPath;
import edu.pja.sri.plonca.sri3jms.carDimens.carDimens;
import edu.pja.sri.plonca.sri3jms.config.JmsConfig;
import edu.pja.sri.plonca.sri3jms.helpers.JsonReader;
import edu.pja.sri.plonca.sri3jms.model.GeneralMessage;
import edu.pja.sri.plonca.sri3jms.model.CarStatusMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CarEngineStatusAnalyzer {

    private final JmsTemplate jmsTemplate;

    public void sendMessageToDriver(GeneralMessage message) {
        jmsTemplate.convertAndSend(JmsConfig.QUEUE_COCKPIT_ANALYZER, message);
    }

    public void sendMessageToTeam(GeneralMessage message) {
        jmsTemplate.convertAndSend(JmsConfig.QUEUE_TEAM_DESKTOP, message);
    }

    private GeneralMessage createMessage(ArrayList<String> content) {
        return GeneralMessage.builder()
                .id(GeneralMessage.nextId())
                .createdAt(LocalDateTime.now())
                .message("Sent from CarEngineStatusAnalyzer: " + content)
                .build();
    }

    @SneakyThrows
    private Map<String, ArrayList<String>> analyzeMessage(CarStatusMessage message) {

        Object engineStatusValuesMap = JsonReader.readJsonFile(carDimens.LIMITS_FILE_PATH);
        ArrayList<String> warningsArray = new ArrayList<>();
        ArrayList<String> errorsArray = new ArrayList<>();
        Map<String, Double> statusMap = message.getStatusMap();
        for (Map.Entry<String, Double> pair : statusMap.entrySet()) {
            String dimenName = pair.getKey();
            Double dimenValue = pair.getValue();
            ArrayList<Double> upperBounds = JsonPath.read(engineStatusValuesMap, String.format("$.%s.%s", dimenName, carDimens.UPPER_LIMIT_NAME));
            ArrayList<Double> lowerBounds = JsonPath.read(engineStatusValuesMap, String.format("$.%s.%s", dimenName, carDimens.LOWER_LIMIT_NAME));
            int dangerLevel = assessDangerLevel(dimenValue, upperBounds, lowerBounds);
            if (dangerLevel == 1)
                errorsArray.add(String.format("Danger: %s at %f", dimenName, dimenValue));
            if (dangerLevel == -1)
                warningsArray.add(String.format("Warning: %s at %f", dimenName, dimenValue));
        }

        Map<String, ArrayList<String>> warningsErrorsMap = new HashMap<>();
        warningsErrorsMap.put("warnings", warningsArray);
        warningsErrorsMap.put("errors", errorsArray);
        return warningsErrorsMap;
    }

    private int assessDangerLevel(Double value, ArrayList<Double> upperBounds, ArrayList<Double> lowerBounds) {
        // -1 -> warning; 0 -> normal; 1 -> error
        if (value > upperBounds.get(0)) return 1;
        if (value > upperBounds.get(1)) return -1;
        if (value < lowerBounds.get(0)) return 1;
        if (value < lowerBounds.get(1)) return -1;
        return 0;
    }

    @JmsListener(destination = JmsConfig.TOPIC_CAR_ENGINE_STATUS, containerFactory =
            "topicConnectionFactory")
    public void receiveMessage(@Payload CarStatusMessage convertedMessage,
                               @Headers MessageHeaders messageHeaders,
                               Message message) {
        Map<String, ArrayList<String>> analysisResult = analyzeMessage(convertedMessage);

        ArrayList<String> warningsList = analysisResult.get("warnings");
        ArrayList<String> errorsList = analysisResult.get("errors");

        if (warningsList.toArray().length > 0) {
            GeneralMessage messageWarnings = createMessage(warningsList);
            sendMessageToDriver(messageWarnings);
        }
        if (errorsList.toArray().length > 0) {
            GeneralMessage messageErrors = createMessage(errorsList);
            sendMessageToDriver(messageErrors);
            sendMessageToTeam(messageErrors);
        }
    }

}
